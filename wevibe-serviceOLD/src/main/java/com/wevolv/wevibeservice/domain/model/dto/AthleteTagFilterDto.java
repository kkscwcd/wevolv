package com.wevolv.wevibeservice.domain.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AthleteTagFilterDto {
    private String gender;
    private List<String> country;
    private int page;
    private int size;
}
