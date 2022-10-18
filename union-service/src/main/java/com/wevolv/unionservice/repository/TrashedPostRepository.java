package com.wevolv.unionservice.repository;


import com.wevolv.unionservice.model.TrashedPost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrashedPostRepository extends MongoRepository<TrashedPost, String> {
}
