package com.auth.testlogin.integration.user.service.impl;

import com.auth.testlogin.config.GenericApiResponse;
import com.auth.testlogin.integration.user.modal.User;
import com.auth.testlogin.integration.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${services.user-service.url}")
    private String USER_SERVICE;

    private final RestTemplate restTemplate;

    public UserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        User user = new User();
        try {
            Optional<GenericApiResponse> foundUser = Optional.ofNullable(restTemplate.exchange(
                    "" + USER_SERVICE + "/email/{email}",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    GenericApiResponse.class,
                    email
            ).getBody());
            foundUser.ifPresent(u -> {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) u.getResponse();
                user.setPhoneNumber(map.get("phoneNumber"));
            });
            return ((foundUser.isEmpty()) ? Optional.empty() : Optional.of(user));
        }catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to User service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to User service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }

    }
}
