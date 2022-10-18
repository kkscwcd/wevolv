package com.wevolv.registration.integration.google.service;

import com.wevolv.registration.integration.google.model.dto.GoogleSignInRequetDto;
import com.wevolv.registration.model.dto.RegistrationResponseDto;

public interface GoogleService {

    RegistrationResponseDto registerUser(String keycloakId,GoogleSignInRequetDto googleSignInRequetDto);
}
