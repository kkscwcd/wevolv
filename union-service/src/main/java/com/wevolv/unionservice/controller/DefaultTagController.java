package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.DefaultTags;
import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.model.dto.TagsDto;
import com.wevolv.unionservice.service.DefaultTagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class DefaultTagController {

    private final DefaultTagService defaultTagService;

    public DefaultTagController(DefaultTagService defaultTagService) {
        this.defaultTagService = defaultTagService;
    }

    @PostMapping(value = "/create/tags/list")
    public List<DefaultTags> createTagList(@RequestBody TagsDto tagsDto) {
        return defaultTagService.saveListOfTags(tagsDto);
    }

    @GetMapping(value = "/get/tags/list")
    public ResponseEntity<GenericApiResponse> getTagsList(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var list = defaultTagService.getTagsList(keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(list)
                .message("Tags list")
                .build());
    }
}
