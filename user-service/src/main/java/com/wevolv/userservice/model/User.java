package com.wevolv.userservice.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "user")
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String keycloakId;
    private String googleId;
    private String appleId;
    private Boolean emailVerified = false;
    private Boolean enabled = false;
    private String gender;
    private String deviceToken;
    private List<String> groups;
}