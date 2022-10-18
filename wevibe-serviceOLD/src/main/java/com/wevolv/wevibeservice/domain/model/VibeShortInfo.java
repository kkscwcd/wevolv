package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VibeShortInfo {
    private String name;
    private Image vibeImage;
    private Author vibeAuthor;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private VibeGeoLocation vibeGeoLocation;
}
