package com.wevolv.calendarservice.service.impl;

import com.wevolv.calendarservice.exceptions.NotFoundException;
import com.wevolv.calendarservice.integration.profile.service.ProfileService;
import com.wevolv.calendarservice.model.Author;
import com.wevolv.calendarservice.model.ProfileShortInfo;
import com.wevolv.calendarservice.model.Vibe;
import com.wevolv.calendarservice.model.dto.VibeDto;
import com.wevolv.calendarservice.repository.VibeRepository;
import com.wevolv.calendarservice.service.VibeService;

import java.util.List;
import java.util.UUID;

public class VibeServiceImpl implements VibeService {

    private final ProfileService profileService;
    private final VibeRepository vibeRepository;

    public VibeServiceImpl(ProfileService profileService, VibeRepository vibeRepository) {
        this.profileService = profileService;
        this.vibeRepository = vibeRepository;
    }

    @Override
    public Vibe saveVibe(VibeDto vibeDto, String keycloakId) {
        var psi = getProfileShortInfo(keycloakId);

        var author = Author.builder()
                .id(UUID.randomUUID().toString())
                .firstName(psi.getFirstName())
                .lastName(psi.getLastName())
                .image(psi.getImage())
                .build();

        var newVibe = Vibe.builder()
                .id(UUID.randomUUID().toString())
                .name(vibeDto.getName())
                .description(vibeDto.getDescription())
                .keycloakId(keycloakId)
                .startDate(vibeDto.getStartDate())
                .endDate(vibeDto.getEndDate())
                .startTime(vibeDto.getStartTime())
                .endTime(vibeDto.getEndTime())
                .vibeAuthor(author)
                .vibeImage(vibeDto.getVibeImage())
//                .vibeGeoLocation(geoLocation)
                .build();

        vibeRepository.save(newVibe);

        return newVibe;
    }


    @Override
    public Vibe getVibeById(String vibeId, String keycloakId) {
        return vibeRepository.findById(vibeId)
                .orElseThrow(() -> new NotFoundException(String.format("Vibe with vibeId %s was not found", vibeId)));
    }

    @Override
    public List<Vibe> getAllVibes(String keycloakId) {
        return vibeRepository.findAllByKeycloakId(keycloakId);
    }

    @Override
    public void deleteVibe(String vibeId) {
         vibeRepository.deleteById(vibeId);
    }

    private ProfileShortInfo getProfileShortInfo(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s was not found", keycloakId)));
    }
}
