package com.wevolv.registration.integration.profile.service.impl;

import com.wevolv.registration.model.GenericApiResponse;
import com.wevolv.registration.integration.profile.model.ProfileInitInfoDto;
import com.wevolv.registration.model.dto.RegistrationRequestDto;
import com.wevolv.registration.integration.profile.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfileServiceImp implements ProfileService {

    @Value("${services.profile-service.url}")
    private String PROFILE_SERVICE;

    private final RestTemplate restTemplate;

    public ProfileServiceImp(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String createInitialUserProfile(RegistrationRequestDto registrationRequestDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        ProfileInitInfoDto profileInitInfoDto = ProfileInitInfoDto.builder()
                .firstName(registrationRequestDto.getFirstName())
                .lastName(registrationRequestDto.getLastName())
                .email(registrationRequestDto.getEmail())
                .keycloakId(registrationRequestDto.getKeycloakId())
                .gender(registrationRequestDto.getGender())
                .build();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(profileInitInfoDto, httpHeaders);
        try {
            Optional<GenericApiResponse> response = Optional.ofNullable(restTemplate.postForObject(
                    "" + PROFILE_SERVICE + "/create",
                    //"http://localhost:8087/profile-service/profile/create/init",
                    httpEntity,
                    GenericApiResponse.class
            ));
            return response.toString();
        }catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to profile service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to profile service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

}
