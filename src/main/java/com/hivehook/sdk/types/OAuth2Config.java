package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OAuth2 client-credentials configuration for outbound delivery.
 *
 * @param tokenUrl     OAuth2 token endpoint.
 * @param clientId     OAuth2 client ID.
 * @param clientSecret OAuth2 client secret (write-only on most APIs).
 * @param scopes       requested OAuth2 scopes.
 * @param audience     OAuth2 audience claim if required.
 */
public record OAuth2Config(
        @JsonProperty("tokenUrl") String tokenUrl,
        @JsonProperty("clientId") String clientId,
        @JsonProperty("clientSecret") String clientSecret,
        @JsonProperty("scopes") List<String> scopes,
        @JsonProperty("audience") String audience
) {
}
