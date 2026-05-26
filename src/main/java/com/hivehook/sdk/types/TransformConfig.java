package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Configures how an event is reshaped before delivery.
 *
 * @param envelope whether the gateway wraps the event payload in a Hivehook envelope.
 * @param headers  static headers added to the outbound request (nullable).
 */
public record TransformConfig(
        @JsonProperty("envelope") boolean envelope,
        @JsonProperty("headers") Map<String, Object> headers
) {
}
