package com.wevolv.userservice.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wevolv.userservice.exception.QRCodeGenerationException;
import com.wevolv.userservice.exception.UserAlreadyExistsException;
import com.wevolv.userservice.exception.UserNotFoundException;
import com.wevolv.userservice.model.User;
import com.wevolv.userservice.model.dto.PhoneNumberEditDto;
import com.wevolv.userservice.model.dto.RegistrationRequestDto;
import com.wevolv.userservice.model.dto.UpdateDeviceTokenDto;
import com.wevolv.userservice.model.dto.UpdatePasswordResponseDto;
import com.wevolv.userservice.repository.UserRepository;
import com.wevolv.userservice.service.UserService;
import com.wevolv.userservice.util.PasswordStorage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String REALM;

    @Value("${keycloak.auth-server-url}")
    private String AUTH_URL;

    @Value("${admin.username}")
    private String ADMIN_USERNAME;

    @Value("${admin.password}")
    private String ADMIN_PASSWORD;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> addUser(RegistrationRequestDto registrationRequestDto) {
        Optional<User> userOptional = userRepository.findByEmail(registrationRequestDto.getEmail());
        userOptional.ifPresent(e -> {
            throw new UserAlreadyExistsException(String.format("User with email %s already exists", registrationRequestDto.getEmail()));
        });
        User user = User.builder()
                .email(registrationRequestDto.getEmail())
                .firstName(registrationRequestDto.getFirstName())
                .lastName(registrationRequestDto.getLastName())
                .username(registrationRequestDto.getUsername())
                .phoneNumber(registrationRequestDto.getPhoneNumber())
                .password(registrationRequestDto.getPassword())
                .appleId(registrationRequestDto.getAppleUserId())
                .googleId(registrationRequestDto.getGoogleUserId())
                .keycloakId(registrationRequestDto.getKeycloakId())
                .gender(registrationRequestDto.getGender())
                .emailVerified(false)
                .enabled(false)
                .deviceToken(registrationRequestDto.getDeviceToken())
                .build();
        // Add default group passed on registration time
        if (registrationRequestDto.getGroup() != null && !registrationRequestDto.getGroup().isEmpty()) {
            user.setGroups(Arrays.asList(registrationRequestDto.getGroup()));
        }
        userRepository.save(user);
        return Optional.of(user);
    }

    @Override
    public String activateUser(String keycloakId) {
        Optional<User> user = userRepository.findByKeycloakId(keycloakId);
        user.ifPresentOrElse(u -> {
            u.setEnabled(true);
            u.setEmailVerified(true);
            userRepository.save(u);
        }, () -> {
            throw new UserNotFoundException(String.format("User with keycloakId %s is not found", keycloakId));
        });
        return keycloakId;
    }

    @Override
    public Optional<User> updatePassword(UpdatePasswordResponseDto dto) {
        Optional<User> user = Optional.of(userRepository.findByKeycloakId(dto.getKeycloakId())
                .orElseThrow(() -> new UserAlreadyExistsException(String.format("User with keycloakId %s is not found", dto.getKeycloakId()))));
        String hashedPassword = hashUserPassword(dto.getPassword());
        user.ifPresent(u -> {
            u.setPassword(hashedPassword);
            userRepository.save(u);
        });
        return user;
    }

    @Override
    public String deleteUserByEmail(String email) {
        UsersResource usersResource = getKeycloakUserResource();
        Optional<User> userInMongo = Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("Unable to find user with %s email", email))));

        Optional<UserRepresentation> userInKeycloak = Optional.ofNullable(findInKeycloakByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("Unable to find user with %s email", email))));

        userInKeycloak.ifPresent(uk -> {
            usersResource.delete(uk.getId());
            userInMongo.ifPresent(userRepository::delete);
        });
        return email;
    }

    @Override
    public void editPhoneNumber(String keycloakId, PhoneNumberEditDto phoneNumberEditDto) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));
        user.setPhoneNumber(phoneNumberEditDto.getPhoneNumber());
        userRepository.save(user);
    }

    @Override
    public Optional<User> updateDeviceToken(UpdateDeviceTokenDto updateDeviceTokenDto) {
        Optional<User> user = Optional.of(userRepository.findByKeycloakId(updateDeviceTokenDto.getKeycloakId())
                .orElseThrow(() -> new UserAlreadyExistsException(String.format("User with keycloakId %s is not found", updateDeviceTokenDto.getKeycloakId()))));

        user.ifPresent(u -> {
            u.setDeviceToken(updateDeviceTokenDto.getDeviceToken());
            userRepository.save(u);
        });
        return user;
    }

    private String hashUserPassword(String password) {
        try {
            return PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            log.warn("Unable to hash password due to {}", e.getMessage());
        }
        return null;
    }

    private UsersResource getKeycloakUserResource() {
        Keycloak keycloak = KeycloakBuilder.builder().serverUrl(AUTH_URL)
                .grantType(OAuth2Constants.PASSWORD).realm("master").clientId("admin-cli")
                .username(ADMIN_USERNAME).password(ADMIN_PASSWORD)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        RealmResource realmResource = keycloak.realm(REALM);
        return realmResource.users();
    }

    public Optional<UserRepresentation> findInKeycloakByEmail(String email) {
        List<UserRepresentation> search = findUserInKeycloakByUsername(email);
        UserRepresentation existingUser = search.stream()
                .filter(eu -> eu.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s does not exist in Keycloak database", email)));

        return Optional.of(existingUser);
    }

    private RealmResource getRealmResource() {
        Keycloak kc = KeycloakBuilder.builder().serverUrl(AUTH_URL).realm("master").username(ADMIN_USERNAME).password(ADMIN_PASSWORD)
                .clientId("admin-cli").resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        return kc.realm(REALM);
    }

    public List<UserRepresentation> findUserInKeycloakByUsername(String email) {
        return getRealmResource().users().search(email);
    }

    @Override
    public byte[] generateQrCode(String qrCodeContent, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, width, height);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (com.google.zxing.WriterException | IOException e) {
            log.error(e.getMessage(), e);
            throw new QRCodeGenerationException("Error in generating QR code");
        }

    }

    @Override
    public void addUserToGroup(String keycloakUserId, String groupName) {
        var user = userRepository.findByKeycloakId(keycloakUserId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));
        if (user.getGroups() != null) {
            user.getGroups().add(groupName);
        } else {
            user.setGroups(Arrays.asList(groupName));
        }
        userRepository.save(user);
    }

    @Override
    public void removeUserFromGroup(String keycloakUserId, String groupName) {
        var user = userRepository.findByKeycloakId(keycloakUserId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));
        if (user.getGroups() != null) {
            user.getGroups().remove(groupName);
        }

        userRepository.save(user);
    }
}
