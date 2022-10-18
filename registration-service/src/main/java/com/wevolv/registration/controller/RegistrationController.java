package com.wevolv.registration.controller;

import com.wevolv.registration.model.GenericApiResponse;
import com.wevolv.registration.integration.apple.model.dto.AppleSignInRequetDto;
import com.wevolv.registration.model.dto.PhoneNumberEditDto;
import com.wevolv.registration.model.dto.RegistrationRequestDto;
import com.wevolv.registration.model.dto.RegistrationResponseDto;
import com.wevolv.registration.integration.apple.service.AppleService;
import com.wevolv.registration.integration.google.model.dto.GoogleSignInRequetDto;
import com.wevolv.registration.integration.google.service.GoogleService;
import com.wevolv.registration.integration.keycloak.KeycloakService;
import com.wevolv.registration.model.dto.TokenDto;
import com.wevolv.registration.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.wevolv.registration.util.TokenDecoder.getUserIdFromToken;

@RequestMapping(value = "/registration")
@RestController
public class RegistrationController {

    private final GoogleService googleService;
    private final KeycloakService keycloakService;
    private final AppleService appleService;
    private final UserService userService;

    public RegistrationController(GoogleService googleService, KeycloakService keycloakService, AppleService appleService, UserService userService) {
        this.googleService = googleService;
        this.keycloakService = keycloakService;
        this.appleService = appleService;
        this.userService = userService;
    }

    @PostMapping(value = "/user")
    public ResponseEntity<GenericApiResponse> createUser(@RequestBody RegistrationRequestDto registrationRequestDto) {
        GenericApiResponse genericApiResponse = new GenericApiResponse();


            Optional<UserRepresentation> userRepresentation = keycloakService.createUserInKeycloak(registrationRequestDto);
            if(userRepresentation.isPresent()) {
                RegistrationResponseDto registrationResponseDto = RegistrationResponseDto.builder()
                        .keycloakId(userRepresentation.get().getId())
                        .username(userRepresentation.get().getUsername())
                        .firstName(userRepresentation.get().getFirstName())
                        .lastName(userRepresentation.get().getLastName())
                        .phoneNumber(registrationRequestDto.getPhoneNumber())
                        .email(userRepresentation.get().getEmail())
                        .emailVerified(userRepresentation.get().isEmailVerified())
                        .enabled(userRepresentation.get().isEnabled())
                        .gender(registrationRequestDto.getGender())
                        .deviceToken(registrationRequestDto.getDeviceToken())
                        .groups(userRepresentation.get().getGroups())
                        .build();
            
                genericApiResponse = GenericApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .response(registrationResponseDto)
                        .message("User created.")
                        .build();
            }

        return ResponseEntity.ok(genericApiResponse);
    }

    @PostMapping(value = "/edit/phoneNumber")
    public void editPhoneNumber(@RequestHeader("Authorization") String auth, @RequestBody PhoneNumberEditDto phoneNumberEditDto) {
        String keycloakId = getUserIdFromToken(auth);
        userService.editPhoneNumber(phoneNumberEditDto, keycloakId);
    }



    @PostMapping(value = "/email/verification/{keycloakUserId}")
    public ResponseEntity<GenericApiResponse> emailVerification(@PathVariable String keycloakUserId) {
        RegistrationResponseDto registrationResponseDto = keycloakService.userEmailVerification(keycloakUserId);

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(registrationResponseDto)
                .message("User created.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/apple")
    public ResponseEntity<GenericApiResponse> createUserWithApple(@RequestHeader("Authorization") String auth/*, @RequestBody AppleSignInRequetDto registrationRequestDto*/) {
       // Validate access token
      //  String keycloakId = getUserIdFromToken(auth);
        TokenDto tokenDto = appleService.createUser(auth);

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(tokenDto)
                .message("User created.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/google")
    public ResponseEntity<GenericApiResponse> createUserWithGoogle(@RequestHeader("Authorization") String auth/*,@RequestBody GoogleSignInRequetDto googleSignInRequetDto*/) {
         // Validate access token
//       String keycloakId = getUserIdFromToken(auth);
//        RegistrationResponseDto responseMessage = googleService.registerUser(keycloakId,googleSignInRequetDto);
//        GenericApiResponse apiResponse = GenericApiResponse.builder()
//                .statusCode(HttpStatus.OK.value())
//                .response(responseMessage)
//                .message("User created.")
//                .build();

     TokenDto tokenDto = appleService.createUser(auth);

        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(tokenDto)
                .message("User created.")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
    
    @PostMapping(value = "/user/group/add/{groupName}")
    public ResponseEntity<GenericApiResponse> addUserToGroup(@RequestHeader("Authorization") String auth, @PathVariable String groupName) {
        String keycloakId = getUserIdFromToken(auth);
        keycloakService.addUserToGroup(keycloakId, groupName);
       GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("User added to group %s .",groupName))
                .build();
       return ResponseEntity.ok(apiResponse);
    }
    
       @PostMapping(value = "/user/group/remove/{groupName}")
    public ResponseEntity<GenericApiResponse> removeUserFromGroup(@RequestHeader("Authorization") String auth, @PathVariable String groupName) {
        String keycloakId = getUserIdFromToken(auth);
        keycloakService.removeUserFromGroup(keycloakId, groupName);
       GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(String.format("User removed from group %s .",groupName))
                .build();
       return ResponseEntity.ok(apiResponse);
    }
}
