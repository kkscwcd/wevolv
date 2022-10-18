package com.wevolv.wevibeservice.domain.model;


import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "locationReview")
public class LocationReview {
    @Id
    private String id;
    private String locationId;
    private int locationRating;
    private String review;
    private ProfileShortInfo playerInfo;
    private String keycloakId;
    private List<Image> photos;
    private int numberOfLikes;
    private int numberOfShares;
    private Boolean isLiked;
    private Instant timePosted;
}
