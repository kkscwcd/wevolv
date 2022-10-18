package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.domain.model.enums.LocationTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locationTags")
public class LocationTags {
    @Id
    private String id;
    private LocationTag tagName;
    private String locationId;
    private String keycloakId;
}
