package com.wevolv.registration.integration.keycloak.service.impl;

import com.wevolv.registration.exception.*;
import com.wevolv.registration.integration.keycloak.KeycloakService;
import com.wevolv.registration.integration.profile.model.ProfileInitInfoDto;
import com.wevolv.registration.integration.profile.service.ProfileService;
import com.wevolv.registration.integration.twilio.service.TwilioService;
import com.wevolv.registration.model.GenericApiResponse;
import com.wevolv.registration.model.User;
import com.wevolv.registration.model.UserCredentials;
import com.wevolv.registration.model.UserInfoDto;
import com.wevolv.registration.model.dto.RegistrationRequestDto;
import com.wevolv.registration.model.dto.RegistrationResponseDto;
import com.wevolv.registration.model.dto.TokenDto;
import com.wevolv.registration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.core.Response;
import java.util.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final UserService userService;
    private final TwilioService twilioService;
    private final ProfileService profileService;
    private final RestTemplate restTemplate;

    @Value("${keycloak.resource}")
    private String CLIENTID;
    
    @Value("${keycloak.realm}")
    private String REALM;

    @Value("${keycloak.auth-server-url}")
    private String AUTH_URL;

    @Value("${admin.username}")
    private String ADMIN_USERNAME;

    @Value("${admin.password}")
    private String ADMIN_PASSWORD;

    @Value("${services.auth_api.url}")
    private String AUTH_API;
    
    private final String secret="456abc321erwwww3322reddd";
    private final String password="456abc321erwwww3322reddd";

    public KeycloakServiceImpl(UserService userService, TwilioService twilioService, ProfileService profileService,RestTemplate restTemplate) {
        this.userService = userService;
        this.twilioService = twilioService;
        this.profileService = profileService;
        this.restTemplate=restTemplate;
    }

    @Override
    public Optional<UserRepresentation> createUserInKeycloak(RegistrationRequestDto registrationRequestDto) {
        if (registrationRequestDto.getAppleUserId() == null || registrationRequestDto.getGoogleUserId() == null) {
            RegistrationRequestDto.requestValidator(registrationRequestDto);
        }
        UsersResource usersResource = getKeycloakUserResource();
        UserRepresentation userRepresentation = new UserRepresentation();
        //check if user already exists in keycloak database
        List<UserRepresentation> search = findUserInKeycloakByUsername(userRepresentation.getEmail());
        boolean usersExistsKeycloak = search.stream().anyMatch(ur -> ur.getEmail().equals(registrationRequestDto.getEmail()));
        UserRepresentation existingUser = search.stream()
                .filter(eu -> eu.getEmail().equals(registrationRequestDto.getEmail()))
                .findFirst()
                .orElse(null);

        userExists(registrationRequestDto, usersResource, userRepresentation, usersExistsKeycloak, existingUser);
        return Optional.of(userRepresentation);
    }

    private void userExists(RegistrationRequestDto registrationRequestDto, UsersResource usersResource, UserRepresentation userRepresentation, boolean usersExistsKeycloak, UserRepresentation existingUser) {
        if (usersExistsKeycloak) {
            throw new UserAlreadyExistsException(String.format("You are already registered with email %s. Go to login page.", existingUser.getEmail()));
        } else {
            createUserInKeycloakAndUserService(registrationRequestDto, usersResource, userRepresentation);
        }
    }

    private void createUserInKeycloakAndUserService(RegistrationRequestDto registrationRequestDto, UsersResource usersResource, UserRepresentation userRepresentation) {
        Response response;
        Optional<User> userApiResponse;
        String keycloakId = null;

        userRepresentation.setEnabled(false);
        userRepresentation.setEmailVerified(false);
        userRepresentation.setUsername(registrationRequestDto.getEmail());
        userRepresentation.setFirstName(registrationRequestDto.getFirstName());
        userRepresentation.setLastName(registrationRequestDto.getLastName());
        userRepresentation.setEmail(registrationRequestDto.getEmail());
        // create password credential
        List<CredentialRepresentation> credentials = new ArrayList<>();
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(registrationRequestDto.getPassword());
        credentials.add(passwordCred);
        userRepresentation.setCredentials(credentials);
        // Contains device token
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("deviceToken", Arrays.asList(registrationRequestDto.getDeviceToken()));
        userRepresentation.setAttributes(attributes);
        userRepresentation.setGroups(Arrays.asList(registrationRequestDto.getGroup()));
        try {
            response = usersResource.create(userRepresentation);
            keycloakId = CreatedResponseUtil.getCreatedId(response);
            registrationRequestDto.setKeycloakId(keycloakId);
            userApiResponse = userService.addUserToMongoDB(registrationRequestDto);
            userRepresentation.setId(keycloakId);

        } catch (Exception e) {
            usersResource.delete(keycloakId);
            throw new UserServiceException(String.format("Unable to reach user service with exception message %s", e.getMessage()));
        }

        if (response.getStatus() == 201 && userApiResponse.isPresent()) {
            log.info("Created user with keycloakId {} and username {}", keycloakId, userRepresentation.getUsername());

            try {
                //call Twilio Service and send email for verification
                twilioService.sendMailActivateAccount(registrationRequestDto.getEmail(), keycloakId);
            } catch (Exception e) {
                throw new TwilioServiceException(String.format("Unable to reach Twilio service with exception message %s", e.getMessage()));
            }

            try {
                //call Profile Service to create profile
                profileService.createInitialUserProfile(registrationRequestDto);
            } catch (Exception e) {
                throw new ProfileServiceException(String.format("Unable to reach Profile service with exception message %s", e.getMessage()));
            }
        } else {
            log.info("Username = " + userRepresentation.getEmail() + " could not be created in keycloak");
            throw new GeneralException("Username = " + userRepresentation.getEmail() + " could not be created in Keycloak");
        }
    }
    
    @Override
     public TokenDto createSocialUserInKeycloak(RegistrationRequestDto registrationRequestDto) {
        TokenDto tokenDto = new TokenDto();
        Response response;
        Optional<User> userApiResponse;
        String keycloakId = null;
        UsersResource usersResource = getKeycloakUserResource();
        UserRepresentation userRepresentation = new UserRepresentation();
        
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setUsername(registrationRequestDto.getEmail());
        userRepresentation.setFirstName(registrationRequestDto.getFirstName());
        userRepresentation.setLastName(registrationRequestDto.getLastName());
        userRepresentation.setEmail(registrationRequestDto.getEmail());
        // create password credential
        List<CredentialRepresentation> credentials = new ArrayList<>();
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        credentials.add(passwordCred);
        userRepresentation.setCredentials(credentials);
        // Contains device token
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("deviceToken", Arrays.asList(registrationRequestDto.getDeviceToken()));
        userRepresentation.setAttributes(attributes);
       // userRepresentation.setGroups(Arrays.asList(registrationRequestDto.getGroup()));
        try {
            response = usersResource.create(userRepresentation);
            keycloakId = CreatedResponseUtil.getCreatedId(response);
            registrationRequestDto.setKeycloakId(keycloakId);
            userApiResponse = userService.addUserToMongoDB(registrationRequestDto);
            userRepresentation.setId(keycloakId);

        } catch (Exception e) {
            usersResource.delete(keycloakId);
            throw new UserServiceException(String.format("Unable to reach user service with exception message %s", e.getMessage()));
        }

        if (response.getStatus() == 201 && userApiResponse.isPresent()) {
           try{
               tokenDto = exchange(userRepresentation.getUsername());
             /*  if (tokenDto != null) {
                   tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
               }*/
               //call Profile Service to create profile
               try {
                   //call Profile Service to create profile
                   profileService.createInitialUserProfile(registrationRequestDto);
               } catch (Exception e) {
                   throw new ProfileServiceException(String.format("Unable to reach Profile service with exception message %s", e.getMessage()));
               }

               return tokenDto;

           }catch (Exception e) {
            usersResource.delete(keycloakId);
            throw new UserServiceException(String.format("Unable to reach user service with exception message %s", e.getMessage()));
        }
            
        } else {
            log.info("Username = " + userRepresentation.getEmail() + " could not be created in keycloak");
            throw new GeneralException("Username = " + userRepresentation.getEmail() + " could not be created in Keycloak");
        }
       
    }

    @Override
    public List<UserRepresentation> findUserInKeycloakByUsername(String email) {
        return getRealmResource().users().search(email);
    }

    @Override
    public UserResource findUserInKeycloakByUserId(String keycloakUserId) {
        return getRealmResource().users().get(keycloakUserId);
    }

    @Override
    public RegistrationResponseDto userEmailVerification(String keycloakUserId) {
        String userId;
        UserResource existingUser = findUserInKeycloakByUserId(keycloakUserId);
        UserRepresentation userRepresentation = existingUser.toRepresentation();
        if (userRepresentation.isEmailVerified() || userRepresentation.isEnabled()) {
            throw new UserAlreadyExistsException(String.format("User is already verified with email %s and is enabled in service", userRepresentation.getEmail()));
        }
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        try {
            //call User service to activate user
            userId = userService.activateUser(keycloakUserId);
        } catch (Exception e) {
            throw new UserServiceException(String.format("Unable to reach User service with exception message %s", e.getMessage()));
        }
        existingUser.update(userRepresentation);

        return RegistrationResponseDto.builder()
                .keycloakId(keycloakUserId)
                .userId(userId)
                .emailVerified(userRepresentation.isEmailVerified())
                .enabled(userRepresentation.isEnabled())
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .build();
    }

    private UsersResource getKeycloakUserResource() {
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(AUTH_URL)
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(ADMIN_USERNAME).password(ADMIN_PASSWORD)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        RealmResource realmResource = keycloak.realm(REALM);
        return realmResource.users();
    }

    private RealmResource getRealmResource() {
        Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username(ADMIN_USERNAME).password(ADMIN_PASSWORD)
                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        return kc.realm(REALM);

    }

    @Override
    public void addUserToGroup(String keycloakUserId, String groupName) {
        UserResource existingUser = findUserInKeycloakByUserId(keycloakUserId);
        UserRepresentation userRepresentation = existingUser.toRepresentation();
        if (userRepresentation.getGroups() != null) {
            userRepresentation.getGroups().add(groupName);
        } else {
            userRepresentation.setGroups(Arrays.asList(groupName));
        }
        existingUser.update(userRepresentation);
        log.debug("User with keycloakId {} added to group {}", keycloakUserId, groupName);
        //call User service to add user group
        userService.addUserToGroupInMongoDB(keycloakUserId, groupName);
    }

    @Override
    public void removeUserFromGroup(String keycloakUserId, String groupName) {
        UserResource existingUser = findUserInKeycloakByUserId(keycloakUserId);
        UserRepresentation userRepresentation = existingUser.toRepresentation();
        if (userRepresentation.getGroups() != null) {
            userRepresentation.getGroups().remove(groupName);
            existingUser.update(userRepresentation);
            //call User service to add user group
            userService.removeUserFromGroupInMongoDB(keycloakUserId, groupName);
        }
        // In else do nothing as user don't have any group
        log.debug("User with keycloakId {} removed from group {}", keycloakUserId, groupName);
    }
    

    public TokenDto exchange(String userName) throws Exception {
       /* TokenDto tokenDto = new TokenDto();

        HttpHeaders httpHeaders = new HttpHeaders();
        UserCredentials uc = new UserCredentials(password, userName);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(uc, httpHeaders);
        try {
            TokenDto response = restTemplate.postForObject(
                    "" + AUTH_API + "/login",
                    //"http://localhost:8087/profile-service/profile/create/init",
                    httpEntity,
                    TokenDto.class
            );
            return response;
        }catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to profile service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to profile service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }*/

        log.info("Exchange method started.");
          MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", "password");
        mapForm.add("client_id", CLIENTID);
        mapForm.add("username", userName);
        mapForm.add("password", password);
        //mapForm.add("client_secret", secret);

        TokenDto tokenDto = new TokenDto();

        ResponseEntity<Object> response;
        String uri = AUTH_URL + "/realms/" + REALM + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_FORM_URLENCODED)));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(mapForm, headers);

        log.info("Header successfully set.");

        try {
            log.info("Before exchange with Keycloak.");
            response = restTemplate.exchange(uri, HttpMethod.POST, request, Object.class);
            log.info("Exchange succeeded.");
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) response.getBody();
            
            if (map != null) {
                tokenDto.setAccessToken(map.get("access_token").toString());
                tokenDto.setTokenType(map.get("token_type").toString());
                tokenDto.setRefreshToken(map.get("refresh_token").toString());
                tokenDto.setExpires_in(map.get("expires_in").toString());
                tokenDto.setScope(map.get("scope").toString());
            } else {
                return null;
            }
        } catch (Exception e) {

            throw new Exception("Something went wrong, please try again!");
        }
        return tokenDto;
    }

    public UserInfoDto getUserInfo(String token) {

        log.info("Get user info by token.");

        if (token == null) {
            log.error("Token is not present or null.");
            throw new TokenNotValidException("Cannot load user info because token is not present or valid!");
        }

        UserInfoDto userInfoDto = new UserInfoDto();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(token, headers);

        try {
            log.info("Before exchange with Keycloak.");
            ResponseEntity<Object> response = restTemplate.exchange(
                    AUTH_URL + "/realms/" + REALM + "/protocol/openid-connect/userinfo",
                    HttpMethod.GET, entity, Object.class);

            log.info("Success exchange and we get information about user.");

            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) response.getBody();

            if (map != null) {
                userInfoDto.setSub(map.get("sub").toString());
                userInfoDto.setEmail_verified(map.get("email_verified").toString().equalsIgnoreCase("true"));
                userInfoDto.setPreferred_username(map.get("preferred_username").toString());
                userInfoDto.setEmail(map.get("email").toString());
                userInfoDto.setName(map.get("name").toString());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new TokenNotValidException("Please check your login! " + e.getMessage());
        }
        log.info("User info returned.");
        return userInfoDto;
    }
}
