package com.wevolv.registration.integration.keycloak;

import com.wevolv.registration.model.dto.RegistrationRequestDto;
import com.wevolv.registration.model.dto.RegistrationResponseDto;
import com.wevolv.registration.model.dto.TokenDto;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface KeycloakService {

    Optional<UserRepresentation> createUserInKeycloak(RegistrationRequestDto registrationRequestDto);
    List<UserRepresentation> findUserInKeycloakByUsername(String email);
    UserResource findUserInKeycloakByUserId(String keycloakUserId);
    RegistrationResponseDto userEmailVerification(String keycloakUserId);
    void addUserToGroup( String keycloakUserId, String groupName);
    void removeUserFromGroup( String keycloakUserId, String groupName);
    TokenDto createSocialUserInKeycloak(RegistrationRequestDto registrationRequestDto);
    TokenDto exchange(String userName) throws Exception;
}