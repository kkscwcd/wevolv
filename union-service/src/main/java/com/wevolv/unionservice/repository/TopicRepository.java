package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends MongoRepository<Topic, String> {
    Optional<Topic> findByIdAndAuthor_KeycloakId(String topicId, String keycloakId);

    @Query(value = "{'title': {$regex : ?0, $options: 'i'}}")
    List<Topic> findByTitle(String title);
}
