package com.wevolv.registration.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ProfileServiceException extends RuntimeException{
    public ProfileServiceException(String message) {
        super(message);
    }
}