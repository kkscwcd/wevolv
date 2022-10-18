package com.wevolv.unionservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberInvite {
    @Id
    private String id;
    private String email;
    private String role;
    private String invitedBy;
    private Boolean accepted;
    private Date inviteDate;
    private Date acceptedDate;
}
