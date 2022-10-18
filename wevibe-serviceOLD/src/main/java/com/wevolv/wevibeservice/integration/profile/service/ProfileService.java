package com.wevolv.wevibeservice.integration.profile.service;

import com.wevolv.wevibeservice.domain.model.Vibe;
import com.wevolv.wevibeservice.domain.model.dto.AthleteTagFilterDto;
import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface ProfileService {
    Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId);

    List<ProfileShortInfo> getListOfShortProfileInfo(List<String> receiverKeycloakId);

    List<ProfileShortInfo> filterAthletes(AthleteTagFilterDto vibeTagFilterDto);
}
