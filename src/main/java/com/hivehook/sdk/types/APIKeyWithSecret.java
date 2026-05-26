package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * API key together with its raw secret. Returned only on creation; the raw key is never readable
 * afterwards.
 *
 * @param apiKey API key metadata.
 * @param rawKey full secret to store on the client.
 */
public record APIKeyWithSecret(
        @JsonProperty("apiKey") APIKey apiKey,
        @JsonProperty("rawKey") String rawKey
) {
}
