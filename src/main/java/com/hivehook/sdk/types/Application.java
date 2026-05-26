package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An outbound application owning one or more endpoints.
 *
 * @param id        application UUID.
 * @param name      human-readable name.
 * @param uid       caller-provided stable identifier.
 * @param createdAt creation timestamp in RFC3339.
 */
public record Application(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("uid") String uid,
        @JsonProperty("createdAt") String createdAt
) {
}
