package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.Deck;
import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.UploadedImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface ImageService {

    //Location saveImage(MultipartFile multipartFile, String locationId, String keycloakId);

    //Optional<Location> deleteImageById(Location location, String imageId) throws Exception;

    String deleteImageByProfileId(String profileId);

    Optional<Image> getImageById(String id);

    //Optional<Location> updateLocationImage(MultipartFile multipartFile, String locationId, String keycloakId) throws Exception;

    void deleteImageFromDatabase(String imageId);

    //Optional<Location> deleteImageFromLocation(String locationId, String imageId, String keycloakId);

    Optional<Vibe> saveImageVibe(MultipartFile multipartFile, String vibeId, String keycloakId);

    Optional<Vibe> deleteImageVibe(String vibeId, String imageId, String keycloakId);

    Optional<Deck> addCoverImageDeck(MultipartFile multipartFile, String deckId, String keycloakId);

    Optional<Deck> deleteCoverImageDeck(String deckId, String imageId, String keycloakId);
}
