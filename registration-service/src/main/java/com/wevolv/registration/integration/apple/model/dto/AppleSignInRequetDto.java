package com.wevolv.registration.integration.apple.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppleSignInRequetDto {
    private String firstName;
    private String lastName;
    private String email;
    private String appleUserId;
}
