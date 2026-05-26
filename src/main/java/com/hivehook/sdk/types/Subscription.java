package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Links a {@link Source} to a {@link Destination} with optional filtering and transformation.
 *
 * @param id              subscription UUID.
 * @param name            human-readable name.
 * @param sourceId        UUID of the source.
 * @param destinationId   UUID of the destination.
 * @param filterConfig    filter configuration (nullable).
 * @param transformConfig transform configuration (nullable).
 * @param enabled         whether the subscription is active.
 * @param createdAt       creation timestamp in RFC3339.
 */
public record Subscription(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("sourceId") String sourceId,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("filterConfig") FilterConfig filterConfig,
        @JsonProperty("transformConfig") TransformConfig transformConfig,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("createdAt") String createdAt
) {
}
