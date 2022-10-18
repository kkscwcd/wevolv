package com.wevolv.unionservice.keycloak;

import org.keycloak.adapters.*;
import org.keycloak.adapters.spi.AuthOutcome;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.authentication.SpringSecurityRequestAuthenticator;

import javax.servlet.http.HttpServletRequest;

public class WevolvSecurityRequestAuthenticator extends SpringSecurityRequestAuthenticator {

    public WevolvSecurityRequestAuthenticator(HttpFacade facade, HttpServletRequest request, KeycloakDeployment deployment, AdapterTokenStore tokenStore, int sslRedirectPort) {
        super(facade, request, deployment, tokenStore, sslRedirectPort);
    }

    @Override
    public AuthOutcome authenticate() {
        BearerTokenRequestAuthenticator bearer = createBearerTokenAuthenticator();
        bearer.authenticate(facade);
        completeAuthentication(bearer, "KEYCLOAK");
        return AuthOutcome.AUTHENTICATED;
    }
    @Override
    protected BearerTokenRequestAuthenticator createBearerTokenAuthenticator() {
        return new WevolvBearerTokenRestAuthenticator(deployment);
    }


}
