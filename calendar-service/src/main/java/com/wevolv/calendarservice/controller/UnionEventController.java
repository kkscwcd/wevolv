package com.wevolv.calendarservice.controller;

import com.wevolv.calendarservice.model.Event;
import com.wevolv.calendarservice.model.GenericApiResponse;
import com.wevolv.calendarservice.model.dto.EventDto;
import com.wevolv.calendarservice.model.dto.ObjectDeletedResponse;
import com.wevolv.calendarservice.model.dto.UnionEventDto;
import com.wevolv.calendarservice.model.dto.UnionEventResponse;
import com.wevolv.calendarservice.service.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.wevolv.calendarservice.util.TokenDecoder.getUserIdFromToken;

@RestController
public class UnionEventController {

    private final CalendarService calendarService;

    public UnionEventController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping(value = "/create/union/event")
    public UnionEventResponse createUnionEvent(HttpServletRequest request, @RequestBody UnionEventDto unionEventDto) {
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var event = calendarService.createUnionEvent(unionEventDto, keycloakId);
        return new UnionEventResponse(event.getId());
    }

    @DeleteMapping(value = "/delete/union/event/{eventId}")
    public ObjectDeletedResponse deleteUnionEvent(HttpServletRequest request, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        calendarService.deleteUnionEvent(keycloakId, eventId);
        return new ObjectDeletedResponse(true);
    }

    @PostMapping(value = "/update/union/event/{eventId}")
    public Event updateUnionEvent(HttpServletRequest request, @RequestBody UnionEventDto unionEventDto, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        return calendarService.updateUnionEvent(unionEventDto, keycloakId, eventId);

    }

    @GetMapping(value = "/all/union/events")
    public List<Event> getAllUnionEvents(HttpServletRequest request){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        return calendarService.getAllUnionEvents(keycloakId);
    }

    @GetMapping(value = "/get/union/event/{eventId}")
    public Event getUnionEvent(HttpServletRequest request, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        getUserIdFromToken(jwt);
        return  calendarService.getUnionEvent(eventId);
    }
}
