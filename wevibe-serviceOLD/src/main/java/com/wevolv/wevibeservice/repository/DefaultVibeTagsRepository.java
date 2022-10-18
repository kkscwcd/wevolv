package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.DefaultTagsVibe;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultVibeTagsRepository extends MongoRepository<DefaultTagsVibe, String> {
}
