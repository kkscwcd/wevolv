package com.wevolv.wevibeservice.controller;

import com.wevolv.wevibeservice.domain.GenericApiResponse;
import com.wevolv.wevibeservice.domain.LocationSelector;
import com.wevolv.wevibeservice.domain.ReviewSelector;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.dto.*;
import com.wevolv.wevibeservice.service.LocationService;
import com.wevolv.wevibeservice.util.TokenDecoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.wevolv.wevibeservice.util.TokenDecoder.getUserIdFromToken;


@RestController
@RequestMapping(value = "/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }


    @PostMapping("/save")
    public ResponseEntity<GenericApiResponse> saveLocation(@RequestBody LocationDto locationDto, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var savedLocation = locationService.saveLocation(locationDto, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(savedLocation)
                .message(String.format("New location with id %s was added.", savedLocation.getId()))
                .build());
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<GenericApiResponse> getLocation(@PathVariable String locationId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var savedLocation = locationService.getLocation(locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(savedLocation)
                .build());
    }

    @PostMapping("/update/{locationId}")
    public ResponseEntity<GenericApiResponse> updateLocation(@RequestBody EditLocationDto editLocationDto,
                                                                 @PathVariable String locationId,
                                                                 HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var savedLocation = locationService.editLocation(editLocationDto, locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(savedLocation)
                .message(String.format("Location %s was edited", locationId))
                .build());
    }

    @GetMapping(value = "/get/all/images/{locationId}")
    public ResponseEntity<GenericApiResponse> getAllImages(@PathVariable String locationId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "3") int size,
                                                           HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var locationResponse = locationService.getAllImages(locationId, keycloakId, paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(locationResponse)
                .message(String.format("All images for location with locationId: %s.", locationId))
                .build());
    }

    @GetMapping(value = "/get/image/location/{imageId}")
    public ResponseEntity<GenericApiResponse> getImageLocation(@PathVariable String imageId, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var imageLocation = locationService.getImageLocation(imageId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(imageLocation)
                .message(String.format("Image %s.", imageId))
                .build());
    }

    @PostMapping(value = "/likeUnlike/image/{imageId}")
    public ResponseEntity<GenericApiResponse> likeUnlikeImageGallery(@PathVariable String imageId,
                                                                   HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        locationService.likeUnlikeImageGallery(imageId, keycloakId);
        var apiResponse = GenericApiResponse.builder().build();
        apiResponse.setStatusCode(HttpStatus.OK.value());
        apiResponse.setMessage(String.format("Image with imageId: %s was liked/unliked.", imageId));

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/all/likes/image/{imageId}")
    public ResponseEntity<GenericApiResponse> getAllLikes(@PathVariable String imageId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size,
                                                          HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var locationResponse = locationService.getAllImageLikes(imageId, keycloakId, paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(locationResponse)
                .message(String.format("All likes for image with imageId: %s.", imageId))
                .build());
    }

    @PostMapping("/saveUnsave/favorites/{locationId}")
    public ResponseEntity<GenericApiResponse> saveUnsavedLocationToFavorites(@PathVariable String locationId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        locationService.saveUnsavedLocationToFavorites(locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Location with locationId: %s was added to favorites.", locationId))
                .build());
    }

    @GetMapping("/get/all")
    public ResponseEntity<GenericApiResponse> getAllLocations(HttpServletRequest request,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "3") int size,
                                                                  @RequestParam LocationSelector locationSelector){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        GenericApiResponse apiResponse = GenericApiResponse.builder().build();

        var paging = PageRequest.of(page, size);
        if(locationSelector.name().equals(LocationSelector.FAVOURITES.name())) {
            var locations = locationService.getAllFavouriteLocations(paging, keycloakId);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locations);
            apiResponse.setMessage("All favourite locations");
        }

        if(locationSelector.name().equals(LocationSelector.USER_LOCATION.name())) {
            var locations = locationService.getAllUserLocations(paging, keycloakId);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locations);
            apiResponse.setMessage("All user locations");
        }

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/add/review/{locationId}")
    public ResponseEntity<GenericApiResponse> locationAddReview(@RequestBody LocationReviewDto locationReviewDto,
                                                                @PathVariable String locationId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var locationReview = locationService.addLocationReview(locationReviewDto, locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(locationReview)
                .message(String.format("Location review for location with locationId %s was added.", locationId))
                .build());
    }

    @PostMapping("/edit/review/{reviewId}")
    public ResponseEntity<GenericApiResponse> editLocationReview(@RequestBody LocationReviewDto locationReviewDto,
                                                                 @PathVariable String reviewId,
                                                                 HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);


        var locationReview = locationService.editLocationReview(locationReviewDto, reviewId, keycloakId);
        var apiResponse = GenericApiResponse.builder().build();
        locationReview.ifPresent(lr -> {
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(lr);
            apiResponse.setMessage(String.format("Location review %s is edited.", reviewId));
        });
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/get/all/reviews/{locationId}")
    public ResponseEntity<GenericApiResponse> getAllReviews(HttpServletRequest request,
                                                            @PathVariable String locationId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "3") int size,
                                                            @RequestParam ReviewSelector selector){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        GenericApiResponse apiResponse = GenericApiResponse.builder().build();
        Map<String, Object> locationReview;

        if(selector.name().equals(ReviewSelector.NEWEST.name())){
            var paging = PageRequest.of(page, size, Sort.by("timePosted").descending());
            locationReview = locationService.getAllLocationReviews(locationId, paging);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locationReview);
            apiResponse.setMessage("All newest reviews");
        }

        if(selector.name().equals(ReviewSelector.HIGHEST.name())){
            var paging = PageRequest.of(page, size, Sort.by("locationRating").descending());
            locationReview = locationService.getAllLocationReviews(locationId, paging);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locationReview);
            apiResponse.setMessage("All highest rate reviews");
        }

        if(selector.name().equals(ReviewSelector.LOWEST.name())){
            var paging = PageRequest.of(page, size, Sort.by("locationRating").ascending());
            locationReview = locationService.getAllLocationReviews(locationId, paging);
            apiResponse.setStatusCode(HttpStatus.OK.value());
            apiResponse.setResponse(locationReview);
            apiResponse.setMessage("All lowest rate reviews");
        }

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/likeUnlike/review/{reviewId}")
    public ResponseEntity<GenericApiResponse> likeUnlikeLocationReview(@PathVariable String reviewId,
                                                                 HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        locationService.likeUnlikeLocationReview(reviewId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Location review with locationReviewId: %s is liked/unliked.", reviewId))
                .build());
    }

    @GetMapping(value = "/all/likes/review/{reviewId}")
    public ResponseEntity<GenericApiResponse> getAllLikesReview(@PathVariable String reviewId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size,
                                                          HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var locationResponse = locationService.getAllReviewLikes(reviewId, keycloakId, paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(locationResponse.size())
                .message(String.format("All likes for image with reviewId: %s.", reviewId))
                .build());
    }

    @GetMapping(value = "/get/all/images/review/{reviewId}")
    public ResponseEntity<GenericApiResponse> getAllImagesReview(@PathVariable String reviewId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "3") int size,
                                                           HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        var imagesReview = locationService.getAllImagesReview(reviewId, keycloakId, paging);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(imagesReview)
                .message(String.format("All images for review %s.", reviewId))
                .build());
    }

    @GetMapping(value = "/get/image/review/{imageId}")
    public ResponseEntity<GenericApiResponse> getImageReview(@PathVariable String imageId, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var imageReview = locationService.getImageReview(imageId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(imageReview)
                .message(String.format("Image %s.", imageId))
                .build());
    }


    @PostMapping(value = "/share/{reviewId}")
    public ResponseEntity<GenericApiResponse> shareReview(HttpServletRequest request,
                                                        @PathVariable String reviewId){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        locationService.shareReview(keycloakId, reviewId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Review with reviewId %s is shared", reviewId))
                .build());
    }

    @GetMapping(value = "/all/share/{reviewId}")
    public ResponseEntity<GenericApiResponse> getNumberOfReviewShared(HttpServletRequest request,
                                                                    @PathVariable String reviewId){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        int number = locationService.getNumberOfReviewShared(keycloakId, reviewId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(number)
                .message(String.format("Review with reviewId %s is shared %s times.", reviewId , number))
                .build());
    }

    @DeleteMapping("/delete/review/{reviewId}/{locationId}")
    public ResponseEntity<GenericApiResponse> deleteLocationReview(@PathVariable String reviewId,
                                                                   @PathVariable String locationId,
                                                                   HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        locationService.deleteLocationReview(reviewId, locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Review with reviewId %s is deleted.", reviewId))
                .build());
    }

    @PostMapping("/get/locations/nearby")
    public List<Location> getLocationsCoordinatesNearby(@RequestBody AthleteCoordinatesDTO athleteCoordinatesDTO,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "3") int size,
                                                                            HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return locationService.getLocationsCoordinatesNearby(athleteCoordinatesDTO, keycloakId, paging);
    }

    @PostMapping("/delete/location/{locationId}")
    public ResponseEntity<GenericApiResponse> deleteLocation(@PathVariable String locationId, HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        locationService.deleteLocation(locationId, keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("Location with id %s was deleted.", keycloakId))
                .build());

    }

    @PostMapping(value = "/filter")
    public Stream<Location> filterLocationsByTags(@RequestBody LocationTagFilterDto locationTags,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "3") int size, HttpServletRequest request) {
        String jwt = TokenDecoder.getJwt(request);
        String keycloakId = TokenDecoder.getUserIdFromToken(jwt);

        var paging = PageRequest.of(page, size);
        return locationService.filterByTags(locationTags.getLocationTags(), paging, keycloakId);
    }

}
