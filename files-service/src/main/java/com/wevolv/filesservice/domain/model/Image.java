package com.wevolv.filesservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
@Builder
public class Image {
    @Id
    private String id;
    private String folderId;
    private String link;
    private Long createdTime;
    private String fileName;
    private String fileDescription;
    private String fileType;
    private Long fileSize;
    private String public_id;
    //this means that it does not belong to any folder
    private boolean isStandalone;
}