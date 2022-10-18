package com.wevolv.registration.integration.profile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ProfileInitInfoDto {
    private String firstName;
    private String lastName;
    private String keycloakId;
    private String email;
    private String gender;
}
