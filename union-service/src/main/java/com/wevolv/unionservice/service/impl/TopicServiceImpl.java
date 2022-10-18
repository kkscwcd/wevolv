package com.wevolv.unionservice.service.impl;

import com.wevolv.unionservice.exceptions.NotFoundException;
import com.wevolv.unionservice.integration.forum.service.ForumService;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import com.wevolv.unionservice.model.*;
import com.wevolv.unionservice.model.dto.TopicDto;
import com.wevolv.unionservice.repository.PostRepository;
import com.wevolv.unionservice.repository.TopicRepository;
import com.wevolv.unionservice.service.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.*;

public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final ProfileService profileService;
    private final PostRepository postRepository;
    private final ForumService forumService;

    public TopicServiceImpl(TopicRepository topicRepository, ProfileService profileService, PostRepository postRepository, ForumService forumService) {
        this.topicRepository = topicRepository;
        this.profileService = profileService;
        this.postRepository = postRepository;
        this.forumService = forumService;
    }

    @Override
    public Topic createNewTopic(String keycloakId, TopicDto topicDto) {
        var psi = getProfileShortInfo(keycloakId);
        var author = getAuthor(keycloakId, psi);

        return topicRepository.save(new Topic(UUID.randomUUID().toString(), topicDto.getTitle(),
                author, Instant.now(), 0, false));
    }

    @Override
    public void publishTopicToForum(UserDataContext udc, String topicId) {
        var topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(String.format("Topic %s does not exist", topicId)));
        var posts = postRepository.findAllByTopicId(topicId);

        forumService.publishTopicWithAllPosts(udc, topic, posts);
        topic.setIsPublishedToForum(true);
        topicRepository.save(topic);
    }

    @Override
    public void deleteTopic(UserDataContext udc, String topicId) {
        //TODO talk it trough with Leo there is no need
        // to call profile service to check user??
        //getProfileShortInfo(keycloakId);
        var topic = getExistingUserTopic(udc.keycloakId, topicId);
        var existingPostsTopic = postRepository.findAllByTopicId(topicId);

        var topicPublishedForum = isTopicPublishedOnForum(udc, topicId);
        deleteTopicFromForum(udc, topicPublishedForum);

        //delete all existing post from topic
        postRepository.deleteAll(existingPostsTopic);
        topicRepository.delete(topic);
    }

    @Override
    public Topic updateTopic(TopicDto topicDto, UserDataContext udc, String topicId) {
        var topic = getExistingUserTopic(udc.keycloakId, topicId);
        var topicPublishedForum = isTopicPublishedOnForum(udc, topicId);
        updateForumTopic(udc, topicPublishedForum);
        topic.setTitle(topicDto.getTitle());

        return topicRepository.save(topic);
    }

    @Override
    public Map<String, Object> getAllTopics(PageRequest pageRequest) {
        var topics = topicRepository.findAll(pageRequest);
        return populateMapTopicsResponse(topics);
    }

    @Override
    public Topic getTopicById(String keycloakId, String topicId) {
        return getExistingTopic(topicId);
    }

    @Override
    public List<Topic> searchTopicByTitle(String title) {
        return topicRepository.findByTitle(title);
    }



    private Topic getExistingUserTopic(String keycloakId, String topicId) {
        return topicRepository.findByIdAndAuthor_KeycloakId(topicId, keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("Topic %s doesn't exist", topicId)));
    }

    private Topic getExistingTopic(String topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(String.format("Topic %s doesnt exist", topicId)));
    }

    private Author getAuthor(String keycloakId, ProfileShortInfo psi) {
        return new Author(UUID.randomUUID().toString(), keycloakId, psi.getFirstName(),
                psi.getLastName(), psi.getImage());
    }

    private ProfileShortInfo getProfileShortInfo(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User %s doesnt exist", keycloakId)));
    }

    private void deleteTopicFromForum(UserDataContext udc, Optional<Topic> topicPublishedForum) {
        topicPublishedForum.ifPresent(tpf -> {
            forumService.deleteTopic(tpf.getId(), udc);
        });
    }

    private Optional<Topic> isTopicPublishedOnForum(UserDataContext udc, String topicId) {
        return forumService.getTopicById(topicId, udc);
    }

    private void updateForumTopic(UserDataContext udc, Optional<Topic> topicPublishedForum) {
        topicPublishedForum.ifPresent(tpf -> {
            forumService.updateTopic(tpf.getId(), udc);
        });
    }

    private Map<String, Object> populateMapTopicsResponse(Page<Topic> topics) {
        Map<String, Object> response = new HashMap<>();
        response.put("topics", topics.getContent());
        response.put("currentPage", topics.getNumber());
        response.put("totalItems", topics.getTotalElements());
        response.put("totalPages", topics.getTotalPages());
        response.put("hasPrevious", topics.hasPrevious());
        response.put("hasNext", topics.hasNext());
        return response;
    }

}
