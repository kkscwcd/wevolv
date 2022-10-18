package com.wevolv.calendarservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vibe {
    private String id;
    private String name;
    private String keycloakId;
    private String description;
    private Image vibeImage;
    private Author vibeAuthor;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
//    private VibeGeoLocation vibeGeoLocation;
}
