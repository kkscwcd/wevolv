package com.wevolv.unionservice.integration.calendar.service.impl;

import com.wevolv.unionservice.integration.calendar.service.CalendarService;
import com.wevolv.unionservice.model.Event;
import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.ImageDto;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.UnionEventDto;
import com.wevolv.unionservice.model.dto.UnionEventResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpCalendarServiceImpl implements CalendarService {

    @Value("${services.calendar-service.url}")
    private String CALENDAR_SERVICE;

    private final RestTemplate restTemplate;

    public HttpCalendarServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Event createEvent(UserDataContext udc, UnionEventDto unionEventDto) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(unionEventDto, httpHeaders);

        try {
            return restTemplate.exchange(
                    CALENDAR_SERVICE + "/create/union/event/",
                    HttpMethod.POST,
                    requestEntity,
                    Event.class
            ).getBody();

        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Calendar service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Profile service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    private HttpHeaders getHttpHeaders(UserDataContext userDataContext) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", userDataContext.accessToken);
        return httpHeaders;
    }

    private HttpHeaders getHttpHeadersMultipartFile(UserDataContext userDataContext) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.set("Authorization", userDataContext.accessToken);
        return httpHeaders;
    }

    @Override
    public void deleteEvent(UserDataContext udc, String eventId) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);

        try {
            Optional<GenericApiResponse> event = Optional.ofNullable(restTemplate.exchange(
                    CALENDAR_SERVICE + "/delete/union/event/" + eventId,
                    HttpMethod.DELETE,
                    requestEntity,
                    GenericApiResponse.class
            ).getBody());
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while sending data to User service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while sending data to User service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public Event updateEvent(UnionEventDto unionEventDto, UserDataContext udc, String eventId) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(unionEventDto, httpHeaders);

        try {
            Optional<Event> event = Optional.ofNullable(restTemplate.exchange(
                    CALENDAR_SERVICE + "/update/union/event/" + eventId,
                    HttpMethod.POST,
                    requestEntity,
                    Event.class
            ).getBody());
            return event.get();
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
    public List<Event> getAllEvents(UserDataContext udc) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);

        try {
            var event = Optional.ofNullable(restTemplate.exchange(
                    CALENDAR_SERVICE + "/all/union/events",
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<List<Event>>() {}
            ).getBody());
            return ((event.isEmpty()) ? null : event.get());

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
    public Event getEvent(UserDataContext udc, String eventId) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);

        try {
            Optional<Event> event = Optional.ofNullable(restTemplate.exchange(
                    CALENDAR_SERVICE + "/get/union/event/" + eventId,
                    HttpMethod.GET,
                    requestEntity,
                    Event.class
            ).getBody());
            return event.get();
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
    public Event saveImageEvent(MultipartFile multipartFile, String eventId, UserDataContext udc) {
        var httpHeaders = getHttpHeadersMultipartFile(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(multipartFile, httpHeaders);

        try {
            Optional<Event> event = Optional.ofNullable(restTemplate.exchange(
                    CALENDAR_SERVICE + "/add/image/" + eventId,
                    HttpMethod.POST,
                    requestEntity,
                    Event.class
            ).getBody());
            return event.get();
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Calendar service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Calendar service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public void deleteImageEvent(ImageDto imageDto, String eventId, UserDataContext udc) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(imageDto, httpHeaders);

        try {
            restTemplate.exchange(
                    CALENDAR_SERVICE + "/delete/image/" + eventId,
                    HttpMethod.POST,
                    requestEntity,
                    Event.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Calendar service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Calendar service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

}
