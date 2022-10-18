package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    private String id;
    private String profileId;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String image;
    private String country;
    private String currentPosition;
}

