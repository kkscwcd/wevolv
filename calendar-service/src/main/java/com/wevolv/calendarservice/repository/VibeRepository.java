package com.wevolv.calendarservice.repository;

import com.wevolv.calendarservice.model.Vibe;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VibeRepository extends MongoRepository<Vibe, String> {
    List<Vibe> findAllByKeycloakId(String keycloakId);
}
