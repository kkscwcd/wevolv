package com.wevolv.unionservice.model;

public class UserDataContext {
    public final String keycloakId;
    public final String accessToken;
    //private List<Role> roles;

    public UserDataContext(String keycloakId, String accessToken) {
        this.keycloakId = keycloakId;
        this.accessToken = accessToken;
    }

    public static UserDataContext create(String keycloakId, String accessToken){
        return new UserDataContext(keycloakId, accessToken);
    }
}
