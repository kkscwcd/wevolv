package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.service.ImageService;
import com.wevolv.unionservice.util.TokenDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<GenericApiResponse> uploadImage(MultipartFile multipartFile,
                                                          HttpServletRequest request) throws IOException {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

//        var image = imageService.uploadImage(multipartFile, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
//                .response(image)
                .message("Image uploaded.")
                .build());
    }

    @GetMapping(value = "/get/image/{imageId}")
    public ResponseEntity<GenericApiResponse> getImageById(@PathVariable String imageId,
                                                           HttpServletRequest request) {

        String jwt = request.getHeader("Authorization");
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

//        var image = imageService.getImageById(imageId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
//                .response(image)
                .message(String.format("Image by imageId: %s.", imageId))
                .build());
    }
}
