package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A JavaScript transformation script that runs on events before delivery.
 *
 * @param id          transformation UUID.
 * @param name        human-readable name.
 * @param description description of what the script does.
 * @param code        JavaScript code containing a top-level {@code transform} function.
 * @param enabled     whether the transformation runs in production.
 * @param failOpen    {@code true} to skip the transform on error rather than failing the delivery.
 * @param timeoutMs   execution timeout in milliseconds.
 * @param createdAt   creation timestamp in RFC3339.
 * @param updatedAt   last-modified timestamp in RFC3339.
 */
public record Transformation(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("code") String code,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("failOpen") boolean failOpen,
        @JsonProperty("timeoutMs") int timeoutMs,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt
) {
}
