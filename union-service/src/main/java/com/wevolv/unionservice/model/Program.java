package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Program {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDate startDate;
    private String duration;
    private String externalProgramInfo;
    private String contactPerson;
}
