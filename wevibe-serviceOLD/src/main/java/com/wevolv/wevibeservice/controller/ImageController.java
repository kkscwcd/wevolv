package com.wevolv.wevibeservice.controller;

import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.service.GalleryImagesService;
import com.wevolv.wevibeservice.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.wevolv.wevibeservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class ImageController {

    public final GalleryImagesService galleryImageService;
    public final ImageService imageService;

    public ImageController(GalleryImagesService galleryImageService, ImageService imageService) {
        this.galleryImageService = galleryImageService;
        this.imageService = imageService;
    }

///////////////////////////////////////////////////////////////////////
    ///     LOCATION
///////////////////////////////////////////////////////////////////////
    @PostMapping("/gallery/add/image/location/{locationId}")
    public ResponseEntity<GenericApiResponse> saveImageToLocationGallery(List<MultipartFile> multipartFile, @PathVariable String locationId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var location = galleryImageService.saveImageToLocationGallery(multipartFile, locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(location)
                .message("Image saved.")
                .build());
    }

    @DeleteMapping("/gallery/delete/image/list/locationId/{locationId}")
    public ResponseEntity<GenericApiResponse> deleteImageListFromLocationGallery(@RequestBody List<String> imageId, @PathVariable String locationId,
                                                          HttpServletRequest request) throws Exception {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        galleryImageService.deleteImageListFromLocation(locationId, imageId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(String.format("Image %s deleted.", imageId))
                .build());
    }

///////////////////////////////////////////////////////////////////////
    ///     LOCATION REVIEW
///////////////////////////////////////////////////////////////////////

    @PostMapping("/gallery/add/image/location/review/{reviewId}")
    public ResponseEntity<GenericApiResponse> saveImageToLocationReview(List<MultipartFile> multipartFile,
                                                                         @PathVariable String reviewId,
                                                                         HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var lr = galleryImageService.saveImageToLocationReview(multipartFile, reviewId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(lr)
                .message(String.format("Image/s added to review with reviewId: %s.", reviewId))
                .build());
    }

    @DeleteMapping("/gallery/delete/image/review/{reviewId}")
    public ResponseEntity<GenericApiResponse> deleteImageListFromLocationReview(@RequestBody List<String> imageId,
                                                                                 @PathVariable String reviewId,
                                                                                 HttpServletRequest request) throws Exception {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var r = galleryImageService.deleteImageListFromLocationReview(reviewId, imageId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(r)
                .message(String.format("Image/s for review with reviewId: %s deleted.", reviewId))
                .build());
    }

///////////////////////////////////////////////////////////////////////
    ///     VIBE
///////////////////////////////////////////////////////////////////////
@PostMapping("/vibe/add/image/{vibeId}")
public ResponseEntity<GenericApiResponse> saveImageVibe(MultipartFile multipartFile, @PathVariable String vibeId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var vibe = imageService.saveImageVibe(multipartFile, vibeId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(vibe)
                .message("Image saved.")
                .build());
    }

    @DeleteMapping ("/vibe/{vibeId}/imageId/{imageId}")
    public ResponseEntity<GenericApiResponse> deleteImageVibe(@PathVariable String vibeId, @PathVariable String imageId,
                                                                      HttpServletRequest request) throws Exception {
        var apiResponse = GenericApiResponse.builder().build();
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var vibe = imageService.deleteImageVibe(vibeId, imageId, keycloakId);
        vibe.ifPresent(l -> {
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(vibe);
            apiResponse.setMessage("Image with id: " + imageId + "is deleted.");
        });
        return ResponseEntity.ok(apiResponse);
    }

///////////////////////////////////////////////////////////////////////
    ///     DECK
///////////////////////////////////////////////////////////////////////
    @PostMapping("/deck/cover/image/add/{deckId}")
    public ResponseEntity<GenericApiResponse> addCoverImageDeck(MultipartFile multipartFile, @PathVariable String deckId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var deck = imageService.addCoverImageDeck(multipartFile, deckId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(deck)
                .message("Image saved.")
                .build());
    }

    @PostMapping("/deck/{deckId}/delete/cover/image/{imageId}")
    public ResponseEntity<GenericApiResponse> deleteCoverImageDeck(@PathVariable String deckId, @PathVariable String imageId,
                                                              HttpServletRequest request) throws Exception {
        var apiResponse = GenericApiResponse.builder().build();
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var deck = imageService.deleteCoverImageDeck(deckId, imageId, keycloakId);
        deck.ifPresent(l -> {
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(deck);
            apiResponse.setMessage("Image with id: " + imageId + "is deleted.");
        });
        return ResponseEntity.ok(apiResponse);
    }

///////////////////////////////////////////////////////////////////////
    ///Tea Milardovic 2.5.2022: not used at moment, left for future use
///////////////////////////////////////////////////////////////////////
   /* @PostMapping("/location/profile/add/image/location/{locationId}")
    public ResponseEntity<GenericApiResponse> saveImageToLocationProfile(MultipartFile multipartFile, @PathVariable String locationId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var location = imageService.saveImage(multipartFile, locationId, keycloakId);
        var apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .response(location)
                .message("Image saved.")
                .build();
        return ResponseEntity.ok(apiResponse);
    }*/

  /*  @PostMapping("/fix/location/profile/locationId/{locationId}/imageId/{imageId}")
    public ResponseEntity<GenericApiResponse> deletePlaceProfileImage(@PathVariable String locationId, @PathVariable String imageId,
                                                          HttpServletRequest request) throws Exception {
        var apiResponse = GenericApiResponse.builder().build();
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var location = imageService.deleteImageFromLocation(locationId, imageId, keycloakId);

        location.ifPresent(l -> {
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(location);
            apiResponse.setMessage("Image with id: " + imageId + "is deleted.");
        });
        return ResponseEntity.ok(apiResponse);
    }
*/
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

}
