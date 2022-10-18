package com.wevolv.wevibeservice.domain.model.dto;

import com.wevolv.wevibeservice.domain.model.enums.VibeTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VibeDefaultTagsDto {
    private List<VibeTag> tagName;
}
