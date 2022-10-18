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
public class BenefitDto {
    private String title;
    private String description;
    private boolean active;
}
