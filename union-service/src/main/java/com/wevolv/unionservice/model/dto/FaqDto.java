package com.wevolv.unionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FaqDto {
    private String categoryId;
    private String title;
    private String description;
}
