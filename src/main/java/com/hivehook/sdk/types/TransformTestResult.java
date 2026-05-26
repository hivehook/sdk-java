package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Outcome of a transformation dry-run.
 *
 * @param success    {@code true} when the script executed without error.
 * @param output     transformed payload (nullable when the script fails).
 * @param error      error message (empty on success).
 * @param durationMs script execution time in milliseconds.
 */
public record TransformTestResult(
        @JsonProperty("success") boolean success,
        @JsonProperty("output") Map<String, Object> output,
        @JsonProperty("error") String error,
        @JsonProperty("durationMs") int durationMs
) {
}
