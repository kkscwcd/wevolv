package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    @Id
    private String id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String image;
}
