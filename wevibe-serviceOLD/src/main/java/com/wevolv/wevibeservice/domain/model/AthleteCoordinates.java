package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "athleteCoordinates")
public class AthleteCoordinates {
    @Id
    private String id;
    private String keycloakId;
    @GeoSpatialIndexed
    private AthletesGeoLocation athletesGeoLocation;
    private ProfileShortInfo profile;
}
