package com.wevolv.filesservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class File {
    @Id
    private String id;
    private String keycloakId;
    private String folderId;
    private String fileName;
    private String fileDescription;
    private String fileType;
    private long fileSize;
    private Instant createdTime;
    //this means that it does not belong to any folder
    private Boolean isStandalone;
    @JsonIgnore
    private byte[] file;

}
