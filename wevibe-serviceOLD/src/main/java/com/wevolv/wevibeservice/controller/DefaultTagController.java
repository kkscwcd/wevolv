package com.wevolv.wevibeservice.controller;

import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.domain.model.dto.LocationDefaultTagsDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeDefaultTagsDto;
import com.wevolv.wevibeservice.service.DefaultTagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.wevolv.wevibeservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class DefaultTagController {

    private final DefaultTagService defaultTagService;

    public DefaultTagController(DefaultTagService defaultTagService) {
        this.defaultTagService = defaultTagService;
    }

    @PostMapping(value = "/create/location/tags/list")
    public ResponseEntity<GenericApiResponse> createLocationTagList(@RequestBody LocationDefaultTagsDto tagsDto) {
        var list = defaultTagService.saveListOfLocationTags(tagsDto);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(list)
                .message("Location tags saved")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/get/location/tags/list")
    public ResponseEntity<GenericApiResponse> getTagsList(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var list = defaultTagService.getListOfLocationTags(keycloakId);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(list)
                .message("Location tags list")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/create/vibe/tags/list")
    public ResponseEntity<GenericApiResponse> createVibeTagList(@RequestBody VibeDefaultTagsDto tagsDto) {
        var list = defaultTagService.saveListOfVibeTags(tagsDto);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(list)
                .message("Vibe tags saved")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/get/vibe/tags/list")
    public ResponseEntity<GenericApiResponse> getVibeTagList(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var list = defaultTagService.getListOfVibeTags(keycloakId);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(list)
                .message("Vibe tags list")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}