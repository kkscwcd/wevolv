package com.wevolv.registration.exception;

public class TwilioServiceException extends RuntimeException{
    public TwilioServiceException(String message) {
        super(message);
    }
}