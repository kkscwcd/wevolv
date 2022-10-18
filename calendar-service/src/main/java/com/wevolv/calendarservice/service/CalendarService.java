package com.wevolv.calendarservice.service;

import com.wevolv.calendarservice.model.Event;
import com.wevolv.calendarservice.model.dto.EventDto;
import com.wevolv.calendarservice.model.dto.UnionEventDto;

import java.util.List;

public interface CalendarService {
    Event createEvent(EventDto eventDto, String keycloakId);

    void deleteEvent(String keycloakId, String eventId);

    Event updateEvent(EventDto eventDto, String keycloakId, String eventId);

    List<Event> getAllEvents(String keycloakId);

    Event getEvent(String eventId);

    Event createUnionEvent(UnionEventDto eventDto, String keycloakId);

    void deleteUnionEvent(String keycloakId, String eventId);

    Event updateUnionEvent(UnionEventDto unionEventDto, String keycloakId, String eventId);

    List<Event> getAllUnionEvents(String keycloakId);

    Event getUnionEvent(String eventId);
}
