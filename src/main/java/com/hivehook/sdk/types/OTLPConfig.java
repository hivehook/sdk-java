package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * OpenTelemetry exporter configuration for an organization.
 *
 * @param endpoint   OTLP/gRPC or OTLP/HTTP endpoint URL.
 * @param headers    extra headers sent with OTLP requests (nullable).
 * @param insecure   {@code true} to disable TLS verification.
 * @param sampleRate trace sampling rate in {@code [0, 1]}.
 */
public record OTLPConfig(
        @JsonProperty("endpoint") String endpoint,
        @JsonProperty("headers") Map<String, String> headers,
        @JsonProperty("insecure") boolean insecure,
        @JsonProperty("sampleRate") double sampleRate
) {
}
