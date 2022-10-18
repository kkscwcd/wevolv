package com.wevolv.registration.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoDto {

    /**
     * unique user number
     */
    String sub;

    /**
     * user verified email
     */
    Boolean email_verified;

    /**
     * user username
     */
    String preferred_username;

    /**
     * user email
     */
    String email;

    /**
     * user full name
     */
    String name;
}
