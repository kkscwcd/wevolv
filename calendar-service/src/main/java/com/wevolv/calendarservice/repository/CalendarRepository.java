package com.wevolv.calendarservice.repository;

import com.wevolv.calendarservice.model.Author;
import com.wevolv.calendarservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends MongoRepository<Event, String> {
    Optional<Event> findByIdAndAuthor_KeycloakId(String eventId, String keycloakId);

    List<Event> findAllByAuthor_KeycloakId(String keycloakId);

    Author findAuthorByAuthor_KeycloakId(String keycloakId);
}
