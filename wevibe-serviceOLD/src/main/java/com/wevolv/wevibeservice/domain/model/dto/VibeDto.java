package com.wevolv.wevibeservice.domain.model.dto;


import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.LocationGeoLocation;
import com.wevolv.wevibeservice.domain.model.VibeGeoLocation;
import com.wevolv.wevibeservice.domain.model.VibeTags;
import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VibeDto {
    private String name;
    private Image vibeImage;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Boolean isPrivate;
    private Boolean isPaid;
    private List<String> friendsId = new ArrayList<>();
    private List<VibeTags> tags = new ArrayList<>();
    private String vibeDescription;
    private VibeGeoLocation vibeGeoLocation;
    private Boolean addToCalendar;
}
