package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.LocationTags;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationTagsRepository extends MongoRepository<LocationTags , String> {
    List<LocationTags> findByLocationId(String locationId);
}
