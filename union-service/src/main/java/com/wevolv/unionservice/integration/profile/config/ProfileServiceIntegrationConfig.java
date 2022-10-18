package com.wevolv.unionservice.integration.profile.config;

import com.wevolv.unionservice.integration.profile.service.impl.HttpProfileServiceImpl;
import com.wevolv.unionservice.integration.profile.service.ProfileService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ProfileServiceIntegrationConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    ProfileService getProfileService(RestTemplate getRestTemplate){
        return new HttpProfileServiceImpl(getRestTemplate);
    }
}
