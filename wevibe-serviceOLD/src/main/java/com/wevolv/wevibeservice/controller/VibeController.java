package com.wevolv.wevibeservice.controller;


import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.domain.LocationSelector;
import com.wevolv.wevibeservice.domain.VibeSelector;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import com.wevolv.wevibeservice.domain.model.dto.LocationTagFilterDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeTagFilterDto;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import com.wevolv.wevibeservice.service.VibeService;
import com.wevolv.wevibeservice.util.TokenDecoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Stream;

import static com.wevolv.wevibeservice.util.TokenDecoder.getUserIdFromToken;

@RestController
@RequestMapping(value = "/vibe")
public class VibeController {

    private final VibeService vibeService;

    public VibeController(VibeService vibeService) {
        this.vibeService = vibeService;
    }


    @PostMapping("/save")
    public ResponseEntity<GenericApiResponse> saveVibe(@RequestBody VibeDto vibeDto, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var savedVibe = vibeService.saveVibe(vibeDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(savedVibe)
                .message(String.format("New vibe with id %s was added.", savedVibe.getId()))
                .build());
    }

    @GetMapping(value = "/{vibeId}")
    public ResponseEntity<GenericApiResponse> getVibeById(@PathVariable String vibeId, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var vibe = vibeService.getVibeById(vibeId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(vibe)
                .message(String.format("Vibe %s", vibeId))
                .build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<GenericApiResponse> getAllVibes(HttpServletRequest request,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "3") int size,
                                                              @RequestParam VibeSelector vibeSelector){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        GenericApiResponse apiResponse = GenericApiResponse.builder().build();

        var paging = PageRequest.of(page, size);
        if(vibeSelector.name().equals(VibeSelector.FAVOURITES.name())) {
            var locations = vibeService.getAllFavouriteVibes(paging, keycloakId);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locations);
            apiResponse.setMessage("All favourite vibes");
        }

        if(vibeSelector.name().equals(VibeSelector.USER_VIBE.name())) {
            var locations = vibeService.getAllUserVibes(paging, keycloakId);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locations);
            apiResponse.setMessage("All user vibes");
        }

        if(vibeSelector.name().equals(VibeSelector.USER_VIBE.name())) {
          /*  var locations = vibeService.getAllInvitedToVibes(paging, keycloakId);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locations);
            apiResponse.setMessage("All user vibes");*/
        }


        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/saveUnsave/favorites/{vibeId}")
    public ResponseEntity<GenericApiResponse> saveUnsavedVibeToFavorites(@PathVariable String vibeId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        vibeService.saveUnsavedVibeToFavorites(vibeId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Vibe %s was added to favorites.", vibeId))
                .build());
    }

    @PostMapping("/edit/{vibeId}")
    public ResponseEntity<GenericApiResponse> editVibe(@PathVariable String vibeId, @RequestBody VibeDto vibeDto,
                                                       HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var savedVibe = vibeService.editVibe(vibeId, vibeDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(savedVibe)
                .message(String.format("New vibe with id %s was added.", savedVibe.getId()))
                .build());
    }

    @GetMapping(value = "/list/friends/{vibeId}")
    public ResponseEntity<GenericApiResponse> getListOfFriends(@PathVariable String vibeId, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var listOfFriends = vibeService.getListOfFriends(vibeId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(listOfFriends)
                .message("Friends list")
                .build());
    }

    @DeleteMapping ("/delete/{vibeId}")
    public ResponseEntity<GenericApiResponse> deleteVibe(@PathVariable String vibeId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        vibeService.deleteVibe(vibeId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Vibe %s is deleted.", vibeId))
                .build());
    }

    @PostMapping("/get/vibes/nearby")
    public List<Vibe> getVibesCoordinatesNearby(@RequestBody AthleteCoordinatesDTO athleteCoordinatesDTO,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "3") int size,
                                                HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return vibeService.getVibesCoordinatesNearby(athleteCoordinatesDTO, keycloakId, paging);
    }


    @PostMapping(value = "/filter")
    public Stream<Vibe> filterLocationsByTags(@RequestBody VibeTagFilterDto vibeTagFilterDto,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return vibeService.filterByTags(vibeTagFilterDto.getVibeTags(), paging, keycloakId);
    }
}
