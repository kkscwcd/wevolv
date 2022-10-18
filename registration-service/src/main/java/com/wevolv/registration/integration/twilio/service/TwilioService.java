package com.wevolv.registration.integration.twilio.service;

public interface TwilioService {
    String sendMailActivateAccount(String email, String userId);
}