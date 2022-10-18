package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.*;
import com.wevolv.wevibeservice.domain.model.dto.*;
import com.wevolv.wevibeservice.exception.NotFoundException;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
import com.wevolv.wevibeservice.repository.*;
import com.wevolv.wevibeservice.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;


import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    public final ProfileService profileService;
    public final LocationReviewRepository locationReviewRepository;
    public final LocationTagsRepository locationTagsRepository;
    private final ImageRepository imageRepository;
    private final LikeRepository likeRepository;
    private final TagRepository tagRepository;
    private final FavouritesLocationsRepository favouritesLocationsRepository;
    private final DeckLocationsRepository deckLocationsRepository;

    public LocationServiceImpl(LocationRepository locationRepository, ProfileService profileService,
                               LocationReviewRepository locationReviewRepository, LocationTagsRepository locationTagsRepository,
                               ImageRepository imageRepository,
                               LikeRepository likeRepository, TagRepository tagRepository,
                               FavouritesLocationsRepository favouritesLocationsRepository, DeckLocationsRepository deckLocationsRepository) {
        this.locationRepository = locationRepository;
        this.profileService = profileService;
        this.locationReviewRepository = locationReviewRepository;
        this.locationTagsRepository = locationTagsRepository;
        this.imageRepository = imageRepository;
        this.likeRepository = likeRepository;
        this.tagRepository = tagRepository;
        this.favouritesLocationsRepository = favouritesLocationsRepository;
        this.deckLocationsRepository = deckLocationsRepository;
    }

    @Override
    public Location saveLocation(LocationDto locationDto, String keycloakId) {
        checkIfUserExists(keycloakId);
//        var longitude = locationDto.getLocationGeoLocation().getCoordinates()[0];
//        var latitude = locationDto.getLocationGeoLocation().getCoordinates()[1];
        var geoLocation = LocationGeoLocation.builder()
                .type(locationDto.getLocationGeoLocation().getType())
                .coordinates(locationDto.getLocationGeoLocation().getCoordinates())
                .build();

        var location = Location.builder()
                .id(UUID.randomUUID().toString())
                .keycloakId(keycloakId)
                .name(locationDto.getName())
                .city(locationDto.getCity())
                .country(locationDto.getCountry())
                .address(locationDto.getAddress())
                .website(locationDto.getWebsite())
                .phoneNumber(locationDto.getPhoneNumber())
                .status(locationDto.getStatus())
                .locationGeoLocation(geoLocation)
                .build();

        //var savedLocation = locationRepository.save(location);
        var locationTagsList = addLocationTags(locationDto, location.getId(), keycloakId);
        location.setTags(locationTagsList);
        locationRepository.save(location);

        var deckLocations = DeckLocations.builder()
                .id(UUID.randomUUID().toString())
                .keycloakId(keycloakId)
                .myLocation(List.of(location))
                .favouriteLocations(new ArrayList<>())
                .build();
        deckLocationsRepository.save(deckLocations);
        return location;
    }

    @Override
    public Optional<Location> updateLocationName(LocationNameDto locationDto, String locationId, String keycloakId) {
        checkIfUserExists(keycloakId);
        var location = getUserLocation(locationId, keycloakId);
        location.setName(locationDto.getName());
        return Optional.of(locationRepository.save(location));
    }

    @Override
    public Location editLocation(EditLocationDto editLocationDto, String locationId, String keycloakId) {
        var location = getUserLocation(locationId, keycloakId);
        location.setName(editLocationDto.getName());

        var existingTagsList = location.getTags();

        if(existingTagsList.isEmpty()){
            location.setTags(new ArrayList<>());
            List<LocationTags> locationTagsNewlyList = getLocationTags(editLocationDto.getTags(), keycloakId, locationId);

            location.getTags().addAll(locationTagsNewlyList);
            locationTagsRepository.saveAll(locationTagsNewlyList);
            locationRepository.save(location);

        } else {

            List<LocationTags> locationTagsNewlyList = getLocationTags(editLocationDto.getTags(), keycloakId, locationId);
            List<LocationTags> addTags = locationTagsNewlyList.stream().filter(o1 -> existingTagsList.stream()
                            .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                    .collect(Collectors.toList());

            List<LocationTags> removeTags = existingTagsList.stream().filter(o1 -> locationTagsNewlyList.stream()
                            .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                    .collect(Collectors.toList());

            //remove tags from location
            location.getTags().removeAll(removeTags);
            tagRepository.deleteAll(removeTags);

            addTags.forEach(t -> {
                t.setId(UUID.randomUUID().toString());
                t.setLocationId(location.getId());
                t.setKeycloakId(keycloakId);
            });

            //add tags to post
            location.getTags().addAll(addTags);
            //add tags to parent topic
            tagRepository.saveAll(addTags);
            locationRepository.save(location);
        }

        return location;
    }

    @Override
    public List<ProfileShortInfo> getAllReviewLikes(String reviewId, String keycloakId, PageRequest paging) {
        checkIfUserExists(keycloakId);
        var existingLikes = likeRepository.findAllByReviewId(reviewId);
        List<String> listProfile = new ArrayList<>();
        existingLikes.forEach(el -> {
            listProfile.add(el.getAuthor().getKeycloakId());
        });
        return profileService.getListOfShortProfileInfo(listProfile);
    }

    @Override
    public Image getImageReview(String imageId, String keycloakId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(String.format("Image %s does not exist", imageId)));
    }

    @Override
    public Image getImageLocation(String imageId, String keycloakId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(String.format("Image %s does not exist", imageId)));

    }

    @Override
    public Map<String, Object> getAllFavouriteLocations(PageRequest paging, String keycloakId) {
        var locations = favouritesLocationsRepository.findAllByKeycloakId(keycloakId, paging);
        return populateFavouriteLocationMapResponse(locations);
    }

    @Override
    public Map<String, Object> getAllUserLocations(PageRequest paging, String keycloakId) {
        var locations = locationRepository.findAllByKeycloakId(keycloakId, paging);
        return populateUserLocationMapResponse(locations);
    }


    @Override
    public Optional<Location> updateLocationTags(List<LocationTagsDto> locationTagsDtoList, String keycloakId, String locationId) {
        checkIfUserExists(keycloakId);
        var location = getLocation(locationId);

        location.ifPresentOrElse(l -> {
            var existingTagsList = l.getTags();

            if(existingTagsList.isEmpty()){
                l.setTags(new ArrayList<>());
                List<LocationTags> locationTagsNewlyList = getLocationTagsList(locationTagsDtoList, keycloakId, locationId);

                l.getTags().addAll(locationTagsNewlyList);
                locationTagsRepository.saveAll(locationTagsNewlyList);
                locationRepository.save(l);

            } else {

                List<LocationTags> locationTagsNewlyList = getLocationTagsList(locationTagsDtoList, keycloakId, locationId);
                List<LocationTags> addTags = locationTagsNewlyList.stream().filter(o1 -> existingTagsList.stream()
                                .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                        .collect(Collectors.toList());

                List<LocationTags> removeTags = existingTagsList.stream().filter(o1 -> locationTagsNewlyList.stream()
                                .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                        .collect(Collectors.toList());

                //remove tags from location
                l.getTags().removeAll(removeTags);
                tagRepository.deleteAll(removeTags);

                addTags.forEach(t -> {
                    t.setId(UUID.randomUUID().toString());
                    t.setLocationId(l.getId());
                    t.setKeycloakId(keycloakId);
                });

                //add tags to post
                l.getTags().addAll(addTags);
                //add tags to parent topic
                tagRepository.saveAll(addTags);
                locationRepository.save(l);
            }

        }, () -> {
            throw new NotFoundException(String.format("Location with id: %s does not exist", locationId));
        });

        return location;
    }

    @Override
    public void likeUnlikeImageGallery(String imageId, String keycloakId) {
        checkIfUserExists(keycloakId);
        var psi = getUser(keycloakId);
        var image = getImage(imageId);
        var author = getAuthor(psi, keycloakId);

        var newLike = Like.builder()
                .id(UUID.randomUUID().toString())
                .imageId(imageId)
                .author(author)
                .build();

        if(isImageLiked(imageId, keycloakId)){
            var existingLike = getExistingLike(imageId, keycloakId);

            //remove like from image
            likeRepository.delete(existingLike);
            var existingLikes = likeRepository.findAllByImageId(imageId);

            //set number of likes on image
            image.setNumberOfLikes(existingLikes.size());
            image.setIsLiked(false);
            imageRepository.save(image);
        } else {

            //add like to image
            likeRepository.save(newLike);
            var existingLikes = likeRepository.findAllByImageId(imageId);

            //set number of likes on image
            image.setNumberOfLikes(existingLikes.size());
            image.setIsLiked(true);
            imageRepository.save(image);
        }
    }

    @Override
    public List<ProfileShortInfo> getAllImageLikes(String imageId, String keycloakId, PageRequest paging) {
        checkIfUserExists(keycloakId);
        var existingLikes = likeRepository.findAllByImageId(imageId);
        List<String> listProfile = new ArrayList<>();
        existingLikes.forEach(el -> {
            listProfile.add(el.getAuthor().getKeycloakId());
        });
        var l = profileService.getListOfShortProfileInfo(listProfile);
        return l;
    }

    private Map<String, Object> populateMapResponseLikes(Page<LocationReview> locationReviews) {
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", locationReviews.getContent());
        response.put("currentPage", locationReviews.getNumber());
        response.put("totalItems", locationReviews.getTotalElements());
        response.put("totalPages", locationReviews.getTotalPages());
        response.put("hasPrevious", locationReviews.hasPrevious());
        response.put("hasNext", locationReviews.hasNext());
        return response;
    }

    @Override
    public void saveUnsavedLocationToFavorites(String locationId, String keycloakId) {
        checkIfUserExists(keycloakId);
        var deckLocations = getUserDeckLocations(keycloakId);
        var favouriteLocation = getUserFavouriteLocation(locationId, keycloakId);

        favouriteLocation.ifPresentOrElse(fl -> {
            deckLocations.ifPresentOrElse(dl -> {
                dl.getFavouriteLocations().remove(fl);
                deckLocationsRepository.save(dl);
                favouritesLocationsRepository.delete(fl);
            }, () -> {
                var deckLocation = DeckLocations.builder()
                        .id(UUID.randomUUID().toString())
                        .keycloakId(keycloakId)
                        .myLocation(new ArrayList<>())
                        .favouriteLocations(List.of(fl))
                        .build();
                deckLocationsRepository.save(deckLocation);
            });

        }, () -> {
            var newFavoriteLocation = FavoriteLocation.builder()
                    .id(UUID.randomUUID().toString())
                    .locationId(locationId)
                    .keycloakId(keycloakId)
                    .build();

            deckLocations.ifPresentOrElse(dl -> {
                dl.getFavouriteLocations().add(newFavoriteLocation);
                deckLocationsRepository.save(dl);
                favouritesLocationsRepository.save(newFavoriteLocation);
            }, () -> {
                var deckLocation = DeckLocations.builder()
                        .id(UUID.randomUUID().toString())
                        .keycloakId(keycloakId)
                        .favouriteLocations(List.of(newFavoriteLocation))
                        .build();
                deckLocationsRepository.save(deckLocation);
                favouritesLocationsRepository.save(newFavoriteLocation);
            });
        });
    }

    @Override
    public LocationReview addLocationReview(LocationReviewDto locationReviewDto, String locationId, String keycloakId) {
        var profileShortInfo = Optional.ofNullable(getProfileShortInfo(keycloakId));
        var location = getLocation(locationId);
        var locationReview = LocationReview.builder().build();

        //athleteProfile refreshed by every call to profile service
        profileShortInfo.ifPresent(psi -> {
            location.ifPresent(l -> {
                locationReview.setId(UUID.randomUUID().toString());
                locationReview.setReview(locationReviewDto.getReview());
                locationReview.setLocationId(locationId);
                locationReview.setLocationRating(locationReviewDto.getLocationRating());
                locationReview.setPlayerInfo(psi);
                locationReview.setKeycloakId(keycloakId);
                locationReview.setTimePosted(Instant.now());
                locationReview.setPlayerInfo(psi);
                locationReview.setPhotos(new ArrayList<>());
                locationReview.setIsLiked(false);
                locationReviewRepository.save(locationReview);
                int numbReviews = locationReviewRepository.findAllByLocationId(l.getId()).size();
                l.setNumberOfReviews(numbReviews);
                locationRepository.save(l);
            });
        });
        return locationReview;
    }

    @Override
    public Optional<LocationReview> editLocationReview(LocationReviewDto locationReviewDto, String reviewId, String keycloakId) {
        var locationReview = getUserReview(reviewId, keycloakId);
        locationReview.ifPresentOrElse(er -> {
            er.setReview(locationReviewDto.getReview());
            er.setLocationRating(locationReviewDto.getLocationRating());
            locationReviewRepository.save(er);
        }, () -> {
            throw new NotFoundException(String.format("Location review %s does not exist", reviewId));
        });
        return locationReview;
    }

    @Override
    public Map<String, Object> getAllLocationReviews(String locationId, PageRequest paging) {
        var locationReview = locationReviewRepository.findAllByLocationId(locationId, paging);
        return populateMapResponseLikes(locationReview);
    }

    @Override
    public int getNumberOfReviewShared(String keycloakId, String reviewId) {
        var review = getExistingReview(reviewId);
        return review.getNumberOfShares();
    }

    @Override
    public void shareReview(String keycloakId, String reviewId) {
        var review = getExistingReview(reviewId);
        review.setNumberOfShares(review.getNumberOfShares() + 1);
        locationReviewRepository.save(review);
    }

    @Override
    public Location getLocation(String locationId, String keycloakId) {
        return locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(String.format("Not found %s", locationId)));
    }

    @Override
    public Map<String, Object> getAllImages(String locationId, String keycloakId, PageRequest paging) {
        var images = imageRepository.findAllByLocationId(locationId, paging);
        return populateImageMapResponse(images);
    }

    @Override
    public Map<String, Object> getAllImagesReview(String reviewId, String keycloakId, PageRequest paging) {
        var images = imageRepository.findAllByReviewId(reviewId, paging);
        return populateImageMapResponse(images);
    }

    private LocationReview getExistingReview(String reviewId) {
        return locationReviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(String.format("Review with reviewId %s does not exist in database", reviewId)));
    }

    private Map<String, Object> populateImageMapResponse(Page<Image> images) {
        Map<String, Object> response = new HashMap<>();
        response.put("images", images.getContent());
        response.put("currentPage", images.getNumber());
        response.put("totalItems", images.getTotalElements());
        response.put("totalPages", images.getTotalPages());
        response.put("hasPrevious", images.hasPrevious());
        response.put("hasNext", images.hasNext());
        return response;
    }


    private Map<String, Object> populateMapResponse(Page<LocationReview> locationReviews) {
        Map<String, Object> response = new HashMap<>();
        response.put("reviews", locationReviews.getContent());
        response.put("currentPage", locationReviews.getNumber());
        response.put("totalItems", locationReviews.getTotalElements());
        response.put("totalPages", locationReviews.getTotalPages());
        response.put("hasPrevious", locationReviews.hasPrevious());
        response.put("hasNext", locationReviews.hasNext());
        return response;
    }

    private Optional<LocationReview> getUserReview(String reviewId, String keycloakId) {
        return locationReviewRepository.findByIdAndKeycloakId(reviewId, keycloakId);
    }

    @Override
    public void deleteLocationReview(String reviewId, String locationId, String keycloakId) {
        var existingReview = getUserReview(reviewId, keycloakId);
        existingReview.ifPresent(er -> {
            locationReviewRepository.delete(er);
            var reviews = locationReviewRepository.findAllByLocationId(locationId);
            var location = locationRepository.findById(locationId).orElseThrow(() -> new NotFoundException(String.format("Location with locationId %s was not found in database", locationId)));
            location.setNumberOfReviews(reviews.size());
            locationRepository.save(location);
            var reviewLikes = likeRepository.findAllByReviewId(reviewId);
            likeRepository.deleteAll(reviewLikes);
        });
    }


    @Override
    public List<Location> getLocationsCoordinatesNearby(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId, PageRequest page) {
        Point basePoint = new Point(athleteCoordinatesDTO.getLongitude(), athleteCoordinatesDTO.getLatitude());
        Distance radius = new Distance(athleteCoordinatesDTO.getRadius(), Metrics.KILOMETERS);
       // Circle area = new Circle(basePoint, radius);
       
        var locationList = locationRepository.findByLocationGeoLocationGeometry(basePoint, athleteCoordinatesDTO.getRadius(), athleteCoordinatesDTO.getTags(), page);
       // var locationList = locationRepository.findByLocationGeoLocationWithin(area);
        /*List<Location> filteredLocations = locationList.stream().filter(v -> v.getTags().stream()
                .anyMatch(t -> athleteCoordinatesDTO.getTags().contains(t.getTagName().name()))
        ).collect(Collectors.toList());*/

        final List<Location> locationCoordinatesListUpdated = new ArrayList<>();
        locationList.forEach(location -> {
            var psi = locationRepository.findById(location.getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Location %s not found", location.getId())));
            locationCoordinatesListUpdated.add(psi);
        });

        return locationCoordinatesListUpdated;
    }

    @Override
    public Stream<Location> filterByTags(List<String> locationTags, PageRequest page, String keycloakId) {
        return locationRepository.findLocationByTag(locationTags, page);
    }

    @Override
    public void deleteLocation(String locationId, String keycloakId) {
        var location = getLocation(locationId);
        location.ifPresentOrElse(l -> {
            //imageRepository.deleteAll(l.getCustomPhotos());
            tagRepository.deleteAll(l.getTags());
            //locationReviewRepository.deleteAll(l.getLocationReviews());
            locationRepository.delete(l);
        }, () -> {
            throw new NotFoundException(String.format("Location with locationId %s was not found in database", locationId));
        });
    }

    @Override
    public void likeUnlikeLocationReview(String reviewId, String keycloakId) {
        var psi = getProfileShortInfo(keycloakId);
        var review = getReview(reviewId);
        var author = getAuthor(psi, keycloakId);
        var newLike = getNewLike(author, reviewId);
        var reviewLikes = likeRepository.findAllByReviewId(reviewId);

        if(isReviewLiked(reviewLikes, keycloakId)){
            //remove like from review
            reviewLikes.stream()
                    .filter(like -> like.getAuthor().getKeycloakId().equals(keycloakId))
                    .findFirst()
                    .map(m-> {
                        reviewLikes.remove(m);
                        likeRepository.delete(m);
                        return m;
                    });
            review.setNumberOfLikes(reviewLikes.size());
            review.setIsLiked(false);
            locationReviewRepository.save(review);

        } else {
            //set number of likes on post
            reviewLikes.add(newLike);
            review.setNumberOfLikes(reviewLikes.size());
            review.setIsLiked(true);
            locationReviewRepository.save(review);
            likeRepository.save(newLike);
        }
    }



    private Optional<DeckLocations> getUserDeckLocations(String keycloakId) {
        return deckLocationsRepository.findByKeycloakId(keycloakId);
    }

    private Optional<FavoriteLocation> getUserFavouriteLocation(String locationId, String keycloakId) {
        return favouritesLocationsRepository.findByLocationIdAndKeycloakId(locationId, keycloakId);
    }

    private ProfileShortInfo getProfileShortInfo(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s does not exist in database", keycloakId)));
    }

    private boolean isReviewLiked(List<Like> likes, String keycloakId) {
        return likes.stream()
                .anyMatch(l-> l.getAuthor().getKeycloakId().equals(keycloakId));
    }

    private Like getNewLike(Author author, String reviewId) {
        return Like.builder()
                .id(UUID.randomUUID().toString())
                .author(author)
                .reviewId(reviewId)
                .timeOfCreation(Instant.now())
                .build();
    }

    private LocationReview getReview(String reviewId) {
        return locationReviewRepository.findById(reviewId).orElseThrow(() -> new NotFoundException(String.format("Review with reviewId: %s " +
                "does not exist!", reviewId)));
    }

    private void checkIfUserExists(String keycloakId) {
        profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s does not exist", keycloakId)));
    }

    private List<LocationTags> addLocationTags(LocationDto locationDto, String locationId, String keycloakId) {
        List<LocationTags> newTagList = new ArrayList<>();
        locationDto.getTags().forEach(t -> {
            var newTag = LocationTags.builder()
                    .id(t.getId())
                    .tagName(t.getTagName())
                    .keycloakId(keycloakId)
                    .locationId(locationId)
                    .build();
            newTagList.add(newTag);
        });
        tagRepository.saveAll(newTagList);
        return newTagList;
    }

    private Location getUserLocation(String locationId, String keycloakId) {
        return locationRepository.findByIdAndKeycloakId(locationId, keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("Location with locationId %s does not exist in database", locationId)));
    }

/*    private void setNumberOfImageLikesLocation(String imageId, Location l, Image existingImage) {
        l.getCustomPhotos().stream()
                .filter(cp -> cp.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Image with imageId %s does not exist in database", imageId)))
                .setNumberOfLikes(existingImage.getLikes().size());
    }*/

    private Like getExistingLike(String imageId, String keycloakId) {
        var likes = likeRepository.findAllByImageId(imageId);
        return likes.stream()
                .filter(l-> l.getAuthor().getKeycloakId().equals(keycloakId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Like with keycloakId %s does not exist in database", keycloakId)));
    }

    private boolean isImageLiked(String imageId, String keycloakId) {
        var likes = likeRepository.findAllByImageId(imageId);
        return likes.stream()
                .anyMatch(l-> l.getAuthor().getKeycloakId().equals(keycloakId));
    }


    private ProfileShortInfo getUser(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s does not exist in database", keycloakId)));
    }

    private Optional<Location> getLocation(String locationId) {
        return locationRepository.findById(locationId);
    }

    private Image getImage(String imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException(String.format("Image with imageId %s does not exist in database", imageId)));
    }

    private Author getAuthor(ProfileShortInfo psi, String keycloakId) {

        Author author = new Author();
        author.setId(UUID.randomUUID().toString());
        author.setKeycloakId(keycloakId);
        author.setFirstName(psi.getFirstName());
        author.setLastName(psi.getLastName());
        author.setImage(psi.getImage());

        return author;
    }

    private LocationTags getUserTag(String keycloakId, String tagId) {
        return tagRepository.findByIdAndKeycloakId(tagId, keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("Tag with keycloakId %s does not exist in database", keycloakId)));
    }

    private List<LocationTags> getLocationTagsList(List<LocationTagsDto> locationTagsDtoList, String keycloakId, String locationId) {
        List<LocationTags> locationTagsNewlyList = new ArrayList<>();
        locationTagsDtoList.forEach(lt -> {
            var newTag = LocationTags.builder()
                    .id(UUID.randomUUID().toString())
                    .locationId(locationId)
                    .tagName(lt.getTagName())
                    .keycloakId(keycloakId)
                    .build();
            locationTagsNewlyList.add(newTag);
        });
        return locationTagsNewlyList;
    }

    private List<LocationTags> getLocationTags(List<LocationTags> locationTagsDtoList, String keycloakId, String locationId) {
        List<LocationTags> locationTagsNewlyList = new ArrayList<>();
        locationTagsDtoList.forEach(lt -> {
            var newTag = LocationTags.builder()
                    .id(UUID.randomUUID().toString())
                    .locationId(locationId)
                    .tagName(lt.getTagName())
                    .keycloakId(keycloakId)
                    .build();
            locationTagsNewlyList.add(newTag);
        });
        return locationTagsNewlyList;
    }

    private Map<String, Object> populateUserLocationMapResponse(Page<Location> locations) {
        Map<String, Object> response = new HashMap<>();
        response.put("locations", locations.getContent());
        response.put("currentPage", locations.getNumber());
        response.put("totalItems", locations.getTotalElements());
        response.put("totalPages", locations.getTotalPages());
        response.put("hasPrevious", locations.hasPrevious());
        response.put("hasNext", locations.hasNext());
        return response;
    }

    private Map<String, Object> populateFavouriteLocationMapResponse(Page<FavoriteLocation> locations) {
        Map<String, Object> response = new HashMap<>();
        response.put("locations", locations.getContent());
        response.put("currentPage", locations.getNumber());
        response.put("totalItems", locations.getTotalElements());
        response.put("totalPages", locations.getTotalPages());
        response.put("hasPrevious", locations.hasPrevious());
        response.put("hasNext", locations.hasNext());
        return response;
    }
}
