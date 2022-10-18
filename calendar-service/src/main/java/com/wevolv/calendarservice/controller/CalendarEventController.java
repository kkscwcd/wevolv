package com.wevolv.calendarservice.controller;

import com.wevolv.calendarservice.model.Event;
import com.wevolv.calendarservice.model.dto.EventDto;
import com.wevolv.calendarservice.service.CalendarService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.wevolv.calendarservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class CalendarEventController {
    private final CalendarService calendarService;

    public CalendarEventController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping(value = "/create/event")
    public Event createEvent(HttpServletRequest request, @RequestBody EventDto eventDto) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var event = calendarService.createEvent(eventDto, keycloakId);

        return event;
    }

    @DeleteMapping(value = "/delete/event/{eventId}")
    public void deleteEvent(HttpServletRequest request, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        calendarService.deleteEvent(keycloakId, eventId);
    }

    @PostMapping(value = "/update/event/{eventId}")
    public Event updateEvent(HttpServletRequest request, @RequestBody EventDto eventDto, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var event = calendarService.updateEvent(eventDto, keycloakId, eventId);

        return event;
    }

    @GetMapping(value = "/all/events")
    public List<Event> getAllEvents(HttpServletRequest request){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        return  calendarService.getAllEvents(keycloakId);
    }

    @GetMapping(value = "/get/event/{eventId}")
    public Event getEvent(HttpServletRequest request, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);
        return  calendarService.getEvent(eventId);
    }
}
