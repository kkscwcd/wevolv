package com.wevolv.registration.integration.apple.service.impl;

import com.wevolv.registration.exception.TokenNotValidException;
import com.wevolv.registration.exception.UserServiceException;
import com.wevolv.registration.model.UserInfoDto;
import com.wevolv.registration.model.dto.RegistrationRequestDto;
import com.wevolv.registration.integration.apple.service.AppleService;
import com.wevolv.registration.integration.keycloak.KeycloakService;
import com.wevolv.registration.model.User;
import com.wevolv.registration.model.dto.TokenDto;
import com.wevolv.registration.service.UserService;
import com.wevolv.registration.util.TokenDecoder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AppleServiceImpl implements AppleService {

    @Value("${keycloak.auth-server-url}")
    private String AUTH_URL;

    @Value("${keycloak.realm}")
    private String REALM;

    private final KeycloakService keycloakService;
    private final UserService userService;
    private final RestTemplate restTemplate;

    public AppleServiceImpl(KeycloakService keycloakService, UserService userService, RestTemplate restTemplate) {
        this.keycloakService = keycloakService;
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @Override
    public TokenDto createUser(String jwt) {

        TokenDto tokenDto = new TokenDto();

        Map<String, Object> data = TokenDecoder.getUserDetailsFromJWT(jwt);

        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        if (data.get("iss").toString().contains("google.com")) {
            registrationRequestDto.setGoogleUserId(data.get("iss").toString());
        } else if (data.get("iss").toString().contains(".apple.com")) {
            registrationRequestDto.setAppleUserId(data.get("iss").toString());
        }
        registrationRequestDto.setEmail(data.get("email").toString());
        registrationRequestDto.setFirstName(data.getOrDefault("given_name", "").toString());
        registrationRequestDto.setLastName(data.getOrDefault("family_name", "").toString());
// Check user in MongoDB, If exist do notihin else create it on first sign in
        Optional<User> user = userService.findUserByEmail(registrationRequestDto.getEmail());
        log.info("AppleServiceImpl->createUser ->findUserByEmail user present {}", user.isPresent());
        if (!user.isPresent()) {
            // Register User in Keycloak

            // If user not in our database , add it in to the database
//             RegistrationRequestDto registrationRequestDto = RegistrationRequestDto.builder()
//                .email(appleRegistrationRequestDto.getEmail())
//                .firstName(appleRegistrationRequestDto.getFirstName())
//                .lastName(appleRegistrationRequestDto.getLastName())
//                .appleUserId(appleRegistrationRequestDto.getAppleUserId())
//                .
//                .build();
            tokenDto= keycloakService.createSocialUserInKeycloak(registrationRequestDto);
            // user= userService.addUserToMongoDB(registrationRequestDto);
        } else {
            try {

                tokenDto = keycloakService.exchange(data.get("email").toString());
                
                /*if (tokenDto != null) {
                    tokenDto.setUserInfo(getUserInfo(tokenDto.getAccessToken()));
                }*/
//                return tokenDto;
            } catch (Exception e) {
                throw new UserServiceException(String.format("Unable to reach user service with exception message %s", e.getMessage()));
            }
        }
        // Decode JWT to get the UserInfo
        
       Map<String,Object> jwtDetails= TokenDecoder.getUserDetailsFromJWT(tokenDto.getAccessToken());
     UserInfoDto uid=  UserInfoDto.builder().sub(jwtDetails.getOrDefault("sub", "").toString())
               .email(jwtDetails.getOrDefault("email", "").toString())
               .email_verified(Boolean.parseBoolean(jwtDetails.getOrDefault("email_verified", false).toString()))
               .name(jwtDetails.getOrDefault("given_name", "").toString()+" "+jwtDetails.getOrDefault("family_name", "").toString())
               .preferred_username(jwtDetails.getOrDefault("preferred_username", "").toString()).build();
               
       tokenDto.setUserInfo(uid);
       
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
