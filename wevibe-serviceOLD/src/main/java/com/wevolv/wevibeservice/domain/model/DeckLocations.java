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
public class DeckLocations {
    @Id
    private String id;
    private String keycloakId;
    private List<Location> myLocation;
    private List<FavoriteLocation> favouriteLocations;
}
