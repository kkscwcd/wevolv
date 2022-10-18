package com.wevolv.wevibeservice.config;

import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
import com.wevolv.wevibeservice.repository.*;
import com.wevolv.wevibeservice.service.*;
import com.wevolv.wevibeservice.service.impl.*;
import com.wevolv.wevibeservice.util.CloudinaryHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WevibeServiceIntegrationConfiguration {

    @Bean
    ImageService getImageService(ImageRepository imageRepository,
                                 LocationRepository locationRepository, VibeRepository vibeRepository, DeckRepository deckRepository, CloudinaryHelper cloudinaryHelper){
        return new ImageServiceImpl(imageRepository, locationRepository, vibeRepository, deckRepository, cloudinaryHelper);
    }

    @Bean
    GalleryImagesService getGalleryImages(LocationRepository locationRepository,
                                          ImageRepository imageRepository, CloudinaryHelper cloudinaryHelper,
                                          LocationReviewRepository locationReviewRepository, ProfileService profileService){
        return new GalleryImagesImpl(locationRepository, imageRepository, cloudinaryHelper, locationReviewRepository, profileService);
    }

//    @Bean
//    AthleteCoordinatesService getAthleteCoordinates(AthletesCoordinatesRepository athletesCoordinatesRepository, ProfileService profileService){
//        return new AthleteCoordinatesServiceImpl(athletesCoordinatesRepository, profileService);
//    }

    @Bean
    LocationService getPlaceService(LocationRepository locationRepository, ProfileService profileService,
                                    LocationReviewRepository locationReviewRepository, LocationTagsRepository locationTagsRepository,
                                    ImageRepository imageRepository, LikeRepository likeRepository,
                                    TagRepository tagRepository, FavouritesLocationsRepository favouritesLocationsRepository,
                                    DeckLocationsRepository deckLocationsRepository){
        return new LocationServiceImpl(locationRepository, profileService,
                locationReviewRepository, locationTagsRepository, imageRepository, likeRepository, tagRepository,
                favouritesLocationsRepository, deckLocationsRepository);
    }

    @Bean
    VibeService getVibeService(VibeRepository vibeRepository, ProfileService profileService,
                               VibeTagsRepository vibeTagsRepository, DeckVibesRepository deckVibesRepository,
                               FavouriteVibesRepository favouriteVibesRepository){
        return new VibeServiceImpl(vibeRepository, profileService, vibeTagsRepository, deckVibesRepository, favouriteVibesRepository);
    }

    @Bean
    DeckService getDeckService(DeckRepository deckRepository, VibeRepository vibeRepository,
                               LocationRepository locationRepository){
        return new DeckServiceImpl(deckRepository, vibeRepository, locationRepository);
    }

    @Bean
    DefaultTagService getDefaultTagService(DefaultLocationTagsRepository defaultLocationTagsRepository, DefaultVibeTagsRepository defaultVibeTagsRepository){
        return new DefaultTagsServiceImpl(defaultLocationTagsRepository, defaultVibeTagsRepository);
    }

}
