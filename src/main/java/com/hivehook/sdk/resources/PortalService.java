package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.PortalToken;

import java.util.concurrent.CompletableFuture;

/**
 * Service that issues customer-portal access tokens.
 */
public final class PortalService extends BaseService {
    private static final String GENERATE_TOKEN_MUTATION = """
            mutation GeneratePortalToken($applicationId: UUID!) {
              generatePortalToken(applicationId: $applicationId) {
                token
                expiresAt
              }
            }
            """;

    /** @param transport transport to use. */
    public PortalService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * Generate a time-bound portal token scoped to a single application.
     *
     * @param applicationId application UUID.
     * @return signed portal token.
     */
    public PortalToken generateToken(String applicationId) {
        JsonNode data = transport.execute(GENERATE_TOKEN_MUTATION, vars("applicationId", applicationId));
        return toType(data.get("generatePortalToken"), PortalToken.class);
    }

    /**
     * Asynchronously generate a time-bound portal token scoped to a single application.
     *
     * @param applicationId application UUID.
     * @return future completing with the signed portal token.
     */
    public CompletableFuture<PortalToken> generateTokenAsync(String applicationId) {
        return transport.executeAsync(GENERATE_TOKEN_MUTATION, vars("applicationId", applicationId))
                .thenApply(data -> toType(data.get("generatePortalToken"), PortalToken.class));
    }
}
