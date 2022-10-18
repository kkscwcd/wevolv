package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locations")
public class Location {
    @Id
    private String id;
    private String keycloakId;
    private String name;
    private String city;
    private String country;
    private String address;
    private String website;
    private String phoneNumber;
    private Map<String, String> status;
    private List<LocationTags> tags;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private LocationGeoLocation locationGeoLocation;
    private int numberOfPhotos;
    private int numberOfReviews;
    private String deckId;
}
