package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Retry policy applied to delivery attempts.
 *
 * @param maxAttempts   maximum number of attempts the worker will perform.
 * @param initialDelay  initial backoff delay, formatted as a Go duration string (e.g. {@code "1s"}).
 * @param maxDelay      cap on the backoff delay, formatted as a Go duration string.
 * @param backoffFactor multiplier applied to the delay between consecutive attempts.
 */
public record RetryPolicy(
        @JsonProperty("maxAttempts") int maxAttempts,
        @JsonProperty("initialDelay") String initialDelay,
        @JsonProperty("maxDelay") String maxDelay,
        @JsonProperty("backoffFactor") double backoffFactor
) {
}
