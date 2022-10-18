package com.wevolv.calendarservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Author {
    @Id
    private String id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String image;
}
