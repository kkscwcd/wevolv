package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Member {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Boolean isPaid;
    private Instant membershipStart;
    private Boolean isPlayer;
    private Boolean isRepresentative;
    private String team;
}
