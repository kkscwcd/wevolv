package com.wevolv.unionservice.model;

import com.wevolv.unionservice.model.enums.UnionProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {
    @Id
    private String id;
    private String title;
    private Author author;
    private Instant timePosted;
    private Integer numberOfPosts;
    private Boolean isPublishedToForum;
    //private UnionProvider unionProvider;
}
