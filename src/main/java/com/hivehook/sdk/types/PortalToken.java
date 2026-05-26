package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A signed, time-bound token that grants a customer access to their portal view.
 *
 * @param token     opaque signed token.
 * @param expiresAt expiration timestamp in RFC3339.
 */
public record PortalToken(
        @JsonProperty("token") String token,
        @JsonProperty("expiresAt") String expiresAt
) {
}
