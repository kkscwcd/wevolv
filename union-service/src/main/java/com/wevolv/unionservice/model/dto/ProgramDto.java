package com.wevolv.unionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProgramDto {
    private String title;
    private String description;
    private String startDate;
    private String duration;
    private String externalProgramInfo;
    private String contactPerson;
}
