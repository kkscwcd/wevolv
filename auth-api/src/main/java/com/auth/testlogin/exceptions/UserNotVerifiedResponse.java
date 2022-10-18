package com.auth.testlogin.exceptions;


public class UserNotVerifiedResponse {
    //TODO put message as well
    private final String message;
    private final String phoneNumber;
    private final String keycloakId;

    public UserNotVerifiedResponse(String message, String phoneNumber, String keycloakId) {
        this.message=message;
        this.phoneNumber = phoneNumber;
        this.keycloakId = keycloakId;
    }

    public String getMessage() {
        return message;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getKeycloakId() {
        return keycloakId;
    }
}