package com.wevolv.filesservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LimitedNumberOfElementsException extends RuntimeException {
    public LimitedNumberOfElementsException(String message) {
        super(message);
    }
}