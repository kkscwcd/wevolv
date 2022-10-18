package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.PublishedPosts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PublishedPostsRepository extends MongoRepository<PublishedPosts, String> {
}
