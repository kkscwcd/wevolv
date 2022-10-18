package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Tags;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TagsRepository extends MongoRepository<Tags, String> {
}
