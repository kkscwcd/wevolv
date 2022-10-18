package com.wevolv.unionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
public class MemberDto {
    private String firstName;
    private String lastName;
    private Boolean isPaid;
    private Instant membershipStart;
    private Boolean isPlayer;
    private Boolean isRepresentative;
    private String team;
}
