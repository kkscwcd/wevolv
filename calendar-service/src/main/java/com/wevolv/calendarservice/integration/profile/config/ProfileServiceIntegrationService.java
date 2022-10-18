package com.wevolv.calendarservice.integration.profile.config;

import com.wevolv.calendarservice.integration.profile.service.impl.HttpProfileServiceImpl;
import com.wevolv.calendarservice.integration.profile.service.ProfileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProfileServiceIntegrationService {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ProfileService getProfileService(RestTemplate getRestTemplate){
        return new HttpProfileServiceImpl(getRestTemplate);
    }
}
