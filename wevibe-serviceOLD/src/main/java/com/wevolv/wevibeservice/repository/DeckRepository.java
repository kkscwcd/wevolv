package com.wevolv.wevibeservice.repository;

import com.wevolv.wevibeservice.domain.model.Deck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeckRepository extends MongoRepository<Deck, String> {

    void deleteByIdAndKeycloakId(String deckId, String keycloakId);

    Optional<Deck> findByIdAndKeycloakId(String deckId, String keycloakId);
}
