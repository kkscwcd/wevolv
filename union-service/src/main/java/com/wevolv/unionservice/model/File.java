package com.wevolv.unionservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @JsonIgnore
    private byte[] file;
}
