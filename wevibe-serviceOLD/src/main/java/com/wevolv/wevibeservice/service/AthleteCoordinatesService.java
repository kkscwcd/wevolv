package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.AthleteCoordinates;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import com.wevolv.wevibeservice.domain.model.dto.AthleteTagFilterDto;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface AthleteCoordinatesService {
    Optional<AthleteCoordinates> saveAthletesCoordinates(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId);
    Optional<AthleteCoordinates> getAllAthletesCoordinates(String keycloakId);
    Optional<List<AthleteCoordinates>> getAthletesCoordinatesNearby(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId);

    List<ProfileShortInfo> filterByTags(AthleteTagFilterDto vibeTagFilterDto);
}
