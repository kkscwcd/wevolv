package com.auth.testlogin.integration.user.config;

import com.auth.testlogin.integration.user.service.UserService;
import com.auth.testlogin.integration.user.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class UserServiceIntegrationConfiguration {

    @Bean
    RestTemplate getRestTemplate(){ return new RestTemplate();}

    @Bean
    UserService getUserService(RestTemplate getRestTemplate){
        return new UserServiceImpl(getRestTemplate);
    }
}
