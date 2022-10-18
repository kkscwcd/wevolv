package com.wevolv.wevibeservice.domain.model.dto;

import com.wevolv.wevibeservice.domain.model.Image;
import com.wevolv.wevibeservice.domain.model.LocationGeoLocation;
import com.wevolv.wevibeservice.domain.model.LocationTags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String name;
    private String city;
    private String country;
    private String address;
    private String website;
    private String phoneNumber;
    private Map<String, String> status;
    private List<LocationTags> tags;
    private LocationGeoLocation locationGeoLocation;
    private List<Image> customPhotos;
}
