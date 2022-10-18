package com.wevolv.registration.integration.google.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenDto {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String id_token;
    private String refresh_token;
    private String scope;
}
