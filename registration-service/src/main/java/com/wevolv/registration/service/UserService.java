package com.wevolv.registration.service;

import com.wevolv.registration.model.User;
import com.wevolv.registration.model.dto.PhoneNumberEditDto;
import com.wevolv.registration.model.dto.RegistrationRequestDto;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);
    Optional<User> addUserToMongoDB(RegistrationRequestDto registrationRequestDto);
    String activateUser(String keycloakId);

    void editPhoneNumber(PhoneNumberEditDto phoneNumberEditDto, String keycloakId);
    void addUserToGroupInMongoDB( String keycloakId, String groupName);
    void removeUserFromGroupInMongoDB( String keycloakId, String groupName);
}
