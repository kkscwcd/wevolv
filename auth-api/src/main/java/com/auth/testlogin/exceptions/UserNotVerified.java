package com.auth.testlogin.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotVerified extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private final String phoneNumber;
    private final String message;

    private final String keycloakId;

    public UserNotVerified(String message, String phoneNumber, String keycloakId) {
        super(message);
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.keycloakId = keycloakId;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getKeycloakId(){
        return keycloakId;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
}
