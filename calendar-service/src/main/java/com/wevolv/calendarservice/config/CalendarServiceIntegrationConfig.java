package com.wevolv.calendarservice.config;

import com.wevolv.calendarservice.integration.profile.service.ProfileService;
import com.wevolv.calendarservice.repository.CalendarRepository;
import com.wevolv.calendarservice.repository.VibeRepository;
import com.wevolv.calendarservice.service.CalendarService;
import com.wevolv.calendarservice.service.VibeService;
import com.wevolv.calendarservice.service.impl.CalendarServiceImpl;
import com.wevolv.calendarservice.service.impl.VibeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalendarServiceIntegrationConfig {

    @Bean
    CalendarService getCalendarService(CalendarRepository calendarRepository, ProfileService profileService){
        return new CalendarServiceImpl(calendarRepository, profileService);}

    @Bean
    VibeService getVibeService(ProfileService profileService, VibeRepository vibeRepository){
        return new VibeServiceImpl(profileService, vibeRepository);}
}
