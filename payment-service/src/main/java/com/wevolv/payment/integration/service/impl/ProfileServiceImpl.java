package com.wevolv.payment.integration.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wevolv.payment.integration.model.ProfileShortInfo;
import com.wevolv.payment.integration.service.ProfileService;
import com.wevolv.payment.model.dto.GenericApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Value("${services.profile-service.url}")
    private String PROFILE_SERVICE;

    private static final String SHORT_INFO_URL = "/short/info/";

    public ProfileServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId) {

        try {
            var response =  Optional.ofNullable(restTemplate.exchange(
                    String.format("%s%s%s", PROFILE_SERVICE, SHORT_INFO_URL, keycloakId),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    GenericApiResponse.class,
                    keycloakId
            ).getBody());

            return response.map(x -> objectMapper.convertValue(x.getResponse(), ProfileShortInfo.class));

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