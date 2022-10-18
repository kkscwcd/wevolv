package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Link {
    @Id
    private String id;
    private String folderId;
    private String fileName;
    private String fileDescription;
    private String fileType;
    private Long createdTime;
    private String link;
}
