package com.wevolv.unionservice.keycloak;

import com.wevolv.unionservice.keycloak.authorization.GroupAuthorization;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class KeycloakUtils {

    private KeycloakUtils() {
    }

    public static SimpleKeycloakAccount simpleKeycloakAccountFrom(final KeycloakAuthenticationToken token) {
        return (SimpleKeycloakAccount) token.getDetails();
    }

    public static AccessToken accessTokenFrom(final SimpleKeycloakAccount account) {
        return account.getKeycloakSecurityContext().getToken();
    }

    public static AccessToken accessTokenFrom(final KeycloakAuthenticationToken token) {
        final SimpleKeycloakAccount account = simpleKeycloakAccountFrom(token);
        return accessTokenFrom(account);
    }

    public static List<String> groupsFrom(final KeycloakAuthenticationToken token) {
        final AccessToken accessToken = accessTokenFrom(token);
        return (List<String>) accessToken.getOtherClaims().getOrDefault("groups", Collections.emptyList());
    }

    public static List<String> groupsFrom(final AccessToken token) {
        return (List<String>) token.getOtherClaims().getOrDefault("groups", Collections.emptyList());
    }

    public static GroupAuthorization groupAuthorizationFrom(final AccessToken token) {
        return new AuthorizationMapper().map(groupsFrom(token));
    }

    public static GroupAuthorization groupAuthorizationFrom(final KeycloakAuthenticationToken token) {
        return new AuthorizationMapper().map(groupsFrom(token));
    }

    public static Access accessFrom(final KeycloakAuthenticationToken token) {
        return accessTokenFrom(token).getRealmAccess();
    }

    public static Set<String> rolesFrom(final KeycloakAuthenticationToken token) {
        return accessFrom(token).getRoles();
    }

    public static String keycloakIdFrom(final KeycloakAuthenticationToken token) {
        return accessTokenFrom(token).getSubject();
    }

}
