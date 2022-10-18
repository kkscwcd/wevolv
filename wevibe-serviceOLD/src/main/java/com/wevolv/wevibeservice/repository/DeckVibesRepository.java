package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.DeckLocations;
import com.wevolv.wevibeservice.domain.model.DeckVibes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckVibesRepository extends MongoRepository<DeckVibes, String> {
    Page<DeckVibes> findAllByKeycloakId(String keycloakId, PageRequest paging);

    Optional<DeckVibes> findByKeycloakId(String keycloakId);
}
