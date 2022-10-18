package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deck {
    @Id
    private String id;
    private String name;
    private String keycloakId;
    private Image coverImage;
    private String description;
    private int numberOfSpots;
    private boolean isPined;
    private boolean isCurrentLocation;
    private int numbOfVibes;
    private int numOfLocations;
}
