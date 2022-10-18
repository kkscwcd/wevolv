package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.LocationReview;
import com.wevolv.wevibeservice.domain.model.dto.UploadedImageDto;
import com.wevolv.wevibeservice.exception.NotFoundException;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
import com.wevolv.wevibeservice.repository.ImageRepository;
import com.wevolv.wevibeservice.repository.LocationRepository;
import com.wevolv.wevibeservice.repository.LocationReviewRepository;
import com.wevolv.wevibeservice.service.GalleryImagesService;
import com.wevolv.wevibeservice.util.CloudinaryHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
public class GalleryImagesImpl implements GalleryImagesService {

    private final LocationRepository locationRepository;
    private final ImageRepository imageRepository;
    private final CloudinaryHelper cloudinaryHelper;
    private final LocationReviewRepository locationReviewRepository;
    private final ProfileService profileService;

    public GalleryImagesImpl(LocationRepository locationRepository, ImageRepository imageRepository, CloudinaryHelper cloudinaryHelper, LocationReviewRepository locationReviewRepository, ProfileService profileService) {
        this.locationRepository = locationRepository;
        this.imageRepository = imageRepository;
        this.cloudinaryHelper = cloudinaryHelper;
        this.locationReviewRepository = locationReviewRepository;
        this.profileService = profileService;
    }

    @Override
    public Optional<Location> saveImageToLocationGallery(List<MultipartFile> multipartFile, String locationId, String keycloakId) {
        Optional<ProfileShortInfo> psi = profileService.userShortProfileByKeycloakId(keycloakId);
        Optional<Location> location = getLocation(locationId);
        location.ifPresentOrElse(l -> {
            List<Image> imageList = new ArrayList<>();
            multipartFile.forEach(mf -> {
                try {
                        UploadedImageDto newImageLink;
                        newImageLink = cloudinaryHelper.uploadOnCloudinary(mf);
                        Image image = Image.builder()
                            .createdTime(new Date().getTime())
                            .public_id(newImageLink.getPublic_id())
                            .link(newImageLink.getUrl())
                            .locationId(locationId)
                            //.likes(new ArrayList<>())
                            .build();

                        psi.ifPresent(image::setProfileShortInfo);
                        imageList.add(image);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            imageRepository.saveAll(imageList);
            var allImages = imageRepository.findAllByLocationId(l.getId());
            l.setNumberOfPhotos(allImages.size());
            locationRepository.save(l);

        }, () -> {
            throw new NotFoundException(String.format("Location with locationId %s is not found in database", locationId));
        });
        return location;
    }

   /* @Override
    public Optional<Location> updateImageInLocationGallery(MultipartFile multipartFile, String locationId, String imageId, String keycloakId) throws Exception {
        Optional<Location> location = locationRepository.findById(locationId);
        location.ifPresentOrElse(l -> {
            if(l.getCustomPhotos() != null){
                UploadedImageDto newImageLink = new UploadedImageDto();
                var existingImage = imageRepository.findById(imageId)
                        .orElseThrow(() -> new NotFoundException("Image with id: " + l.getPlaceImage().getId() + "does not exist"));
                l.getCustomPhotos().stream().filter(id -> id.getId().equals(imageId))
                        .findFirst()
                        .map(di -> {
                            l.getCustomPhotos().remove(di);
                            return di;
                        });
                imageRepository.deleteById(existingImage.getId());
                try {
                    log.info("Deleting image from Cloudinary.");
                    cloudinaryHelper.deleteImageFromCloudinary(existingImage.getPublic_id());
                    log.info("Uploading image to Cloudinary.");
                    newImageLink = cloudinaryHelper.uploadOnCloudinary(multipartFile);
                } catch (Exception e) {
                    log.error("Error while uploading/deleting image to Cloudinary.");
                    e.printStackTrace();
                }
                existingImage.setLink(newImageLink.getUrl());
                imageRepository.save(existingImage);
                l.getCustomPhotos().add(existingImage);
                locationRepository.save(l);
            } else {
                saveImageToLocationGallery(multipartFile, locationId, keycloakId);
            }}, () -> {
            throw new NotFoundException("Location is not found");
        });
        return location;
    }*/

    @Override
    public void deleteImageListFromLocation(String locationId, List<String> imageId, String keycloakId) {
        List<Image> checkedImageList = new ArrayList<>();
        Optional<Location> location = getLocation(locationId);

        imageId.forEach(i -> {
            checkedImageList.add(getImage(i));
        });

        location.ifPresentOrElse(el -> {
            checkedImageList.forEach(i -> {
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(i.getPublic_id());
                } catch (Exception e) {
                    e.getStackTrace();
                }
                imageRepository.deleteById(i.getId());
            });
            var existingImages = imageRepository.findAllByLocationId(el.getId());
            el.setNumberOfPhotos(existingImages.size());
            locationRepository.save(el);

        }, () -> {
            throw new NotFoundException(String.format("Location with locationId %s does not exist", locationId));
        });
    }

    @Override
    public LocationReview saveImageToLocationReview(List<MultipartFile> multipartFile, String reviewId, String keycloakId) {
        var review = getUserReview(reviewId, keycloakId);

        List<Image> imageList = new ArrayList<>();
        multipartFile.forEach(mf -> {
            try {
                UploadedImageDto newImageLink;
                newImageLink = cloudinaryHelper.uploadOnCloudinary(mf);
                Image image = Image.builder()
                        .id(UUID.randomUUID().toString())
                        .reviewId(reviewId)
                        //.locationId(locationId)
                        .createdTime(new Date().getTime())
                        .public_id(newImageLink.getPublic_id())
                        .link(newImageLink.getUrl())
                        .build();
                imageList.add(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        imageRepository.saveAll(imageList);
        review.getPhotos().addAll(imageList);
        locationReviewRepository.save(review);

        return review;
    }

    @Override
    public LocationReview deleteImageListFromLocationReview(String reviewId, List<String> imageId, String keycloakId) {
        var review = getUserReview(reviewId, keycloakId);
        List<Image> checkedImageList = new ArrayList<>();
        imageId.forEach(i -> {
            checkedImageList.add(getImage(i));
        });

        checkedImageList.forEach(i -> {
            try {
                cloudinaryHelper.deleteImageFromCloudinary(i.getPublic_id());
            } catch (Exception e) {
                e.getStackTrace();
            }
        });
        imageRepository.deleteAll(checkedImageList);
        review.getPhotos().removeAll(checkedImageList);
        locationReviewRepository.save(review);

        return review;
    }

    private Optional<Image> getImageById(String imageId) {
        return imageRepository.findById(imageId);
    }

    private Image getImage(String i) {
        return getImageById(i)
                .orElseThrow(() -> new NotFoundException(String.format("Image with imageId %s does not exist in database", i)));
    }

    private LocationReview getUserReview(String reviewId, String keycloakId) {
        return locationReviewRepository.findByIdAndKeycloakId(reviewId, keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("Review with reviewId %s does not exist in database", reviewId)));
    }

    private Optional<Location> getLocation(String locationId) {
        return locationRepository.findById(locationId);
    }
}
