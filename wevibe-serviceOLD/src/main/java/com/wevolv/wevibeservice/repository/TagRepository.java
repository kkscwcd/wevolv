package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.LocationTags;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<LocationTags, String> {

    Optional<LocationTags> findByIdAndKeycloakId(String tagId, String keycloakId);
}
