package com.wevolv.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class GeneralException extends RuntimeException{
    public GeneralException(String message) {
        super(message);
    }
}
