package com.wevolv.wevibeservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitedFriends {
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String image;
    private String country;
    private String currentPosition;
    private Boolean isAccepted;
}
