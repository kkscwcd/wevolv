package com.auth.testlogin.service.impl;

import com.auth.testlogin.config.GenericApiResponse;
import com.auth.testlogin.exceptions.NotFoundException;
import com.auth.testlogin.exceptions.TokenNotValidException;
import com.auth.testlogin.exceptions.UserNotVerified;
import com.auth.testlogin.exceptions.WrongUserCredentialsException;
import com.auth.testlogin.integration.user.modal.User;
import com.auth.testlogin.model.UserCredentials;
import com.auth.testlogin.model.dto.*;
import com.auth.testlogin.service.KeyCloakService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * @author Djordje
 * @version 1.0
 */
@Component
@Slf4j
public class KeyCloakServiceImpl implements KeyCloakService {

    @Value("${keycloak.resource}")
    private String CLIENTID;

    @Value("${keycloak.auth-server-url}")
    private String AUTHURL;

    @Value("${keycloak.realm}")
    private String REALM;

    @Value("${admin.username}")
    private String ADMIN_USERNAME;

    @Value("${admin.password}")
    private String ADMIN_PASSWORD;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.twilio-service.url}")
    private String TWILIO_SERVICE;

    @Value("${services.user-service.url}")
    private String USER_SERVICE;



    /**
     * Getting token for particular user and getting user info within response
     *
     * @param userCredentials username and password from body
     */
    public TokenDto getToken(UserCredentials userCredentials) throws Exception {

        log.info("Get token form service layer started.");

        TokenDto tokenDto = new TokenDto();
        MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
        mapForm.add("grant_type", "password");
        mapForm.add("client_id", CLIENTID);
        mapForm.add("username", userCredentials.getUsername());
        mapForm.add("password", userCredentials.getPassword());

        log.info("Map is full and ready to be forwarded for exchange.");

        try{
            tokenDto = exchange(mapForm);
        } catch (WrongUserCredentialsException e){
            if(e.getMessage().contains("Check your email inbox and verify your account!")){
                var foundUser = getUserNumber(userCredentials);
                foundUser.ifPresent(fu -> {
                    throw new UserNotVerified("Check your email inbox to verify your account or " +
                            "resend code to phone number", fu.getPhoneNumber(), fu.getKeycloakId());
                });

            }
        }

        log.info("TokenDto received. Now we are looking for user info.");

        if (tokenDto != null) {
            tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
        }
        return tokenDto;
    }

    private Optional<User> getUserNumber(UserCredentials userCredentials) {
        User user = new User();
        Optional<GenericApiResponse> foundUser = Optional.ofNullable(restTemplate.exchange(
                USER_SERVICE + "/email/{email}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                GenericApiResponse.class,
                userCredentials.getUsername()
        ).getBody());
        foundUser.ifPresent(u -> {
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) u.getResponse();
            user.setPhoneNumber(map.get("phoneNumber"));
            user.setKeycloakId(map.get("keycloakId"));
        });
        return ((foundUser.isEmpty()) ? Optional.empty() : Optional.of(user));
    }

    /**
     * Getting user info: id (sub), email_verified, preferred_username
     *
     * @param token user token form Authorization header
     */
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
                    AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/userinfo",
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

    /**
     * Getting token using refresh token, old token will be used until expiration time
     *
     * @param refreshToken refresh token
     */
    public TokenDto getByRefreshToken(String refreshToken) {

        log.info("Get token by refresh token in service layer started.");

        TokenDto tokenDto;

        try {
            MultiValueMap<String, String> mapForm = new LinkedMultiValueMap<>();
            mapForm.add("client_id", CLIENTID);
            mapForm.add("grant_type", "refresh_token");
            mapForm.add("refresh_token", refreshToken.substring(7));

            log.info("Map is full and ready for exchange.");
            tokenDto = exchange(mapForm);
            log.info("Successful exchange.");
            if (tokenDto != null) {
                log.info("Get user info by new token.");
                tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
            }

        } catch (Exception e) {
            log.error("Refresh token is not valid or present!");
            throw new TokenNotValidException("Refresh token is not present or not valid! Please login again.");
        }
        log.info("New token received and returned.");
        return tokenDto;
    }

    /**
     * logout user using refresh token, and invalidate tokens
     *
     * @param refreshToken refresh token
     */
    public void logoutUser(String refreshToken) {

        log.info("logout user from service layer started.");

        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", CLIENTID);
            requestParams.add("refresh_token", refreshToken.substring(7));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);

            String url = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/logout";

            log.info("Before exchange with Keycloak.");
            restTemplate.postForEntity(url, request, Object.class);
            log.info("Exchange succeeded and user is logged out.");

        } catch (Exception e) {
            log.error("Error while logout user, check refresh token.");
            throw new TokenNotValidException("Error while logout user, provided refresh token is not valid!");
        }
    }

    /**
     * Method resetPasswordFromAdmin, reset password using Admin user
     *
     * @param resetPasswordDto model which contain all fields we need
     * @param jwtToken         form Authorization header
     */
    public void resetPasswordFromAdmin(ResetPasswordDto resetPasswordDto, String jwtToken) {

        log.info("Reset password form service layer started.");

        log.info("Decode token and take userId form it.");
        String userIdFromToken = getUserIdFromToken(jwtToken);

        if (!StringUtils.equals(userIdFromToken, resetPasswordDto.getUserId())) {
            log.error("Id from token and from request are not same!!!");
            throw new TokenNotValidException("You cannot change password for this user!");
        }

        log.info("Getting admin user and build him.");
        UsersResource userResource = getKeycloakUserResource();
        log.info("We received Admin user.");
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(resetPasswordDto.getPassword().trim());

        try {
            log.info("Get user by id and reset password sending CredentialRepresentation object.");
            userResource.get(resetPasswordDto.getUserId()).resetPassword(passwordCred);
            log.info("Password changed in Keycloak now calling USER-SERVICE.");
            HttpHeaders headers = new HttpHeaders();
            UpdatePasswordRequestDto updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                    .password(resetPasswordDto.getPassword())
                    .keycloakId(userIdFromToken)
                    .build();
            headers.setContentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)));
            HttpEntity<UpdatePasswordRequestDto> request = new HttpEntity<>(updatePasswordRequestDto, headers);
            restTemplate.exchange(
                    USER_SERVICE + "/user/updatePassword",
                    HttpMethod.POST,
                    request,
                    GenericApiResponse.class).getBody();
            log.info("Password updated.");

        } catch (Exception e) {

            log.error("Error while restarting password. Check Keycloak or USER-SERVICE!");
            throw new WrongUserCredentialsException("Something went wrong while changing password!");
        }
    }

    @Override
    public String forgotPasswordReset(ForgotPasswordDto forgotPasswordDto) throws Exception {
        String responseMessage = "";
        //Check if Token and exists in database and get userId for that token
        try {
            GenericApiResponse twilioResponse =
                    restTemplate.getForObject(TWILIO_SERVICE + "/confirmTokenResetPassword?token=" + forgotPasswordDto.getToken(), GenericApiResponse.class);
            String keycloakId = twilioResponse.getResponse().toString();
            if (keycloakId == null || keycloakId.equals("")) {
                throw new Exception("Bad response from Twilio Service!");
            }
            log.info("Reset password in service layer started.");
            log.info("Getting Keycloak admin user.");
            UsersResource userResource = getKeycloakUserResource();
            log.info("We received Admin user.");
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(forgotPasswordDto.getPassword().trim());

            log.info("Get user by id and reset password sending CredentialRepresentation object.");
            userResource.get(keycloakId).resetPassword(passwordCred);

            HttpHeaders headers = new HttpHeaders();
            UpdatePasswordRequestDto updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                    .password(forgotPasswordDto.getPassword())
                    .keycloakId(keycloakId)
                    .build();
            headers.setContentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)));
            HttpEntity<UpdatePasswordRequestDto> request = new HttpEntity<>(updatePasswordRequestDto, headers);
            GenericApiResponse genericApiResponse = restTemplate.exchange(
                    USER_SERVICE + "/user/updatePassword",
                    HttpMethod.POST,
                    request,
                    GenericApiResponse.class).getBody();

            if (genericApiResponse.getResponse() != null) {

                responseMessage = "Password changed for user";
            } else {
                responseMessage = "Password not changed!";
            }

            return responseMessage;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Method exchange, exchanges data with Keycloak and gets Token response
     *
     * @param mapForm function parameter
     */
    private TokenDto exchange(MultiValueMap<String, String> mapForm) throws Exception {

        log.info("Exchange method started.");

        TokenDto tokenDto = new TokenDto();

        ResponseEntity<Object> response;
        String uri = AUTHURL + "/realms/" + REALM + "/protocol/openid-connect/token";
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
        } catch (HttpClientErrorException.BadRequest e) {

            throw new WrongUserCredentialsException("Check your email inbox and verify your account!");

        } catch (HttpClientErrorException.Unauthorized e) {

            throw new WrongUserCredentialsException("Wrong credentials!");

        } catch (Exception e) {

            throw new Exception("Something went wrong, please try again!");
        }
        return tokenDto;
    }

    @Override
    public GroupRepresentation findGroupByPath(final String path) {
        final RealmResource realmResource = getRealmResource();
        return Optional.of(realmResource.getGroupByPath(path)).orElseThrow(() -> new NotFoundException("group can not be found!"));
    }

    @Override
    public List<GroupRepresentation> getRealmGroups() {
        final RealmResource realmResource = getRealmResource();
        return realmResource.groups().groups();
    }

    @Override
    public void joinGroup(String groupId, String userId) {
        var userResource = getRealmResource().users().get(userId);
        userResource.joinGroup(groupId);
    }

    /**
     * Method getKeycloakUserResource, Create Admin from KeycloakBuilder and get User resource
     */
    private UsersResource getKeycloakUserResource() {
        final RealmResource realmResource = getRealmResource();
        log.info("Admin received.");
        return realmResource.users();
    }

    private RealmResource getRealmResource() {
        log.info("Get Admin user from Keycloak started.");
        final Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(AUTHURL)
                .realm("master")
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .clientId("admin-cli")
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        return kc.realm(REALM);
    }

    /**
     * Method getUserIdFromToken, Taking token and decode it to take userId
     */
    private String getUserIdFromToken(String token) {

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        final ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mapFromString = new HashMap<>();
        try {
            mapFromString = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("Exception launched while trying to parse String to Map.", e);
        }

        return (String) mapFromString.get("sub");

    }

}
