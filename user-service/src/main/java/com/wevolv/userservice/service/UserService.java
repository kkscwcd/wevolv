package com.wevolv.userservice.service;

import com.wevolv.userservice.model.User;
import com.wevolv.userservice.model.dto.PhoneNumberEditDto;
import com.wevolv.userservice.model.dto.RegistrationRequestDto;
import com.wevolv.userservice.model.dto.UpdateDeviceTokenDto;
import com.wevolv.userservice.model.dto.UpdatePasswordResponseDto;

import java.util.Optional;

public interface UserService {
    Optional<User> addUser(RegistrationRequestDto registrationRequestDto);
    String activateUser(String keycloakId);
    Optional<User> updatePassword(UpdatePasswordResponseDto dto);
    String deleteUserByEmail(String email);

    void editPhoneNumber(String keycloakId, PhoneNumberEditDto phoneNumberEditDto);
     Optional<User> updateDeviceToken(UpdateDeviceTokenDto updateDeviceTokenDto);
     void addUserToGroup( String keycloakUserId, String groupName);
    void removeUserFromGroup( String keycloakUserId, String groupName);
     byte[] generateQrCode(String qrCodeContent, int width, int height);
}
