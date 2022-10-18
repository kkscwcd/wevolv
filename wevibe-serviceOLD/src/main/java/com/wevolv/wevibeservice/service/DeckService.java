package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.Deck;
import com.wevolv.wevibeservice.domain.model.dto.DeckVibeLocationDto;
import com.wevolv.wevibeservice.domain.model.dto.DeckDto;
import org.springframework.data.domain.PageRequest;

import java.util.Map;

public interface DeckService {
    Deck createDeck(DeckDto deckDto, String keycloakId);

    void deleteDeck(String deckId, String keycloakId);

    Deck updateDeck(String deckId, DeckDto deckDto, String keycloakId);

    Deck pinUnPinDeck(String deckId, String keycloakId);

    Deck addVibeLocationDeck(String deckId, DeckVibeLocationDto vibeLocationDto, String keycloakId);

    Deck deleteVibeLocationDeck(String deckId, DeckVibeLocationDto vibeLocationDto, String keycloakId);

    Map<String, Object> getAllDecks(PageRequest paging);

    Deck getById(String deckId);

    Map<String, Object> getAllDeckVibes(PageRequest paging, String deckId);

    Map<String, Object> getAllDeckLocations(PageRequest paging, String deckId);

}
