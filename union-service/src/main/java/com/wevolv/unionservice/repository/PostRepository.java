package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    List<Post> findAllByTopicId(String topicId);

    Post findByIdAndKeycloakId(String postId, String keycloakId);

    Page<Post> findAllByTopicId(String topicId, PageRequest paging);
}
