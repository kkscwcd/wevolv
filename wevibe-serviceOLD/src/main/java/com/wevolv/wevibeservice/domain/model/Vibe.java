package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vibes")
public class Vibe {
    @Id
    private String id;
    private String keycloakId;
    private String name;
    private Image vibeImage;
    private Author vibeAuthor;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Boolean isPrivate;
    private Boolean isPaid;
    private List<InvitedFriends> invitedFriends;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private VibeGeoLocation vibeGeoLocation;
    private List<VibeTags> tags;
    private String vibeDescription;
    private Boolean addToCalendar;
    private String deckId;
}
