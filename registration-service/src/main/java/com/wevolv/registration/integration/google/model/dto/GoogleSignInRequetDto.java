package com.wevolv.registration.integration.google.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleSignInRequetDto {
    private String firstName;
    private String lastName;
    private String email;
    private String googleUserId;
}
