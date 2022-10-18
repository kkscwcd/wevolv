package com.wevolv.payment.integration.service;

import com.wevolv.payment.integration.model.ProfileShortInfo;

import java.util.Optional;

public interface ProfileService {
    Optional<ProfileShortInfo> userShortProfileByKeycloakId(String keycloakId);
}
