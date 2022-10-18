package com.wevolv.registration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wevolv.registration.integration.apple.service.AppleService;
import com.wevolv.registration.integration.apple.service.impl.AppleServiceImpl;
import com.wevolv.registration.integration.google.service.GoogleService;
import com.wevolv.registration.integration.google.service.impl.GoogleServiceImpl;
import com.wevolv.registration.integration.keycloak.KeycloakService;
import com.wevolv.registration.integration.keycloak.service.impl.KeycloakServiceImpl;
import com.wevolv.registration.integration.profile.service.ProfileService;
import com.wevolv.registration.integration.profile.service.impl.ProfileServiceImp;
import com.wevolv.registration.integration.twilio.service.TwilioService;
import com.wevolv.registration.integration.twilio.service.impl.TwilioServiceImpl;

import com.wevolv.registration.service.UserService;
import com.wevolv.registration.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RegistrationServiceIntegrationConfiguration {

    @Bean
    RestTemplate getRestTemplate(){ return new RestTemplate();}

    @Bean
    public ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }
//
//    @Bean
//    UserService getUserService(RestTemplate getRestTemplate){
//        return new UserServiceImpl(getRestTemplate);
//    }
//
//    @Bean
//    ProfileService getProfileService(RestTemplate getRestTemplate){return new ProfileServiceImp(getRestTemplate);}
//
//    @Bean
//    KeycloakService getKeycloakService(UserService getUserService, TwilioService twilioService, ProfileService profileService){
//        return new KeycloakServiceImpl(getUserService, twilioService, profileService);
//    }
//
//    @Bean
//    public TwilioService getCommunicationService(RestTemplate getRestTemplate, ObjectMapper getObjectMapper){
//        return new TwilioServiceImpl(getRestTemplate, getObjectMapper);
//    }
//
//    @Bean
//    public GoogleService getGoogleService(RestTemplate getRestTemplate, KeycloakService keycloakService){
//        return new GoogleServiceImpl(getRestTemplate, keycloakService);
//    }
//
//    @Bean
//    AppleService getAppleService(KeycloakService getKeycloakService, UserService getUserService){
//        return new AppleServiceImpl(getKeycloakService, getUserService);
//    }

}
