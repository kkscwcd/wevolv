package com.wevolv.wevibeservice.domain.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class LocationTagFilterDto {
    List<String> locationTags;
}
