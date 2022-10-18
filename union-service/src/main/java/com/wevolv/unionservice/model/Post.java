package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.awt.*;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Post {
    @Id
    private String id;
    private String topicId;
    private String keycloakId;
    private String title;
    private String text;
    private Author author;
    private Image image;
    private Instant timePosted;
    private List<Tags> tags;
    private Boolean isPublished;
    private Boolean isTrashed;
    private Boolean isDraft;
    private Boolean isPublishedToForum;
}
