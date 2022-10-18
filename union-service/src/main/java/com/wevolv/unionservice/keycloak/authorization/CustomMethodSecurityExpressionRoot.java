package com.wevolv.unionservice.keycloak.authorization;

import com.wevolv.unionservice.keycloak.KeycloakUtils;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.Collections;

@Slf4j
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isServiceProvider() {
        final GroupAuthorization groupAuthorization = getGroupAuthorization();
        return groupAuthorization.isServiceProvider();
    }

    public boolean isMemberOf(String scope) {
        return isPartOf(scope, UnionRole.MEMBER);
    }

    public boolean isEmployeeIn(String scope) {
        return isPartOf(scope, UnionRole.EMPLOYEE);
    }

    public boolean isPartOf(final String scope, final UnionRole unionRole) {
        final GroupAuthorization groupAuthorization = getGroupAuthorization();
        var unions = groupAuthorization.getUnions();
        var roles = unions.getOrDefault(scope, Collections.emptyList());
        return roles.contains(unionRole);
    }

    private GroupAuthorization getGroupAuthorization() {
        final SimpleKeycloakAccount account = (SimpleKeycloakAccount) authentication.getDetails();
        final AccessToken token = account.getKeycloakSecurityContext().getToken();
        return KeycloakUtils.groupAuthorizationFrom(token);
    }

    @Override
    public void setFilterObject(Object o) {
        //No implementation
    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object o) {
        //No implementation
    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return this;
    }
}
