package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.About;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AboutRepository extends MongoRepository<About, String> {
    Optional<About> findByUnionProvider(String aboutId);
}
