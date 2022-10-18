package com.wevolv.unionservice.model.dto;

import com.wevolv.unionservice.model.Author;
import com.wevolv.unionservice.model.Image;
import com.wevolv.unionservice.model.enums.UnionProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UnionEventDto {
    private String title;
    private Author author;
    private String description;
    private String color;
    private String startDate;
    private String endDate;
    private Boolean isFree;
    private Boolean isPrivate;
    private String timeZone;
    //private UnionProvider unionProvider;
}
