package com.wevolv.wevibeservice.controller;

import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.domain.model.dto.DeckVibeLocationDto;
import com.wevolv.wevibeservice.domain.model.dto.DeckDto;
import com.wevolv.wevibeservice.service.DeckService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.wevolv.wevibeservice.util.TokenDecoder.getUserIdFromToken;

@RestController
@RequestMapping(value = "/deck")
public class DeckController {

    private final DeckService deckService;

    public DeckController(DeckService deckService) {
        this.deckService = deckService;
    }

    @PostMapping("/create")
    public ResponseEntity<GenericApiResponse> createDeck(@RequestBody DeckDto deckDto, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var savedDeck = deckService.createDeck(deckDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(savedDeck)
                .message(String.format("New deck with deckId %s was added.", savedDeck.getId()))
                .build());
    }

    @PostMapping("/update/{deckId}")
    public ResponseEntity<GenericApiResponse> updateDeck(@PathVariable String deckId, @RequestBody DeckDto deckDto,
                                                         HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var updatedDeck = deckService.updateDeck(deckId, deckDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(updatedDeck)
                .message(String.format("Deck for user with deckId %s was added.", updatedDeck.getId()))
                .build());
    }

    @PostMapping("/pin/{deckId}")
    public ResponseEntity<GenericApiResponse> pinUnPinDeck(@PathVariable String deckId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var pinedDeck = deckService.pinUnPinDeck(deckId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(pinedDeck)
                .message(String.format("Deck for user with deckId %s was added.", pinedDeck.getId()))
                .build());
    }

    @PostMapping("/add/vibe/location/{deckId}")
    public ResponseEntity<GenericApiResponse> addVibeLocationToDeck(@PathVariable String deckId,
                                                                    @RequestBody DeckVibeLocationDto vibeLocationDto,
                                                                    HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var addedVibeLocation = deckService.addVibeLocationDeck(deckId, vibeLocationDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(addedVibeLocation)
                .message(String.format("Added vibe or location to deck with deckId %s.", deckId))
                .build());
    }

    @DeleteMapping("/delete/vibe/location/{deckId}")
    public ResponseEntity<GenericApiResponse> deleteVibeLocationToDeck(@PathVariable String deckId,
                                                                    @RequestBody DeckVibeLocationDto vibeLocationDto,
                                                                    HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var deck = deckService.deleteVibeLocationDeck(deckId, vibeLocationDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(deck)
                .message(String.format("Deleted vibes or locations from deck with deckId %s.", deckId))
                .build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<GenericApiResponse> getAllDecks(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var decks = deckService.getAllDecks(paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(decks)
                .message("All decks")
                .build());
    }

    @GetMapping("/get/{deckId}")
    public ResponseEntity<GenericApiResponse> getById(@PathVariable String deckId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var decks = deckService.getById(deckId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(decks)
                .message("All decks")
                .build());
    }

    @DeleteMapping("/delete/{deckId}")
    public ResponseEntity<GenericApiResponse> deleteDeck(@PathVariable String deckId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        deckService.deleteDeck(deckId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Deck with deckId %s was deleted.", deckId))
                .build());
    }

    @GetMapping("/get/all/deck/vibes/{deckId}")
    public ResponseEntity<GenericApiResponse> getAllDeckVibes(HttpServletRequest request,
                                                          @PathVariable String deckId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var vibes = deckService.getAllDeckVibes(paging, deckId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(vibes)
                .message("All deck vibes")
                .build());
    }

    @GetMapping("/get/all/deck/locations/{deckId}")
    public ResponseEntity<GenericApiResponse> getAllDeckLocations(HttpServletRequest request,
                                                              @PathVariable String deckId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var locations = deckService.getAllDeckLocations(paging, deckId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(locations)
                .message("All deck locations")
                .build());
    }
}
