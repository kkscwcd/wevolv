package com.auth.testlogin.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordDto {


    /**
     * new password
     */
    private String password;

    /**
     * password confirm
     */
    private String confirm;

    /**
     * token form Twilio Service
     */
    private String token;

}
