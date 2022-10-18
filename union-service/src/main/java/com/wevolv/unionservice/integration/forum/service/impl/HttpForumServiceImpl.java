package com.wevolv.unionservice.integration.forum.service.impl;

import com.wevolv.unionservice.integration.forum.service.ForumService;
import com.wevolv.unionservice.model.*;
import com.wevolv.unionservice.model.dto.PostDto;
import com.wevolv.unionservice.model.dto.TopicDto;
import com.wevolv.unionservice.model.dto.TopicToForumRequest;
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
import java.util.List;
import java.util.Optional;

@Slf4j
public class HttpForumServiceImpl implements ForumService {

    @Value("${services.forum-service.url}")
    private String FORUM_SERVICE;

    private final RestTemplate restTemplate;

    public HttpForumServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void createPostWithTopicId(String keycloakId, PostDto postDto, String topicId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(postDto, httpHeaders);

        try {
            restTemplate.exchange(
               FORUM_SERVICE + "/createPost/" + topicId + "/" + keycloakId,
                    HttpMethod.POST,
                    requestEntity,
                    GenericApiResponse.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Forum service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Forum service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public Topic publishTopicWithAllPosts(UserDataContext udc, Topic topic, List<Post> posts) {
        var httpHeaders = getHttpHeaders(udc);
        var topicRequest = new TopicToForumRequest(topic, posts);
        HttpEntity<Object> requestEntity = new HttpEntity<>(topicRequest, httpHeaders);
        try {
            return restTemplate.exchange(
                    FORUM_SERVICE + "/topic/union/publish",
                    HttpMethod.POST,
                    requestEntity,
                    Topic.class
            ).getBody();
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Forum service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Forum service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }

    }

    @Override
    public Optional<Topic> getTopicById(String topicId, UserDataContext udc) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        try {
            return Optional.ofNullable(restTemplate.exchange(
                    FORUM_SERVICE + "/topic/union/" + topicId,
                    HttpMethod.GET,
                    requestEntity,
                    Topic.class
            ).getBody());
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Forum service: " +
                    "with statusCode: %s and message: %s", e.getMessage(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Forum service " +
                    "with statusCode: %s and message: %s", e.getMessage(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public void deleteTopic(String topicId, UserDataContext udc) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        try {
            restTemplate.exchange(
                    FORUM_SERVICE + "/topic/delete" + topicId,
                    HttpMethod.GET,
                    requestEntity,
                    Topic.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Forum service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Forum service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public Topic updateTopic(String topicId, UserDataContext udc) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(httpHeaders);
        try {
            return restTemplate.exchange(
                    FORUM_SERVICE + "/topic/update" + topicId,
                    HttpMethod.POST,
                    requestEntity,
                    Topic.class
            ).getBody();
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Forum service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Forum service " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        }
    }

    @Override
    public void addPostToPublishedTopic(UserDataContext udc, String topicId, PostDto postDto) {
        var httpHeaders = getHttpHeaders(udc);
        HttpEntity<Object> requestEntity = new HttpEntity<>(postDto, httpHeaders);
        try {
            restTemplate.exchange(
                FORUM_SERVICE + "/create/post/" + topicId,
                HttpMethod.POST,
                requestEntity,
                Topic.class
            );
        } catch (HttpClientErrorException e) {
            log.error(String.format("HttpClientErrorException caught while getting data from Forum service: " +
                    "with statusCode: %s and responseBody: %s", e.getResponseBodyAsString(), e.getStatusCode().value()));
            throw e;
        } catch (HttpServerErrorException e) {
            log.error(String.format("HttpServerErrorException caught while getting data from Forum service " +
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


}
