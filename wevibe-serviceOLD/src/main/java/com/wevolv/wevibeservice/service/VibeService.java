package com.wevolv.wevibeservice.service;

import com.wevolv.wevibeservice.domain.model.InvitedFriends;
import com.wevolv.wevibeservice.domain.model.Location;
import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.AthleteCoordinatesDTO;
import com.wevolv.wevibeservice.domain.model.dto.ListOfProfileShortInfoDto;
import com.wevolv.wevibeservice.domain.model.dto.VibeDto;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface VibeService {

    Vibe saveVibe(VibeDto vibeDto, String keycloakId);

    Vibe editVibe(String vibeId, VibeDto vibeDto, String keycloakId);

    void deleteVibe(String vibeId, String keycloakId);

    Stream<Vibe> filterByTags(List<String> vibeTags, PageRequest page, String keycloakId);

    List<InvitedFriends> getListOfFriends(String vibeId, String keycloakId);

    List<Vibe> getVibesCoordinatesNearby(AthleteCoordinatesDTO athleteCoordinatesDTO, String keycloakId, PageRequest paging);

    Vibe getVibeById(String vibeId, String keycloakId);

    Map<String, Object> getAllFavouriteVibes(PageRequest paging, String keycloakId);

    void saveUnsavedVibeToFavorites(String vibeId, String keycloakId);

    Map<String, Object> getAllUserVibes(PageRequest paging, String keycloakId);
}
