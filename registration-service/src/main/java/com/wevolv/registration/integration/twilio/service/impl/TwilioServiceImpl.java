package com.wevolv.registration.integration.twilio.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wevolv.registration.model.GenericApiResponse;
import com.wevolv.registration.integration.twilio.service.TwilioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwilioServiceImpl implements TwilioService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${services.twilio-service.url}")
    private String TWILIO_SERVICE;

    public TwilioServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String sendMailActivateAccount(String email, String userId) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("email", email);
        root.put("userId", userId);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);

        try{
            GenericApiResponse twilioResponse = restTemplate.exchange(
                    TWILIO_SERVICE + "/send/email/activate/account/{email}/{userId}",
                    HttpMethod.POST,
                    requestEntity,
                    GenericApiResponse.class,
                    email,
                    userId
            ).getBody();
            return((twilioResponse == null) ? null : twilioResponse.getResponse().toString());
        }catch (HttpStatusCodeException e){
            if(e.getStatusCode().is4xxClientError()) {
                throw HttpClientErrorException.create(
                        e.getStatusCode(),
                        e.getStatusText(),
                        Optional.ofNullable(e.getResponseHeaders()).orElseGet(HttpHeaders::new),
                        e.getResponseBodyAsString().getBytes(),
                        null);
            } else if(e.getStatusCode().is5xxServerError()){
                throw HttpServerErrorException.create(
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

}
