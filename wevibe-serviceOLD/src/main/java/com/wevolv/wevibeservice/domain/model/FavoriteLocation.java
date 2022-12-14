package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "favoriteLocations")
public class FavoriteLocation {
    @Id
    private String id;
    private String locationId;
    private String keycloakId;
}
