package com.wevolv.registration.model.dto;


import com.wevolv.registration.model.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    private String accessToken;

    private String tokenType;

    private String refreshToken;

    private String expires_in;

    private String scope;

    private UserInfoDto userInfo;

}
