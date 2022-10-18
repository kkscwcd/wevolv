package com.wevolv.wevibeservice.domain.model.dto;

import com.wevolv.wevibeservice.domain.model.LocationTags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditLocationDto {
    private String name;
    private List<LocationTags> tags;
}
