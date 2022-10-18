package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class Podcast {
    @Id
    private String id;
    private String keycloakId;
    private String title;
    private String description;
    private String link;
}
