package com.wevolv.filesservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    private String id;
    private String keycloakId;
    private String name;
    @DBRef
    private List<File> files;
    @DBRef
    private List<Image> images;
    @DBRef
    private List<Link> links;
    private long createdTime;
    @DBRef
    private List<Folder> folders;
}