package com.wevolv.userservice.controller;

import com.wevolv.userservice.exception.UserNotFoundException;
import com.wevolv.userservice.model.GenericApiResponse;
import com.wevolv.userservice.model.User;
import com.wevolv.userservice.model.dto.PhoneNumberEditDto;
import com.wevolv.userservice.model.dto.RegistrationRequestDto;
import com.wevolv.userservice.model.dto.UpdateDeviceTokenDto;
import com.wevolv.userservice.model.dto.UpdatePasswordResponseDto;
import com.wevolv.userservice.repository.UserRepository;
import com.wevolv.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Value("${invite.base-url}")
    private String inviteBaseUrl;
    @Value("${invite.qr-code-height}")
    private int qrCodeHeight;
    @Value("${invite.qr-code-width}")
    private int qrCodeWidth;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> getUserByEmail(@PathVariable String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("Unable to find user with %s email", email))));

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User received.")
                .build();
        user.ifPresent(apiResponse::setResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/{keycloakId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> getUserByUserId(@PathVariable String keycloakId) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Unable to find user with %s keycloakId", keycloakId))));

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User received.")
                .build();
        user.ifPresent(apiResponse::setResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> createUser(@RequestBody RegistrationRequestDto registrationRequestDto) {
        Optional<User> user = userService.addUser(registrationRequestDto);

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User created in database.")
                .build();
        user.ifPresent(apiResponse::setResponse);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping(value = "/delete/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> deleteByEmail(@PathVariable String email) {
        String keycloakIdDeleted = userService.deleteUserByEmail(email);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("User with email %s  is deleted from database.", keycloakIdDeleted))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/activate/user/{keycloakId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> activateUser(@PathVariable String keycloakId) {
        String userId = userService.activateUser(keycloakId);
        return ResponseEntity.ok(GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(userId)
                .message(String.format("User with keycloakId %s is updated.", keycloakId))
                .build());
    }

    @PostMapping(value = "/update/password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> updatePasswordUser(@RequestBody UpdatePasswordResponseDto updatePassword) {
        Optional<User> user = userService.updatePassword(updatePassword);

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .build();

        user.ifPresent(u -> {
            apiResponse.setResponse(u);
            apiResponse.setMessage(String.format("Password for user with keycloakId %s is successfully updated.", u.getKeycloakId()));
        });

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/edit/phoneNumber/{keycloakId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editPhoneNumber(@PathVariable String keycloakId, @RequestBody PhoneNumberEditDto phoneNumberEditDto) {
        userService.editPhoneNumber(keycloakId, phoneNumberEditDto);
    }

    @PostMapping(value = "/update/deviceToken", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> updateDeviceToken(@RequestBody UpdateDeviceTokenDto updateDeviceTokenDto) {
        Optional<User> user = userService.updateDeviceToken(updateDeviceTokenDto);

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .build();

        user.ifPresent(u -> {
            apiResponse.setResponse(u);
            apiResponse.setMessage(String.format("DeviceToken for user with keycloakId %s is successfully updated.", u.getKeycloakId()));
        });

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/user/group/add/{keycloakId}/{groupName}")
    public void addUserToGroup(@PathVariable String keycloakId, @PathVariable String groupName) {
        userService.addUserToGroup(keycloakId, groupName);
    }

    @PostMapping(value = "/user/group/remove/{keycloakId}/{groupName}")
    public void removeUserFromGroup(@PathVariable String keycloakId, @PathVariable String groupName) {
        userService.removeUserFromGroup(keycloakId, groupName);
    }

    @GetMapping(value = "/invite/qrCode/{group}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> generateInviteQR(@PathVariable String group) {
        byte[] qrCode = userService.generateQrCode(inviteBaseUrl + "?group=" + group, qrCodeHeight, qrCodeWidth);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
    }

    @GetMapping(value = "/invite/link/{group}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> generateInviteLink(@PathVariable String group) {
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(inviteBaseUrl + "?group=" + group)
                .message(String.format("Invite link generated to join group %s", group))
                .build();
        return ResponseEntity.ok(apiResponse);

    }

}
