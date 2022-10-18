package com.wevolv.unionservice.keycloak.authorization;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class GroupAuthorization {
    private Sport sport;
    private Map<String, List<UnionRole>> unions;
    private List<String> rawRoles;
    private boolean isServiceProvider;
}