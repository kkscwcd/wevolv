package com.wevolv.unionservice.controller;

import com.wevolv.unionservice.integration.calendar.service.CalendarService;
import com.wevolv.unionservice.model.Event;
import com.wevolv.unionservice.model.GenericApiResponse;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.ImageDto;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.ObjectDeletedResponse;
import com.wevolv.unionservice.model.dto.UnionEventDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.wevolv.unionservice.util.TokenDecoder.getUserIdFromToken;

//@CrossOrigin(origins = "https://api.wevolv.net", maxAge = 3600)
@RestController
@RequestMapping("/calendar")
public class EventController {

    private final CalendarService calendarService;

    public EventController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping(value = "/create/union/event")
    public ObjectCreatedResponse createEvent(HttpServletRequest request, @RequestBody UnionEventDto unionEventDto) {
        //TODO check what union event is consist of from design
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        var udc = UserDataContext.create(keycloakId, jwt);

        var event = calendarService.createEvent(udc, unionEventDto);

        return new ObjectCreatedResponse(event.getId());
    }

    @DeleteMapping(value = "/delete/event/{eventId}")
    public ObjectDeletedResponse deleteUnionEvent(HttpServletRequest request, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        var udc = UserDataContext.create(keycloakId, jwt);

        calendarService.deleteEvent(udc, eventId);
        return new ObjectDeletedResponse(true);
    }

    @PostMapping(value = "/update/event/{eventId}")
    public Event updateEvent(HttpServletRequest request, @RequestBody UnionEventDto unionEventDto, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        var udc = UserDataContext.create(keycloakId, jwt);

        return calendarService.updateEvent(unionEventDto, udc, eventId);

    }

    @GetMapping(value = "/all/events")
    public ResponseEntity<GenericApiResponse> getAllEvents(HttpServletRequest request,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "3") int size){

        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        var udc = UserDataContext.create(keycloakId, jwt);
        var paging = PageRequest.of(page, size);

        var events = calendarService.getAllEvents(udc);
        GenericApiResponse apiResponse = GenericApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .response(events)
                .message(String.format("All events for user with keycloakId %s", udc.keycloakId))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/get/event/{eventId}")
    public Event getEvent(HttpServletRequest request, @PathVariable String eventId){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);
        var udc = UserDataContext.create(keycloakId, jwt);

        return calendarService.getEvent(udc, eventId);

    }

    @PostMapping("/add/image/{eventId}")
    public ObjectCreatedResponse saveImageEvent(MultipartFile multipartFile, @PathVariable String eventId,
                                                HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var udc = UserDataContext.create(keycloakId, jwt);
        var event = calendarService.saveImageEvent(multipartFile, eventId, udc);
        return new ObjectCreatedResponse(event.getId());
    }

    @PostMapping("/delete/image/{eventId}")
    public ObjectDeletedResponse deleteImageEvent(@RequestBody ImageDto imageDto, @PathVariable String eventId,
                                                  HttpServletRequest request){
        String jwt = request.getHeader("Authorization");
        String keycloakId = getUserIdFromToken(jwt);

        var udc = UserDataContext.create(keycloakId, jwt);
        calendarService.deleteImageEvent(imageDto, eventId, udc);
        return new ObjectDeletedResponse(true);
    }
}
