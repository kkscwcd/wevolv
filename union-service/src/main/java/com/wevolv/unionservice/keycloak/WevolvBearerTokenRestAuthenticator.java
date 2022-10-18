package com.wevolv.unionservice.keycloak;

import org.keycloak.TokenVerifier;
import org.keycloak.adapters.BearerTokenRequestAuthenticator;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.OIDCAuthenticationError;
import org.keycloak.adapters.spi.AuthOutcome;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.common.VerificationException;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.representations.AccessToken;

public class WevolvBearerTokenRestAuthenticator extends BearerTokenRequestAuthenticator {
    public WevolvBearerTokenRestAuthenticator(KeycloakDeployment deployment) {
        super(deployment);
    }

    @Override
    public AuthOutcome authenticateToken(HttpFacade exchange, String tokenString) {
        log.debug("Verifying access_token");
        if (log.isTraceEnabled()) {
            try {
                JWSInput jwsInput = new JWSInput(tokenString);
                String wireString = jwsInput.getWireString();
                log.tracef("\taccess_token: %s", wireString.substring(0, wireString.lastIndexOf(".")) + ".signature");
            } catch (JWSInputException e) {
                log.errorf(e, "Failed to parse access_token: %s", tokenString);
            }
        }
        try {
            TokenVerifier<AccessToken> tokenVerifier = TokenVerifier.create(tokenString, AccessToken.class);
            token = tokenVerifier.parse().getToken();
        } catch (VerificationException e) {
            log.debug("Failed to parse token token");
            challengeResponse(exchange, OIDCAuthenticationError.Reason.INVALID_TOKEN, "invalid_token", e.getMessage());
            return AuthOutcome.FAILED;
        }
        log.debug("successful authorized");
        return AuthOutcome.AUTHENTICATED;
    }
}
