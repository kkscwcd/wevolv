package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.DefaultTagsLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultLocationTagsRepository extends MongoRepository<DefaultTagsLocation, String> {
}
