package com.wevolv.wevibeservice.domain.model.dto;

import com.wevolv.wevibeservice.domain.model.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckDto {
    private String title;
    private String description;
    private Image coverImage;
}
