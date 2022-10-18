package com.wevolv.wevibeservice.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRecommendationDto {
    private String placeId;
    private boolean recommended = false;
}
