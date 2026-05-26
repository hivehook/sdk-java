package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * An API key (without the raw secret).
 *
 * @param id          API key UUID.
 * @param name        human-readable name.
 * @param keyPrefix   first few characters of the key for display purposes.
 * @param scopes      granted scopes.
 * @param sourceIds   sources this key is scoped to (empty list = all).
 * @param createdAt   creation timestamp in RFC3339.
 * @param expiresAt   expiration timestamp (nullable).
 * @param revokedAt   revocation timestamp (nullable when active).
 * @param lastUsedAt  last-use timestamp (nullable).
 */
public record APIKey(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("keyPrefix") String keyPrefix,
        @JsonProperty("scopes") List<String> scopes,
        @JsonProperty("sourceIds") List<String> sourceIds,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("expiresAt") String expiresAt,
        @JsonProperty("revokedAt") String revokedAt,
        @JsonProperty("lastUsedAt") String lastUsedAt
) {
}
