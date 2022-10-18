package com.wevolv.registration.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String googleUserId;
    private String appleUserId;
    private String keycloakId;
    private Boolean emailVerified = false;
    private Boolean enabled = false;
    private String gender;
    private String deviceToken;
    private List<String> groups;
}
