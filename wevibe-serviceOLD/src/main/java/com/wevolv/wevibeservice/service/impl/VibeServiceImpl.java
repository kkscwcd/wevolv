package com.wevolv.wevibeservice.service.impl;

import com.wevolv.wevibeservice.domain.model.*;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import com.wevolv.wevibeservice.domain.model.dto.VibeDto;
import com.wevolv.wevibeservice.exception.NotFoundException;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
import com.wevolv.wevibeservice.repository.DeckVibesRepository;
import com.wevolv.wevibeservice.repository.FavouriteVibesRepository;
import com.wevolv.wevibeservice.repository.VibeRepository;
import com.wevolv.wevibeservice.repository.VibeTagsRepository;
import com.wevolv.wevibeservice.service.VibeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VibeServiceImpl implements VibeService {

    private final VibeRepository vibeRepository;
    private final ProfileService profileService;
    private final VibeTagsRepository vibeTagsRepository;
    private final DeckVibesRepository deckVibesRepository;
    private final FavouriteVibesRepository favouriteVibesRepository;

    public VibeServiceImpl(VibeRepository vibeRepository, ProfileService profileService, VibeTagsRepository vibeTagsRepository, DeckVibesRepository deckVibesRepository, FavouriteVibesRepository favouriteVibesRepository) {
        this.vibeRepository = vibeRepository;
        this.profileService = profileService;
        this.vibeTagsRepository = vibeTagsRepository;
        this.deckVibesRepository = deckVibesRepository;
        this.favouriteVibesRepository = favouriteVibesRepository;
    }

    @Override
    public Vibe saveVibe(VibeDto vibeDto, String keycloakId) {
        var psi = getProfileShortInfo(keycloakId);
        List<ProfileShortInfo> receiversProfiles = getReceiversProfileInfo(vibeDto);
        List<InvitedFriends> invitedFriendsList = new ArrayList<>();
        List<VibeTags> newTagList = new ArrayList<>();

        if(vibeDto.getTags() == null){
            vibeDto.setTags(new ArrayList<>());
        }

        var geoLocation = VibeGeoLocation.builder()
                .type(vibeDto.getVibeGeoLocation().getType())
                .coordinates(vibeDto.getVibeGeoLocation().getCoordinates())
                .build();

        var author = Author.builder()
                .id(UUID.randomUUID().toString())
                .firstName(psi.getFirstName())
                .lastName(psi.getLastName())
                .image(psi.getImage())
                .country(psi.getCountry())
                .currentPosition(psi.getCurrentPosition())
                .profileId(psi.getProfileId())
                .keycloakId(keycloakId)
                .build();

        receiversProfiles.forEach(rp -> {
            var invitedFriend = InvitedFriends.builder()
                    .firstName(rp.getFirstName())
                    .lastName(rp.getLastName())
                    .country(rp.getCountry())
                    .image(rp.getImage())
                    .isAccepted(false)
                    .build();
            invitedFriendsList.add(invitedFriend);
        });

        var newVibe = Vibe.builder()
                .id(UUID.randomUUID().toString())
                .keycloakId(keycloakId)
                .name(vibeDto.getName())
                .startDate(vibeDto.getStartDate())
                .endDate(vibeDto.getEndDate())
                .startTime(vibeDto.getStartTime())
                .endTime(vibeDto.getEndTime())
                .invitedFriends(invitedFriendsList)
                .isPaid(vibeDto.getIsPaid())
                .isPrivate(vibeDto.getIsPrivate())
                .vibeAuthor(author)
                .vibeImage(vibeDto.getVibeImage())
                .vibeDescription(vibeDto.getVibeDescription())
                .vibeGeoLocation(geoLocation)
                .addToCalendar(vibeDto.getAddToCalendar())
                .build();

        vibeDto.getTags().forEach(t -> {
            var newTag = VibeTags.builder()
                    .id(t.getId())
                    .keycloakId(keycloakId)
                    .vibeId(newVibe.getId())
                    .tagName(t.getTagName())
                    .build();
            newTagList.add(newTag);
        });
        newVibe.setTags(newTagList);
        vibeRepository.save(newVibe);
        vibeTagsRepository.saveAll(newTagList);

        var deckVibes = DeckVibes.builder()
                .id(UUID.randomUUID().toString())
                .keycloakId(keycloakId)
                .myVibes(List.of(newVibe))
                .favouriteVibes(new ArrayList<>())
                .invitedToVibe(new ArrayList<>())
                .build();
        deckVibesRepository.save(deckVibes);

        if(vibeDto.getAddToCalendar().equals(true)){
            //post vibe to calendar service
        }



        //pushNotificationService.sendVibeInviteNotification(sp.getProfileId());
        //TODO call endpoint notifications and send profileId

        return newVibe;
    }

    @Override
    public Vibe editVibe(String vibeId, VibeDto vibeDto, String keycloakId) {
        var psi = getProfileShortInfo(keycloakId);
        List<ProfileShortInfo> receiversProfiles = getReceiversProfileInfo(vibeDto);
        var existingVibe = vibeRepository.findById(vibeId)
                .orElseThrow(() -> new NotFoundException(String.format("Vibe with vibeId %s was not found", vibeId)));
        var alreadyInvitedFriends = existingVibe.getInvitedFriends();
        List<InvitedFriends> friendsToInvite = new ArrayList<>();

        if(vibeDto.getTags() == null){
            vibeDto.setTags(new ArrayList<>());
        }

        receiversProfiles.forEach(rp -> {
            var invitedFriend = InvitedFriends.builder()
                    .firstName(rp.getFirstName())
                    .lastName(rp.getLastName())
                    .country(rp.getCountry())
                    .image(rp.getImage())
                    .isAccepted(false)
                    .build();
            friendsToInvite.add(invitedFriend);

        });

        List<VibeTags> addTags = vibeDto.getTags().stream().filter(o1 -> existingVibe.getTags().stream()
                        .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                .collect(Collectors.toList());

        List<VibeTags> removeTags = existingVibe.getTags().stream().filter(o1 -> vibeDto.getTags().stream()
                        .noneMatch(o2 -> o2.getTagName().name().equals(o1.getTagName().name())))
                .collect(Collectors.toList());

        existingVibe.getTags().removeAll(removeTags);
        vibeTagsRepository.deleteAll(removeTags);

        addTags.forEach(t -> {
            t.setVibeId(vibeId);
        });

        existingVibe.getTags().addAll(addTags);
        vibeTagsRepository.saveAll(addTags);

        List<InvitedFriends> addFriends = friendsToInvite.stream()
                .filter(o1 -> alreadyInvitedFriends.stream()
                        .noneMatch(o2 -> o2.getKeycloakId().equals(o1.getKeycloakId())))
                .collect(Collectors.toList());

        List<InvitedFriends> removeFriends = alreadyInvitedFriends.stream().filter(o1 -> friendsToInvite.stream()
                        .noneMatch(o2 -> o2.getKeycloakId().equals(o1.getKeycloakId())))
                .collect(Collectors.toList());

        alreadyInvitedFriends.addAll(addFriends);
        alreadyInvitedFriends.removeAll(removeFriends);

        var geoLocation = VibeGeoLocation.builder()
                .type(vibeDto.getVibeGeoLocation().getType())
                .coordinates(vibeDto.getVibeGeoLocation().getCoordinates())
                .build();

        var author = Author.builder()
                .firstName(psi.getFirstName())
                .lastName(psi.getLastName())
                .image(psi.getImage())
                .country(psi.getCountry())
                .currentPosition(psi.getCurrentPosition())
                .profileId(psi.getProfileId())
                .keycloakId(keycloakId)
                .build();

        existingVibe.setName(vibeDto.getName());
        existingVibe.setStartDate(vibeDto.getStartDate());
        existingVibe.setEndDate(vibeDto.getEndDate());
        existingVibe.setStartTime(vibeDto.getStartTime());
        existingVibe.setEndTime(vibeDto.getEndTime());
        existingVibe.setInvitedFriends(alreadyInvitedFriends);
        existingVibe.setIsPaid(vibeDto.getIsPaid());
        existingVibe.setIsPrivate(vibeDto.getIsPrivate());
        existingVibe.setVibeAuthor(author);
        existingVibe.setVibeImage(vibeDto.getVibeImage());
        existingVibe.setVibeDescription(vibeDto.getVibeDescription());
        existingVibe.setVibeGeoLocation(geoLocation);

        vibeRepository.save(existingVibe);
        //pushNotificationService.sendVibeInviteNotification(sp.getProfileId());
        //TODO call endpoint notifications and send profileId

        return existingVibe;
    }

    @Override
    public void deleteVibe(String vibeId, String keycloakId) {
        var existingVibe = vibeRepository.findByIdAndVibeAuthor_KeycloakId(vibeId, keycloakId);
        existingVibe.ifPresentOrElse(vibeRepository::delete,
                () -> { throw new NotFoundException(String.format("Vibe with vibeId %s was not found", vibeId));});
    }

    @Override
    public Stream<Vibe> filterByTags(List<String> vibeTags, PageRequest page, String keycloakId) {
        return vibeRepository.findVibeByTag(vibeTags, page);
    }

    @Override
    public List<InvitedFriends> getListOfFriends(String vibeId, String keycloakId) {
        var vibe = vibeRepository.findById(vibeId)
                .orElseThrow(() -> new NotFoundException(String.format("Vibe %s does not exist", vibeId)));

        return vibe.getInvitedFriends();
    }

    @Override
    public List<Vibe> getVibesCoordinatesNearby(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId, PageRequest paging) {
        Point basePoint = new Point(athleteCoordinatesDTO.getLongitude(), athleteCoordinatesDTO.getLatitude());
        Distance radius = new Distance(athleteCoordinatesDTO.getRadius(), Metrics.KILOMETERS);
        //Circle area = new Circle(basePoint, radius);

        var vibeList = vibeRepository.findByVibeGeoLocationGeometry(basePoint, athleteCoordinatesDTO.getRadius(), athleteCoordinatesDTO.getTags(), paging);

        //var vibeList = vibeRepository.findByVibeGeoLocationCoordinatesWithin(area);
        /*List<Vibe> filteredVibes = vibeList.stream().filter(v -> v.getTags().stream()
                .anyMatch(t -> athleteCoordinatesDTO.getTags().contains(t.getTagName().name()))
        ).collect(Collectors.toList());

        List<Vibe> filteredVibes = vibeList.stream().filter(v -> v.getTags().stream()
               .anyMatch(t -> t.getTagName().name().contains(athleteCoordinatesDTO.getVibeTags()))
        );*/
        
        
        final List<Vibe> vibeCoordinatesListUpdated = new ArrayList<>();
        vibeList.forEach(vibe -> {
                var psi = vibeRepository.findById(vibe.getId())
                        .orElseThrow(() -> new NotFoundException(String.format("Vibe %s not found", vibe.getId())));;
                    vibeCoordinatesListUpdated.add(psi);
            });

        return vibeCoordinatesListUpdated;
    }

    @Override
    public Vibe getVibeById(String vibeId, String keycloakId) {
        return vibeRepository.findById(vibeId)
                .orElseThrow(() -> new NotFoundException(String.format("Vibe with vibeId %s was not found", vibeId)));
    }

    @Override
    public Map<String, Object> getAllFavouriteVibes(PageRequest paging, String keycloakId) {
        var vibes = favouriteVibesRepository.findAllByKeycloakId(keycloakId, paging);
        return populateFavouriteVibeMapResponse(vibes);
    }

    @Override
    public void saveUnsavedVibeToFavorites(String vibeId, String keycloakId) {
        checkIfUserExists(keycloakId);
        var deckVibes = getUserDeckVibes(keycloakId);
        var favouriteVibes = getUserFavouriteVibes(vibeId, keycloakId);

        favouriteVibes.ifPresentOrElse(fv -> {
            deckVibes.ifPresentOrElse(dl -> {
                dl.getFavouriteVibes().remove(fv);
                deckVibesRepository.save(dl);
                favouriteVibesRepository.delete(fv);
            }, () -> {
                var deckVibe = DeckVibes.builder()
                        .id(UUID.randomUUID().toString())
                        .keycloakId(keycloakId)
                        .myVibes(new ArrayList<>())
                        .favouriteVibes(List.of(fv))
                        .build();
                deckVibesRepository.save(deckVibe);
            });

        }, () -> {
            var newFavoriteVibe = FavouriteVibes.builder()
                    .id(UUID.randomUUID().toString())
                    .vibeId(vibeId)
                    .keycloakId(keycloakId)
                    .build();

            deckVibes.ifPresentOrElse(dv -> {
                dv.getFavouriteVibes().add(newFavoriteVibe);
                deckVibesRepository.save(dv);
                favouriteVibesRepository.save(newFavoriteVibe);
            }, () -> {
                var deckLocation = DeckVibes.builder()
                        .id(UUID.randomUUID().toString())
                        .keycloakId(keycloakId)
                        .favouriteVibes(List.of(newFavoriteVibe))
                        .build();
                deckVibesRepository.save(deckLocation);
                favouriteVibesRepository.save(newFavoriteVibe);
            });
        });
    }

    @Override
    public Map<String, Object> getAllUserVibes(PageRequest paging, String keycloakId) {
        var vibes = vibeRepository.findAllByKeycloakId(keycloakId, paging);
        return populateVibeMapResponse(vibes);
    }

    private void checkIfUserExists(String keycloakId) {
        profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s does not exist", keycloakId)));
    }

    private Optional<DeckVibes> getUserDeckVibes(String keycloakId) {
        return deckVibesRepository.findByKeycloakId(keycloakId);
    }

    private Optional<FavouriteVibes> getUserFavouriteVibes(String vibeId, String keycloakId) {
        return favouriteVibesRepository.findByVibeIdAndKeycloakId(vibeId, keycloakId);
    }

    private List<ProfileShortInfo> getReceiversProfileInfo(VibeDto vibeDto) {
        return profileService.getListOfShortProfileInfo(vibeDto.getFriendsId());
    }

    private ProfileShortInfo getProfileShortInfo(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s was not found", keycloakId)));
    }

    private Map<String, Object> populateVibeMapResponse(Page<Vibe> vibes) {
        Map<String, Object> response = new HashMap<>();
        response.put("vibes", vibes.getContent());
        response.put("currentPage", vibes.getNumber());
        response.put("totalItems", vibes.getTotalElements());
        response.put("totalPages", vibes.getTotalPages());
        response.put("hasPrevious", vibes.hasPrevious());
        response.put("hasNext", vibes.hasNext());
        return response;
    }

    private Map<String, Object> populateFavouriteVibeMapResponse(Page<FavouriteVibes> vibes) {
        Map<String, Object> response = new HashMap<>();
        response.put("vibes", vibes.getContent());
        response.put("currentPage", vibes.getNumber());
        response.put("totalItems", vibes.getTotalElements());
        response.put("totalPages", vibes.getTotalPages());
        response.put("hasPrevious", vibes.hasPrevious());
        response.put("hasNext", vibes.hasNext());
        return response;
    }

}
