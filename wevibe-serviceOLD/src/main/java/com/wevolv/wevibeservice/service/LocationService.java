package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.*;
import com.wevolv.wevibeservice.domain.model.dto.*;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface LocationService {

    Location saveLocation(LocationDto locationDto, String keycloakId);

    Optional<Location> updateLocationName(LocationNameDto locationDto, String locationId, String keycloakId);

    Optional<Location> updateLocationTags(List<LocationTagsDto> locationTagsDtoList, String keycloakId, String locationId);

    void likeUnlikeImageGallery(String imageId, String keycloakId);

    List<ProfileShortInfo> getAllImageLikes(String imageId, String keycloakId, PageRequest paging);

    void saveUnsavedLocationToFavorites(String locationId, String keycloakId);

    LocationReview addLocationReview(LocationReviewDto locationReviewDto, String locationId, String keycloakId);

    Optional<LocationReview> editLocationReview(LocationReviewDto locationReviewDto, String reviewId, String keycloakId);

    void deleteLocationReview(String reviewId, String locationId, String keycloakId);

    List<Location> getLocationsCoordinatesNearby(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId, PageRequest page);

    Stream<Location> filterByTags(List<String> locationTags, PageRequest pageRequest, String keycloakId);

    void deleteLocation(String locationId, String keycloakId);

    void likeUnlikeLocationReview(String reviewId, String keycloakId);

    Map<String, Object> getAllLocationReviews(String locationId, PageRequest paging);

    int getNumberOfReviewShared(String keycloakId, String reviewId);

    void shareReview(String keycloakId, String reviewId);

    Location getLocation(String locationId, String keycloakId);

    Map<String, Object> getAllImages(String locationId, String keycloakId, PageRequest paging);

    Map<String, Object> getAllImagesReview(String reviewId, String keycloakId, PageRequest paging);

    Location editLocation(EditLocationDto editLocationDto, String locationId, String keycloakId);

    List<ProfileShortInfo> getAllReviewLikes(String reviewId, String keycloakId, PageRequest paging);

    Image getImageReview(String imageId, String keycloakId);

    Image getImageLocation(String imageId, String keycloakId);

    Map<String, Object> getAllFavouriteLocations(PageRequest paging, String keycloakId);

    Map<String, Object> getAllUserLocations(PageRequest paging, String keycloakId);
}
