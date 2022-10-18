package com.wevolv.wevibeservice.integration.calendar.service.impl;

import com.wevolv.wevibeservice.integration.calendar.service.CalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class CalendarServiceImpl implements CalendarService {

 /*   @Value("${services.calendar-service.url}")
    private String CALENDAR_SERVICE;
*/
    private final RestTemplate restTemplate;

    public CalendarServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
