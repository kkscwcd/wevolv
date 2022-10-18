package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Like {
    @Id
    private String id;
    private String imageId;
    private String reviewId;
    private Author author;
    private Instant timeOfCreation;
}

