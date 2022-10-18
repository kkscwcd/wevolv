package com.wevolv.payment.integration.config;

import com.wevolv.payment.integration.service.ProfileService;
import com.wevolv.payment.integration.service.impl.ProfileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationConfig {

    @Bean
    public ProfileService profileService(final RestTemplate restTemplate) {
        return new ProfileServiceImpl(restTemplate);
    }
}
