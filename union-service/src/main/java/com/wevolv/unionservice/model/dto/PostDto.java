package com.wevolv.unionservice.model.dto;

import com.wevolv.unionservice.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostDto {
    private String title;
    private String text;
    private List<Tags> tags;
    private Boolean isPublished;
    private Boolean isTrashed;
    private Boolean isDraft;
    private Boolean isPublishedToForum;
}
