package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.DefaultTags;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DefaultTagsRepository extends MongoRepository<DefaultTags, String> {
}
