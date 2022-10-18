package com.wevolv.calendarservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event {
    @Id
    private String id;
    private Author author;
    private String title;
    private String description;
    private String location;
    private String color;
    private long remindBefore;
    private boolean repeat;
    private List<InvitedFriends> guests;
    private LocalDate startDate;
    private LocalDate endDate;
//    private TimeZone timezone;
}
