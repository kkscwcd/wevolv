package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.Deck;
import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.UploadedImageDto;
import com.wevolv.wevibeservice.exception.NotFoundException;
import com.wevolv.wevibeservice.repository.DeckRepository;
import com.wevolv.wevibeservice.repository.ImageRepository;
import com.wevolv.wevibeservice.repository.LocationRepository;
import com.wevolv.wevibeservice.repository.VibeRepository;
import com.wevolv.wevibeservice.service.ImageService;
import com.wevolv.wevibeservice.util.CloudinaryHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final LocationRepository locationRepository;
    private final VibeRepository vibeRepository;
    private final DeckRepository deckRepository;
    private final CloudinaryHelper cloudinaryHelper;

    public ImageServiceImpl(ImageRepository imageRepository, LocationRepository locationRepository, VibeRepository vibeRepository, DeckRepository deckRepository, CloudinaryHelper cloudinaryHelper) {
        this.imageRepository = imageRepository;
        this.locationRepository = locationRepository;
        this.vibeRepository = vibeRepository;
        this.deckRepository = deckRepository;
        this.cloudinaryHelper = cloudinaryHelper;
    }

   /* @Override
    public Location saveImage(MultipartFile multipartFile, String locationId, String keycloakId) {
        Optional<Location> location = locationRepository.findById(locationId);
        location.ifPresentOrElse(l -> {
            UploadedImageDto newImageLink;
            Image image = Image.builder()
                    .createdTime(new Date().getTime())
                    .build();
            try {
                newImageLink = cloudinaryHelper.uploadOnCloudinary(multipartFile);
                image.setPublic_id(newImageLink.getPublic_id());
                image.setLink(newImageLink.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            l.setPlaceImage(image);
            imageRepository.save(image);
            locationRepository.save(l);

        }, () -> {
            throw new IllegalStateException("Location is not found");
        });
        return location.get();
    }*/

   /* @Override
    public Optional<Location> deleteImageById(Location location, String imageId) throws Exception {
        var image = imageRepository.findById(imageId);
        var existingLocation = locationRepository.findById(location.getId());

        existingLocation.ifPresentOrElse(el -> {
            image.ifPresentOrElse(i -> {
                el.setPlaceImage(null);
                locationRepository.save(el);
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(i.getPublic_id());
                } catch (Exception e) {
                    e.getStackTrace();
                }
                imageRepository.deleteById(imageId);
            }, () -> {
                throw new NotFoundException("Image does not exist");
            });
        }, () -> {
            throw new NotFoundException("Location does not exist");
        });
        return existingLocation;
    }
*/


  /*  @Override
    public Optional<Location> updateLocationImage(MultipartFile multipartFile, String locationId, String keycloakId) throws Exception {
        Optional<Location> location = locationRepository.findById(locationId);
        location.ifPresentOrElse(l -> {
            if(l.getPlaceImage() != null){
                UploadedImageDto newImageLink = new UploadedImageDto();
                var existingImage = imageRepository.findById(l.getPlaceImage().getId())
                        .orElseThrow(() -> new NotFoundException("Image with id: " + l.getPlaceImage().getId() + "does not exist"));
                l.setPlaceImage(null);
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
                l.setPlaceImage(existingImage);
            } else {
            saveImage(multipartFile, locationId, keycloakId);
        }}, () -> {
            throw new NotFoundException("Location is not found");
        });
        return location;
    }*/
    
    @Override
    public void deleteImageFromDatabase(String imageId) {
        imageRepository.deleteById(imageId);
    }

 /*   @Override
    public Optional<Location> deleteImageFromLocation(String locationId, String imageId, String keycloakId) {
        var image = imageRepository.findById(imageId);
        var existingLocation = locationRepository.findById(locationId);
        existingLocation.ifPresentOrElse(el -> {
            image.ifPresentOrElse(i -> {
                el.setPlaceImage(null);
                locationRepository.save(el);
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(i.getPublic_id());
                } catch (Exception e) {
                    e.getStackTrace();
                }
                imageRepository.deleteById(imageId);
            }, () -> {
                throw new NotFoundException("Image does not exist");
            });
        }, () -> {
            throw new NotFoundException("Location does not exist");
        });
        return existingLocation;
    }*/

    @Override
    public Optional<Vibe> saveImageVibe(MultipartFile multipartFile, String vibeId, String keycloakId) {
        var vibe = getVibeById(vibeId);
        vibe.ifPresentOrElse(v -> {
            UploadedImageDto newImageLink;
            Image image = Image.builder()
                    .vibeId(vibeId)
                    .keycloakId(keycloakId)
                    .createdTime(new Date().getTime())
                    .build();
            try {
                newImageLink = cloudinaryHelper.uploadOnCloudinary(multipartFile);
                image.setPublic_id(newImageLink.getPublic_id());
                image.setLink(newImageLink.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            v.setVibeImage(image);
            imageRepository.save(image);
            vibeRepository.save(v);

        }, () -> {
            throw new NotFoundException(String.format("Vibe with vibeId %s is not found", vibeId));
        });
        return vibe;
    }
    
    @Override
    public Optional<Vibe> deleteImageVibe(String vibeId, String imageId, String keycloakId) {
        var image = getImageById(imageId, keycloakId);
        var vibe = getVibeById(vibeId);
        vibe.ifPresentOrElse(ev -> {
            image.ifPresentOrElse(i -> {
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(i.getPublic_id());
                } catch (Exception e) {
                    e.getStackTrace();
                }
                ev.setVibeImage(null);
                vibeRepository.save(ev);
                imageRepository.deleteById(imageId);
            }, () -> {
                throw new NotFoundException(String.format("Image with imageId: %s does not exist", imageId));
            });
        }, () -> {
            throw new NotFoundException(String.format("Vibe with vibeId %s is not found", vibeId));
        });
        return vibe;
    }

    @Override
    public Optional<Deck> addCoverImageDeck(MultipartFile multipartFile, String deckId, String keycloakId) {
        Optional<Deck> deck = getUserDeck(deckId, keycloakId);
        deck.ifPresentOrElse(d -> {
            UploadedImageDto newImageLink;
            Image image = Image.builder()
                    .deckId(deckId)
                    .keycloakId(keycloakId)
                    .createdTime(new Date().getTime())
                    .build();
            try {
                newImageLink = cloudinaryHelper.uploadOnCloudinary(multipartFile);
                image.setPublic_id(newImageLink.getPublic_id());
                image.setLink(newImageLink.getUrl());
            } catch (IOException e) {
                e.printStackTrace();
            }
            d.setCoverImage(image);
            imageRepository.save(image);
            deckRepository.save(d);

        }, () -> {
            throw new NotFoundException(String.format("Deck with deckId %s is not found", deckId));
        });

        return deck;
    }

    @Override
    public Optional<Deck> deleteCoverImageDeck(String deckId, String imageId, String keycloakId) {
        var image = getImageById(imageId, keycloakId);
        var existingDeck = getUserDeck(deckId, keycloakId);
        existingDeck.ifPresentOrElse(ed -> {
            image.ifPresentOrElse(i -> {
                try {
                    cloudinaryHelper.deleteImageFromCloudinary(i.getPublic_id());
                } catch (Exception e) {
                    e.getStackTrace();
                }
                imageRepository.deleteById(imageId);
                ed.setCoverImage(null);
                deckRepository.save(ed);
            }, () -> {
                throw new NotFoundException("Image does not exist");
            });
        }, () -> {
            throw new NotFoundException(String.format("Deck with deckId %s does not exist", deckId));
        });
        return existingDeck;
    }

    @Override
    public String deleteImageByProfileId(String profileId) {
        return null;
    }

    @Override
    public Optional<Image> getImageById(String id) {
        return Optional.ofNullable(imageRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Image with id: " + id + " does not exists!")
        ));
    }

    private Optional<Deck> getUserDeck(String deckId, String keycloakId) {
        return deckRepository.findByIdAndKeycloakId(deckId, keycloakId);
    }

    private Optional<Vibe> getVibeById(String vibeId) {
        return vibeRepository.findById(vibeId);
    }

    public Optional<Image> getImageById(String id, String keycloakId) {
        return Optional.ofNullable(imageRepository.findByIdAndKeycloakId(id, keycloakId).orElseThrow(
                () -> new NotFoundException(String.format("Image with imageId: %s does not exist", id))
        ));
    }


    
}
