package com.wevolv.registration.integration.profile.service;

import com.wevolv.registration.model.dto.RegistrationRequestDto;

public interface ProfileService {
    String createInitialUserProfile(RegistrationRequestDto registrationRequestDto);
}
