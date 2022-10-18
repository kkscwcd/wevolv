package com.wevolv.unionservice.integration.profile.service;

import com.wevolv.unionservice.model.ProfileShortInfo;

import java.util.Optional;

public interface ProfileService {
    Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId);
}
