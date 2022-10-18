package com.wevolv.userservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String confirmPassword;
    private String username;
    private String googleUserId;
    private String appleUserId;
    private String keycloakId;
    private String gender;
    private String deviceToken;
    private String group;
}
