package com.wevolv.wevibeservice.integration;

import com.wevolv.wevibeservice.integration.calendar.service.CalendarService;
import com.wevolv.wevibeservice.integration.calendar.service.impl.CalendarServiceImpl;
import com.wevolv.wevibeservice.integration.profile.service.ProfileService;
import com.wevolv.wevibeservice.integration.profile.service.impl.ProfileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class IntegrationConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ProfileService getProfileService(RestTemplate restTemplate){
        return new ProfileServiceImpl(restTemplate);
    }

    @Bean
    CalendarService getCalendarService(RestTemplate restTemplate){
        return new CalendarServiceImpl(restTemplate);
    }

}
