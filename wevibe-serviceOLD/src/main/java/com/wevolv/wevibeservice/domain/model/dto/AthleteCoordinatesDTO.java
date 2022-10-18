package com.wevolv.wevibeservice.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AthleteCoordinatesDTO {
    //longitude always comes first and latitude second
    private float longitude;
    private float latitude;
    //private int radius;
    private double radius;
    List<String> tags;
}