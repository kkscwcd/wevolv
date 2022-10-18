package com.wevolv.unionservice.model;

import com.wevolv.unionservice.model.enums.UnionProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event {
    @Id
    private String id;
    private String title;
    private Author author;
    private String description;
    private String color;
    private Image image;
    private Date startDate;
    private Date endDate;
    private Boolean isFree;
    private Boolean isPrivate;
    private String timeZone;
    private UnionProvider unionProvider;

}
