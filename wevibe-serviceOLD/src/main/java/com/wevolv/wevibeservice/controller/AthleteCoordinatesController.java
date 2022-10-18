package com.wevolv.wevibeservice.controller;

import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import com.wevolv.wevibeservice.domain.model.dto.AthleteTagFilterDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeTagFilterDto;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import com.wevolv.wevibeservice.service.AthleteCoordinatesService;
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
@RequestMapping(value = "/athlete")
public class AthleteCoordinatesController {

    public final AthleteCoordinatesService athleteCoordinatesService;


    public AthleteCoordinatesController(AthleteCoordinatesService athleteCoordinatesService) {
        this.athleteCoordinatesService = athleteCoordinatesService;
    }

    @PostMapping("/save/coordinates")
    public ResponseEntity<GenericApiResponse> saveAthletesCoordinates(@RequestBody AthleteCoordinatesDTO athleteCoordinatesDTO, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var athleteCoordinates = athleteCoordinatesService.saveAthletesCoordinates(athleteCoordinatesDTO, keycloakId);
        var apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .build();
        athleteCoordinates.ifPresent(ac -> {
            apiResponse.setResponse(ac);
            apiResponse.setMessage(String.format("Following athlete coordinates for user with keycloakId %s were added to database.", keycloakId));
        });
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get/all/athletes/coordinates")
    public ResponseEntity<GenericApiResponse> getAthletesCoordinates(HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var athleteCoordinates = athleteCoordinatesService.getAllAthletesCoordinates(keycloakId);
        var apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .build();

        athleteCoordinates.ifPresent(ac -> {
            apiResponse.setResponse(ac);
            apiResponse.setMessage(String.format("Athlete coordinates for user with keycloakId %s.", keycloakId));
        });
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/get/athlete/nearby")
    public ResponseEntity<GenericApiResponse> getAthletesCoordinatesNearby(@RequestBody AthleteCoordinatesDTO athleteCoordinatesDTO, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var athleteCoordinates = athleteCoordinatesService.getAthletesCoordinatesNearby(athleteCoordinatesDTO,keycloakId);
        var apiResponse = GenericApiResponse.builder().build();
        athleteCoordinates.ifPresent(ac -> {
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(ac);
            apiResponse.setMessage(String.format("Athlete near user with keycloakId %s.", keycloakId));
        });
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/filter")
    public List<ProfileShortInfo> filterLocationsByTags(@RequestBody AthleteTagFilterDto vibeTagFilterDto,
                                                        HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        TokenDecoder.getUserIdFromToken(jwt);
        return athleteCoordinatesService.filterByTags(vibeTagFilterDto);
    }
}
