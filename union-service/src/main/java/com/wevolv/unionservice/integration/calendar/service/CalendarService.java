package com.wevolv.unionservice.integration.calendar.service;

import com.wevolv.unionservice.model.Event;
import com.wevolv.unionservice.model.UserDataContext;
import com.wevolv.unionservice.model.dto.ImageDto;
import com.wevolv.unionservice.model.dto.ObjectCreatedResponse;
import com.wevolv.unionservice.model.dto.UnionEventDto;
import com.wevolv.unionservice.model.dto.UnionEventResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CalendarService {

    Event createEvent(UserDataContext udc, UnionEventDto unionEventDto);

    void deleteEvent(UserDataContext udc, String eventId);

    Event updateEvent(UnionEventDto unionEventDto, UserDataContext udc, String eventId);

    List<Event> getAllEvents(UserDataContext udc);

    Event getEvent(UserDataContext udc, String eventId);

    Event saveImageEvent(MultipartFile multipartFile, String eventId, UserDataContext userDataContext);

    void deleteImageEvent(ImageDto imageDto, String eventId, UserDataContext udc);
}
