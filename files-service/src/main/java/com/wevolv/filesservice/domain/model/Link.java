package com.wevolv.filesservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link {

    @Id
    private String id;
    private String folderId;
    private String fileName;
    private String fileDescription;
    private String fileType;
    private Instant createdTime;
    private String link;
    //this means that it does not belong to any folder
    private boolean isStandalone;
}
