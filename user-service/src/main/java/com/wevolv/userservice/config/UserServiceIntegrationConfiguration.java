package com.wevolv.userservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wevolv.userservice.repository.UserRepository;
import com.wevolv.userservice.service.UserService;
import com.wevolv.userservice.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceIntegrationConfiguration {

    @Bean
    ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

    @Bean
    UserService getUserService(UserRepository userRepository){
        return new UserServiceImpl(userRepository);
    }
}
