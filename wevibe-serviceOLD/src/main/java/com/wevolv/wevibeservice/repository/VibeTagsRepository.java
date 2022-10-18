package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.VibeTags;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VibeTagsRepository extends MongoRepository<VibeTags, String> {
}
