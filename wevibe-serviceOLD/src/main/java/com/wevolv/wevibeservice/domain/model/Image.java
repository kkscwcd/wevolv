package com.wevolv.wevibeservice.domain.model;

import com.wevolv.wevibeservice.integration.profile.model.ProfileShortInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@Builder
public class Image {

    @Id
    private String id;
    private String link;
    private String locationId;
    private String reviewId;
    private String vibeId;
    private String deckId;
    private String keycloakId;
    private String public_id;
    private Long createdTime;
    private String fileName;
    private String fileDescription;
    private String fileType;
    private Long fileSize;
    //private List<Like> likes;
    private int numberOfLikes;
    private Boolean isLiked;
    private ProfileShortInfo profileShortInfo;
}
