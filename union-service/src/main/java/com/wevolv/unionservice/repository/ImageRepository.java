package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Image;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ImageRepository extends MongoRepository<Image, String> {
}
