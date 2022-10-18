package com.wevolv.calendarservice.model.dto;

import com.wevolv.calendarservice.model.Author;
import com.wevolv.calendarservice.model.ProfileShortInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventDto {
    private String title;
    private Author author;
    private String description;
    private String color;
    private String location;
    private long remindBefore;
    private boolean repeat;
    private List<String> guestsKeycloakId;
    private String startDate;
    private String endDate;
}
