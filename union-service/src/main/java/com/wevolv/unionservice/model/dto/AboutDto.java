package com.wevolv.unionservice.model.dto;

import com.wevolv.unionservice.model.enums.UnionProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AboutDto {
    private String description;
    private UnionProvider unionProvider;
}
