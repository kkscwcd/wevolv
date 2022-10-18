package com.wevolv.unionservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class Image {
    @Id
    private String id;
    private String link;
    public String postId;
    private Long createdTime;
    private String fileName;
    private String fileDescription;
    private String fileType;
    private Long fileSize;
    private String public_id;
}
