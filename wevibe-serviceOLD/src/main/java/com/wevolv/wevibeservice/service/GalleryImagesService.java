package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.LocationReview;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface GalleryImagesService {

    Optional<Location> saveImageToLocationGallery(List<MultipartFile> multipartFile, String locationId, String keycloakId);

    void deleteImageListFromLocation(String locationId, List<String> imageId, String keycloakId);

    LocationReview saveImageToLocationReview(List<MultipartFile> multipartFile, String reviewId, String keycloakId);

    LocationReview deleteImageListFromLocationReview(String reviewId, List<String> imageId, String keycloakId);

}
