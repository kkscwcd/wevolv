package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.DeckLocations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckLocationsRepository extends MongoRepository<DeckLocations, String> {
    Optional<DeckLocations> findByKeycloakId(String keycloakId);
}
