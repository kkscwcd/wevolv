package com.wevolv.unionservice.integration.forum.service;

import com.wevolv.unionservice.model.Post;
import com.wevolv.unionservice.model.Topic;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.PostDto;
import com.wevolv.unionservice.model.dto.TopicDto;

import java.util.List;
import java.util.Optional;

public interface ForumService {
    
    void createPostWithTopicId(String keycloakId, PostDto postDto, String topicId);

    Topic publishTopicWithAllPosts(UserDataContext udc, Topic topic, List<Post> posts);

    Optional<Topic> getTopicById(String topicId, UserDataContext udc);

    void deleteTopic(String id, UserDataContext udc);

    Topic updateTopic(String id, UserDataContext udc);

    void addPostToPublishedTopic(UserDataContext udc, String topicId, PostDto postDto);
}
