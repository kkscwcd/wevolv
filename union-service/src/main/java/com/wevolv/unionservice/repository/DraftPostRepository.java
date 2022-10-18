package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.DraftPost;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DraftPostRepository extends MongoRepository<DraftPost, String> {
}
