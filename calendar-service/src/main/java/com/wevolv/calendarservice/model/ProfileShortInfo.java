package com.wevolv.calendarservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileShortInfo {
    private String profileId;
    private String firstName;
    private String lastName;
    private String image;
}
