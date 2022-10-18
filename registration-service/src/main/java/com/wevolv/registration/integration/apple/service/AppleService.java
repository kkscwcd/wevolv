package com.wevolv.registration.integration.apple.service;

import com.wevolv.registration.model.dto.TokenDto;

public interface AppleService {
    TokenDto createUser(String jwt);
}
