package com.wevolv.calendarservice.integration.profile.service.impl;

import com.wevolv.calendarservice.integration.profile.service.ProfileService;
import com.wevolv.calendarservice.model.GenericApiResponse;
import com.wevolv.calendarservice.model.ProfileShortInfo;
import com.wevolv.calendarservice.model.dto.ListOfProfileShortInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpProfileServiceImpl implements ProfileService {


    @Value("${services.profile-service.url}")
    private String PROFILE_SERVICE;

    private final RestTemplate restTemplate;

    public HttpProfileServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId) {

        ProfileShortInfo profileShortInfo = new ProfileShortInfo();

        try {
            Optional<GenericApiResponse> foundProfile = Optional.ofNullable(restTemplate.exchange(
                    PROFILE_SERVICE + "/short/info/" + keycloakId,
//                    "http://localhost:8087/profile-service/short/info/" + keycloakId,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    GenericApiResponse.class,
                    keycloakId
            ).getBody());
            foundProfile.ifPresent(u -> {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) u.getResponse();
                profileShortInfo.setProfileId(map.get("profileId"));
                profileShortInfo.setImage(map.get("image"));
                profileShortInfo.setFirstName(map.get("firstName"));
                profileShortInfo.setLastName(map.get("lastName"));
            });
            return ((foundProfile.isEmpty()) ? Optional.empty() : Optional.of(profileShortInfo));
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Profile service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Profile service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public List<ProfileShortInfo> getListOfShortProfileInfo(List<String> receiverKeycloakId) {
        var listOfProfileShortInfoDto = new ListOfProfileShortInfoDto();
        listOfProfileShortInfoDto.setKeycloakId(receiverKeycloakId);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(listOfProfileShortInfoDto, httpHeaders);
        try {
            return restTemplate.exchange(
                    PROFILE_SERVICE + "/short/info/list",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<ProfileShortInfo>>() {}
            ).getBody();

        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Profile service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Profile service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }
}
