package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Health-based auto-disable configuration for a destination or endpoint.
 *
 * @param windowHours  window over which the success ratio is measured.
 * @param disableBelow success ratio below which the destination is automatically disabled.
 */
public record HealthConfig(
        @JsonProperty("windowHours") int windowHours,
        @JsonProperty("disableBelow") double disableBelow
) {
}
