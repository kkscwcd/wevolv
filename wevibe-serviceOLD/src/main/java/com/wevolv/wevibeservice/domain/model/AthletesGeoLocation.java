package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AthletesGeoLocation {
    //longitude always comes first and latitude second
//    private float longitude;
//    private float latitude;
    private double[] coordinates;
}
