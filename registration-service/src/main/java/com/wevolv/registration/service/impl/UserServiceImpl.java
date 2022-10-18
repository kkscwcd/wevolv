package com.wevolv.registration.service.impl;

import com.wevolv.registration.model.GenericApiResponse;
import com.wevolv.registration.model.PasswordStorage;
import com.wevolv.registration.model.User;
import com.wevolv.registration.model.UserApiResponse;
import com.wevolv.registration.model.dto.PhoneNumberEditDto;
import com.wevolv.registration.model.dto.RegistrationRequestDto;
import com.wevolv.registration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final RestTemplate restTemplate;

    @Value("${services.user-service.url}")
    private String USER_SERVICE;

    public UserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        User user = new User();
        try {
            Optional<UserApiResponse> foundUser = Optional.ofNullable(restTemplate.getForEntity(
                    "" + USER_SERVICE + "/email/{email}",
                    /*"http://localhost:8086/user-service/user/email/{email}",*/
                    UserApiResponse.class,
                    email
            ).getBody());

            if (foundUser.isPresent()) {
                user =foundUser.get().getResponse();
            }

//            foundUser.ifPresent(u -> {
//                @SuppressWarnings("unchecked")
//                      
//                LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) u.getResponse();
//                user.setId(map.get("id"));
//                user.setEmail(map.get("email"));
//                user.setUsername(map.get("username"));
//                user.setEmailVerified(Boolean.valueOf(map.get("emailVerified")));
//                user.setEnabled((Boolean)(map.get("enabled")));
//                user.setKeycloakId(map.get("keycloakId"));
//                user.setAppleUserId(map.get("appleUserId"));
//                user.setGoogleUserId(map.get("googleUserId"));
//                user.setFirstName(map.get("firstName"));
//                user.setLastName(map.get("lastName"));
//                user.setDeviceToken(map.get("deviceToken"));
//            });
            return ((foundUser.isEmpty()) ? Optional.empty() : Optional.of(user));
        } catch (HttpStatusCodeException e) {
            log.error("Error getting user data for user with email: {}  with statusCode: {}", email, e.getStatusCode());
            String formattedErrorMessage = String.format("Error getting user data for user with email: %s. With exceptionMessage: %s", email, e.getMessage());
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                return Optional.empty();
            }
            if (e.getStatusCode().is4xxClientError()) {
                throw HttpClientErrorException.create(formattedErrorMessage,
                        e.getStatusCode(),
                        e.getStatusText(),
                        Optional.ofNullable(e.getResponseHeaders()).orElseGet(HttpHeaders::new),
                        e.getResponseBodyAsString().getBytes(),
                        null);
            } else if (e.getStatusCode().is5xxServerError()) {
                throw HttpServerErrorException.create(formattedErrorMessage,
                        e.getStatusCode(),
                        e.getStatusText(),
                        Optional.ofNullable(e.getResponseHeaders()).orElseGet(HttpHeaders::new),
                        e.getResponseBodyAsString().getBytes(),
                        null);
            } else {
                throw e;
            }
        } 
    }

    @Override
    public Optional<User> addUserToMongoDB(RegistrationRequestDto registrationRequestDto) {
        User user = new User();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (registrationRequestDto.getAppleUserId() == null || registrationRequestDto.getGoogleUserId() == null) {
            //String hashedPassword = hashUserPassword(registrationRequestDto.getPassword());
            //registrationRequestDto.setPassword(hashedPassword);
        }
        HttpEntity<Object> requestEntity = new HttpEntity<>(registrationRequestDto, httpHeaders);
        try {
            Optional<UserApiResponse> savedUser = Optional.ofNullable(restTemplate.postForEntity(
                    //"http://localhost:8086/user-service/user/",
                    "" + USER_SERVICE + "/add/",
                    requestEntity,
                    UserApiResponse.class
            ).getBody());
            
              if (savedUser.isPresent()) {
                user = savedUser.get().getResponse();
              }

//            savedUser.ifPresent(u -> {
//                @SuppressWarnings("unchecked")
//                LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) u.getResponse();
//                user.setId(map.get("id"));
//                user.setEmail(map.get("email"));
//                user.setUsername(map.get("username"));
//                user.setEmailVerified(true);
//                user.setEnabled(true);
//                user.setKeycloakId(map.get("keycloakId"));
//                user.setAppleUserId(map.get("appleUserId"));
//                user.setGoogleUserId(map.get("googleUserId"));
//                user.setFirstName(map.get("firstName"));
//                user.setLastName(map.get("lastName"));
//                user.setPhoneNumber(map.get("phoneNumber"));
//                user.setDeviceToken(map.get("deviceToken"));
//            });
            return (savedUser.isEmpty() ? Optional.empty() : Optional.of(user));
        } catch (HttpStatusCodeException e) {
            log.error("Error saving user with email: {}  with statusCode: {}", registrationRequestDto.getEmail(), e.getStatusCode());
            String formattedErrorMessage = String.format("Error saving user with email: %s  with statusCode: %s", registrationRequestDto.getEmail(), e.getStatusCode());
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                return Optional.empty();
            }
            if (e.getStatusCode().is4xxClientError()) {
                throw HttpClientErrorException.create(formattedErrorMessage,
                        e.getStatusCode(),
                        e.getStatusText(),
                        Optional.ofNullable(e.getResponseHeaders()).orElseGet(HttpHeaders::new),
                        e.getResponseBodyAsString().getBytes(),
                        null);
            } else if (e.getStatusCode().is5xxServerError()) {
                throw HttpServerErrorException.create(formattedErrorMessage,
                        e.getStatusCode(),
                        e.getStatusText(),
                        Optional.ofNullable(e.getResponseHeaders()).orElseGet(HttpHeaders::new),
                        e.getResponseBodyAsString().getBytes(),
                        null);
            } else {
                throw e;
            }
        }
    }

    @Override
    public String activateUser(String keycloakId) {
        try {
            Optional<GenericApiResponse> response = Optional.ofNullable(restTemplate.postForObject(
                    "" + USER_SERVICE + "/activate/user/" + keycloakId,
                    /*"http://localhost:8081/user/activateUser/{keycloakId}",*/
                    keycloakId,
                    GenericApiResponse.class
            ));
            return response.toString();
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to User service: "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to User service "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public void editPhoneNumber(PhoneNumberEditDto phoneNumberEditDto, String keycloakId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(phoneNumberEditDto, httpHeaders);
        try {
            restTemplate.exchange(
                    "" + USER_SERVICE + "/edit/phoneNumber/" + keycloakId,
                    HttpMethod.POST,
                    requestEntity,
                    GenericApiResponse.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to User service: "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to User service "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }

    }

    private String hashUserPassword(String password) {
        try {
            return PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            log.warn("Unable to hash password due to {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void addUserToGroupInMongoDB(String keycloakId, String groupName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.exchange(
                    "" + USER_SERVICE + "/user/group/add/" + keycloakId + "/" + groupName,
                    HttpMethod.POST,
                    requestEntity,
                    GenericApiResponse.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to User service: "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to User service "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public void removeUserFromGroupInMongoDB(String keycloakId, String groupName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.exchange(
                    "" + USER_SERVICE + "/user/group/remove/" + keycloakId + "/" + groupName,
                    HttpMethod.POST,
                    requestEntity,
                    GenericApiResponse.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to User service: "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to User service "
                    + "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }
}
