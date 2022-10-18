package com.wevolv.calendarservice.integration.profile.service;

import com.wevolv.calendarservice.model.ProfileShortInfo;

import java.util.List;
import java.util.Optional;

public interface ProfileService {
    Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId);

    List<ProfileShortInfo> getListOfShortProfileInfo(List<String> receiverKeycloakId);
}
