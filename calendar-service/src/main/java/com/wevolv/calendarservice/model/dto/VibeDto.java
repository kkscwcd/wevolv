package com.wevolv.calendarservice.model.dto;

import com.wevolv.calendarservice.model.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VibeDto {
    private String name;
    private String description;
    private Image vibeImage;
    private Author vibeAuthor;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
}
