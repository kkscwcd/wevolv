package com.wevolv.wevibeservice.integration.profile.service.impl;

import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.AthleteTagFilterDto;
import com.wevolv.wevibeservice.domain.model.dto.ListOfProfileShortInfoDto;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Value("${services.profile-service.url}")
    private String PROFILE_SERVICE;

    private final RestTemplate restTemplate;

    public ProfileServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId) {
        ProfileShortInfo profileShortInfo = new ProfileShortInfo();

        try {
            Optional<GenericApiResponse> foundProfile = Optional.ofNullable(restTemplate.exchange(
                    PROFILE_SERVICE + "/short/info/" + keycloakId,
                  // "http://localhost:8087/profile-service/profile/short/info/" + keycloakId,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    GenericApiResponse.class,
                    keycloakId
            ).getBody());
            foundProfile.ifPresent(u -> {
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) u.getResponse();
                profileShortInfo.setProfileId(map.get("profileId"));
                //profileShortInfo.setCountry(map.get("country"));
                profileShortInfo.setImage(map.get("image"));
                profileShortInfo.setCurrentPosition(map.get("currentPosition"));
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

    @Override
    public List<ProfileShortInfo> filterAthletes(AthleteTagFilterDto vibeTagFilterDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(vibeTagFilterDto, httpHeaders);
        try {
            return restTemplate.exchange(
                    PROFILE_SERVICE + "/filter/athletes",
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
