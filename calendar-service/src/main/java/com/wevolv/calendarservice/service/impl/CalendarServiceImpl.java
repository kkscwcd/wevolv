package com.wevolv.calendarservice.service.impl;

import com.wevolv.calendarservice.exceptions.NotFoundException;
import com.wevolv.calendarservice.integration.profile.service.ProfileService;
import com.wevolv.calendarservice.model.Author;
import com.wevolv.calendarservice.model.Event;
import com.wevolv.calendarservice.model.InvitedFriends;
import com.wevolv.calendarservice.model.ProfileShortInfo;
import com.wevolv.calendarservice.model.dto.EventDto;
import com.wevolv.calendarservice.model.dto.UnionEventDto;
import com.wevolv.calendarservice.repository.CalendarRepository;
import com.wevolv.calendarservice.service.CalendarService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CalendarServiceImpl implements CalendarService {
    private final CalendarRepository calendarRepository;
    private final ProfileService profileService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public CalendarServiceImpl(CalendarRepository calendarRepository, ProfileService profileService) {
        this.calendarRepository = calendarRepository;
        this.profileService = profileService;
    }

    @Override
    public Event createEvent(EventDto eventDto, String keycloakId) {
        var psi = getProfileShortInfo(keycloakId);
        var author = getAuthor(keycloakId, psi);

        List<ProfileShortInfo> receiversProfiles = getReceiversProfileInfo(eventDto);
        List<InvitedFriends> invitedFriendsList = new ArrayList<>();

        receiversProfiles.forEach(rp -> {
            var invitedFriend = InvitedFriends.builder()
                    .profileShortInfo(rp)
                    .isAccepted(false)
                    .build();
            invitedFriendsList.add(invitedFriend);
        });


        Event newEvent = Event.builder()
                .id(UUID.randomUUID().toString())
                .title(eventDto.getTitle())
                .author(author)
                .color(eventDto.getColor())
                .description(eventDto.getDescription())
                .location(eventDto.getLocation())
                .remindBefore(eventDto.getRemindBefore())
                .repeat(eventDto.isRepeat())
                .guests(invitedFriendsList)
                .startDate(LocalDate.parse(eventDto.getStartDate(), formatter))
                .endDate(LocalDate.parse(eventDto.getEndDate(), formatter))
                .build();

        calendarRepository.save(newEvent);
        return newEvent;
    }

    @Override
    public void deleteEvent(String keycloakId, String eventId) {
        var event = getExistingUserEvent(eventId, keycloakId);

        calendarRepository.delete(event);
    }

    @Override
    public Event updateEvent(EventDto eventDto, String keycloakId, String eventId) {
        var event = getExistingUserEvent(eventId, keycloakId);

        List<ProfileShortInfo> receiversProfiles = getReceiversProfileInfo(eventDto);
        List<InvitedFriends> invitedFriendsList = new ArrayList<>();

        receiversProfiles.forEach(rp -> {
            var invitedFriend = InvitedFriends.builder()
                    .profileShortInfo(rp)
                    .isAccepted(false)
                    .build();
            invitedFriendsList.add(invitedFriend);
        });

        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        event.setColor(eventDto.getColor());
        event.setLocation(eventDto.getLocation());
        event.setRemindBefore(eventDto.getRemindBefore());
        event.setRepeat(eventDto.isRepeat());
        event.setGuests(invitedFriendsList);
        event.setStartDate(LocalDate.parse(eventDto.getStartDate(), formatter));
        event.setEndDate(LocalDate.parse(eventDto.getEndDate(), formatter));
        calendarRepository.save(event);

        return event;
    }

    @Override
    public List<Event> getAllEvents(String keycloakId) {
        return calendarRepository.findAllByAuthor_KeycloakId(keycloakId);
    }

    @Override
    public Event getEvent(String eventId) {
        return calendarRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with eventId %s doesn't exist", eventId)));
    }

    @Override
    public Event createUnionEvent(UnionEventDto eventDto, String keycloakId) {
        var psi = getProfileShortInfo(keycloakId);
        var author = getAuthor(keycloakId, psi);

        Event newEvent = Event.builder()
                .id(UUID.randomUUID().toString())
                .title(eventDto.getTitle())
                .author(author)
                .color(eventDto.getColor())
                .description(eventDto.getDescription())
                .startDate(LocalDate.parse(eventDto.getStartDate(), formatter))
                .endDate(LocalDate.parse(eventDto.getEndDate(), formatter))
                .build();

        calendarRepository.save(newEvent);
        return newEvent;
    }

    @Override
    public void deleteUnionEvent(String keycloakId, String eventId) {
        var event = getExistingUserEvent(eventId, keycloakId);
        calendarRepository.delete(event);
    }

    @Override
    public Event updateUnionEvent(UnionEventDto unionEventDto, String keycloakId, String eventId) {
        var event = getExistingUserEvent(eventId, keycloakId);

       /* List<ProfileShortInfo> receiversProfiles = getReceiversProfileInfo(unionEventDto);
        List<InvitedFriends> invitedFriendsList = new ArrayList<>();

        receiversProfiles.forEach(rp -> {
            var invitedFriend = InvitedFriends.builder()
                    .profileShortInfo(rp)
                    .isAccepted(false)
                    .build();
            invitedFriendsList.add(invitedFriend);
        });*/

        event.setTitle(unionEventDto.getTitle());
        event.setDescription(unionEventDto.getDescription());
        event.setColor(unionEventDto.getColor());
        event.setStartDate(LocalDate.parse(unionEventDto.getStartDate(), formatter));
        event.setEndDate(LocalDate.parse(unionEventDto.getEndDate(), formatter));
        //event.setGuests(invitedFriendsList);

        calendarRepository.save(event);

        return event;
    }

    @Override
    public List<Event> getAllUnionEvents(String keycloakId) {
        return calendarRepository.findAllByAuthor_KeycloakId(keycloakId);
    }

    @Override
    public Event getUnionEvent(String eventId) {
        return calendarRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with eventId %s doesn't exist", eventId)));
    }

    private Event getExistingUserEvent(String eventId, String keycloakId) {
        return calendarRepository.findByIdAndAuthor_KeycloakId(eventId, keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with eventId %s doesn't exist", eventId)));
    }

    private ProfileShortInfo getProfileShortInfo(String keycloakId) {
        return profileService.userShortProfileByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException(String.format("User with keycloakId %s does not exist", keycloakId)));
    }
    private Author getAuthor(String keycloakId, ProfileShortInfo psi) {
        Author author = new Author();
        author.setId(UUID.randomUUID().toString());
        author.setKeycloakId(keycloakId);
        author.setFirstName(psi.getFirstName());
        author.setLastName(psi.getLastName());
        author.setImage(psi.getImage());

        return author;
    }

    private List<ProfileShortInfo> getReceiversProfileInfo(EventDto eventDto) {
        return profileService.getListOfShortProfileInfo(eventDto.getGuestsKeycloakId());
    }

    /*private List<ProfileShortInfo> getReceiversProfileInfo(UnionEventDto unionEventDto) {
        return profileService.getListOfShortProfileInfo(unionEventDto.getGuestsKeycloakId());
    }*/
}
