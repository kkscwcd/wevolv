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
public class TopicDto {
    private String title;
}
