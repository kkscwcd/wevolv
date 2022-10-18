package com.wevolv.unionservice.repository;

import com.wevolv.unionservice.model.Podcast;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PodcastRepository extends MongoRepository<Podcast, String> {
}
