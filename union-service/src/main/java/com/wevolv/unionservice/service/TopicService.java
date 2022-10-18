package com.wevolv.unionservice.service;

import com.wevolv.unionservice.model.Topic;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.TopicDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface TopicService {

    Topic createNewTopic(String keycloakId, TopicDto topicDto);

    void deleteTopic(UserDataContext userDataContext, String topicId);

    Topic updateTopic(TopicDto topicDto, UserDataContext userDataContext, String topicId);

    Map<String, Object> getAllTopics(PageRequest page);

    Topic getTopicById(String keycloakId, String topicId);

    List<Topic> searchTopicByTitle(String title);

    void publishTopicToForum(UserDataContext udc, String topicDto);
}
