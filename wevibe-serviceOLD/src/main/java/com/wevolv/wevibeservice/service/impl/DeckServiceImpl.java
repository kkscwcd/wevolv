package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.Deck;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.DeckDto;
import com.wevolv.wevibeservice.domain.model.dto.DeckVibeLocationDto;
import com.wevolv.wevibeservice.exception.NotFoundException;
import com.wevolv.wevibeservice.repository.DeckRepository;
import com.wevolv.wevibeservice.repository.LocationRepository;
import com.wevolv.wevibeservice.repository.VibeRepository;
import com.wevolv.wevibeservice.service.DeckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.*;

public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final VibeRepository vibeRepository;
    private final LocationRepository locationRepository;

    public DeckServiceImpl(DeckRepository deckRepository, VibeRepository vibeRepository, LocationRepository locationRepository) {
        this.deckRepository = deckRepository;
        this.vibeRepository = vibeRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public Deck createDeck(DeckDto deckDto, String keycloakId) {
       var myDeck = Deck.builder()
               .id(UUID.randomUUID().toString())
               .keycloakId(keycloakId)
               .name(deckDto.getTitle())
               .description(deckDto.getDescription())
               .coverImage(deckDto.getCoverImage())
               .numbOfVibes(0)
               .numOfLocations(0)
               .numberOfSpots(0)
               .build();
       deckRepository.save(myDeck);

        return myDeck;
    }

    @Override
    public void deleteDeck(String deckId, String keycloakId) {
        deckRepository.deleteByIdAndKeycloakId(deckId, keycloakId);
    }

    @Override
    public Deck updateDeck(String deckId, DeckDto deckDto, String keycloakId) {
        var existingDeck = getUserExistingDeck(deckId, keycloakId);
        existingDeck.setName(deckDto.getTitle());
        existingDeck.setDescription(deckDto.getDescription());
        existingDeck.setCoverImage(deckDto.getCoverImage());
        return deckRepository.save(existingDeck);
    }

    @Override
    public Deck pinUnPinDeck(String deckId, String keycloakId) {
        var existingDeck = getUserExistingDeck(deckId, keycloakId);
        if(existingDeck.isPined()){
            existingDeck.setPined(false);
        } else {
            existingDeck.setPined(true);
        }
        return deckRepository.save(existingDeck);
    }

    @Override
    public Deck addVibeLocationDeck(String deckId, DeckVibeLocationDto vibeLocationDto, String keycloakId) {
        var existingDeck = getUserExistingDeck(deckId, keycloakId);
        Iterable<Vibe>  vibesIterable;
        Iterable<Location> locationsIterable;

        if(vibeLocationDto.getVibeIds() != null){
             vibesIterable = getVibesByIds(vibeLocationDto);
            if(vibeLocationDto.getVibeIds().size() > 0){
                vibesIterable.forEach(v -> {
                    v.setDeckId(deckId);
                    vibeRepository.save(v);
                });
            }
        }

        if(vibeLocationDto.getLocationIds() != null){
            locationsIterable = getLocationsByIds(vibeLocationDto);
            if(vibeLocationDto.getLocationIds().size() > 0){
                locationsIterable.forEach(l -> {
                    l.setDeckId(deckId);
                    locationRepository.save(l);
                });
            }
        }

        var numberOfLocations = locationRepository.findAllByDeckId(deckId).size();
        existingDeck.setNumOfLocations(numberOfLocations);

        var numberOfVibes = vibeRepository.findAllByDeckId(deckId).size();
        existingDeck.setNumbOfVibes(numberOfVibes);

        existingDeck.setNumberOfSpots(numberOfLocations+numberOfVibes);

        return deckRepository.save(existingDeck);
    }

    private Iterable<Location> getLocationsByIds(DeckVibeLocationDto vibeLocationDto) {
        return locationRepository.findAllById(vibeLocationDto.getLocationIds());
    }

    private Iterable<Vibe> getVibesByIds(DeckVibeLocationDto vibeLocationDto) {
        return vibeRepository.findAllById(vibeLocationDto.getVibeIds());
    }

    @Override
    public Deck deleteVibeLocationDeck(String deckId, DeckVibeLocationDto vibeLocationDto, String keycloakId) {
        var existingDeck = getUserExistingDeck(deckId, keycloakId);
        var vibesIterable = getVibesByIds(vibeLocationDto);
        var locationsIterable = getLocationsByIds(vibeLocationDto);

        if(vibeLocationDto.getLocationIds().size() > 0){
            locationsIterable.forEach(l -> {
                l.setDeckId(null);
                locationRepository.save(l);
            });
        }
        if(vibeLocationDto.getVibeIds().size() > 0){
            vibesIterable.forEach(v -> {
                v.setDeckId(null);
                vibeRepository.save(v);
            });
        }

        var numberOfLocations = locationRepository.findAllByDeckId(deckId).size();
        existingDeck.setNumOfLocations(numberOfLocations);

        var numberOfVibes = vibeRepository.findAllByDeckId(deckId).size();
        existingDeck.setNumbOfVibes(numberOfVibes);

        existingDeck.setNumberOfSpots(numberOfLocations+numberOfVibes);

        return deckRepository.save(existingDeck);
    }

    @Override
    public Map<String, Object> getAllDecks(PageRequest paging) {
        var decks = deckRepository.findAll(paging);
        return populateMapResponseLikes(decks);
    }

    @Override
    public Deck getById(String deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new NotFoundException(String.format("Deck with deckId %s doesn't exist", deckId)));
    }

    @Override
    public Map<String, Object> getAllDeckVibes(PageRequest paging, String deckId) {
        var existingDeckVibes = vibeRepository.findAllVibesByDeckId(deckId, paging);
        return populateDeckVibes(existingDeckVibes);
    }

    @Override
    public Map<String, Object> getAllDeckLocations(PageRequest paging, String deckId) {
        var existingDeckLocations = locationRepository.findAllLocationsByDeckId(deckId, paging);
        return populateDeckLocations(existingDeckLocations);
    }

    private Map<String, Object> populateDeckVibes(Page<Vibe> vibes) {
        Map<String, Object> response = new HashMap<>();
        response.put("vibes", vibes.getContent());
        response.put("currentPage", vibes.getNumber());
        response.put("totalItems", vibes.getTotalElements());
        response.put("totalPages", vibes.getTotalPages());
        response.put("hasPrevious", vibes.hasPrevious());
        response.put("hasNext", vibes.hasNext());
        return response;
    }


    private Map<String, Object> populateDeckLocations(Page<Location> locations) {
        Map<String, Object> response = new HashMap<>();
        response.put("locations", locations.getContent());
        response.put("currentPage", locations.getNumber());
        response.put("totalItems", locations.getTotalElements());
        response.put("totalPages", locations.getTotalPages());
        response.put("hasPrevious", locations.hasPrevious());
        response.put("hasNext", locations.hasNext());
        return response;
    }


    private Map<String, Object> populateMapResponseLikes(Page<Deck> decks) {
        Map<String, Object> response = new HashMap<>();
        response.put("decks", decks.getContent());
        response.put("currentPage", decks.getNumber());
        response.put("totalItems", decks.getTotalElements());
        response.put("totalPages", decks.getTotalPages());
        response.put("hasPrevious", decks.hasPrevious());
        response.put("hasNext", decks.hasNext());
        return response;
    }

    private Deck getUserExistingDeck(String deckId, String keycloakId) {
        return deckRepository.findByIdAndKeycloakId(deckId, keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("Deck with deckId %s doesnt exist", deckId)));
    }
}
