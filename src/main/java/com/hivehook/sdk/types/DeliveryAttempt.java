package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One attempt to deliver a webhook to a destination.
 *
 * @param id             attempt UUID.
 * @param deliveryId     parent delivery UUID.
 * @param attemptNumber  1-based attempt number.
 * @param responseStatus HTTP status returned by the destination.
 * @param responseBody   destination response body (truncated by the server).
 * @param error          error message if the attempt failed, empty otherwise.
 * @param durationMs     wall-clock time in milliseconds.
 * @param attemptedAt    attempt timestamp in RFC3339.
 */
public record DeliveryAttempt(
        @JsonProperty("id") String id,
        @JsonProperty("deliveryId") String deliveryId,
        @JsonProperty("attemptNumber") int attemptNumber,
        @JsonProperty("responseStatus") int responseStatus,
        @JsonProperty("responseBody") String responseBody,
        @JsonProperty("error") String error,
        @JsonProperty("durationMs") int durationMs,
        @JsonProperty("attemptedAt") String attemptedAt
) {
}
