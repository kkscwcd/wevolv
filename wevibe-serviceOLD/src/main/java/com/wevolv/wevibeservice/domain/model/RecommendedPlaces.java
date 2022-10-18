package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
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
@Document(collection = "recommendedPlaces")
public class RecommendedPlaces {
    @Id
    private String id;
    private String placeId;
    private List<ProfileShortInfo> placeRecommendations;
    private String keycloakId;
}
