package com.wevolv.calendarservice.model.dto;

import com.wevolv.calendarservice.model.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnionEventDto {
    private String title;
    private Author author;
    private String description;
    private String color;
    //private List<String> guestsKeycloakId;
    private String startDate;
    private String endDate;
}
