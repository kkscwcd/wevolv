package com.wevolv.registration.model.dto;

import com.wevolv.registration.util.EmailValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.apache.http.util.Asserts.check;
import static org.apache.http.util.Asserts.notBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RegistrationRequestDto {
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
    private String gender;
    private String deviceToken;
    private String group;

    public static void requestValidator(RegistrationRequestDto requestDto){
        try{
            notBlank(requestDto.getUsername(), "username must be set and cannot be blank");
            notBlank(requestDto.getEmail(), "email must be set and cannot be blank");
            notBlank(requestDto.getPassword(), "password must be set and cannot be blank");
            notBlank(requestDto.getConfirmPassword(), "confirm password must be set and cannot be blank");
            notBlank(requestDto.getFirstName(), "first name must be set and cannot be blank");
            notBlank(requestDto.getPhoneNumber(), "phoneNumber must be set and cannot be blank");
            notBlank(requestDto.getGender(), "gender must be set and cannot be blank");
            check(requestDto.getPassword().equals(requestDto.getConfirmPassword()), "Passwords are not the same!");
            check(EmailValidator.test(requestDto.getEmail()), "email is not valid!");
        }catch(Exception e){
            log.warn("Registration fields not correct {} {}", requestDto, e.getMessage());
            throw e;
        }
    }

}
