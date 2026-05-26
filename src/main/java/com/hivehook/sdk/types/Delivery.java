package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A delivery aggregates all attempts to forward one event to one destination.
 *
 * @param id                delivery UUID.
 * @param eventId           UUID of the event being delivered.
 * @param subscriptionId    subscription UUID that produced this delivery.
 * @param destinationId     UUID of the target destination.
 * @param status            delivery status (e.g. {@code PENDING}, {@code DELIVERED}, {@code FAILED}).
 * @param attempts          number of attempts performed so far.
 * @param maxAttempts       maximum number of attempts allowed.
 * @param nextAttemptAt     scheduled timestamp for the next attempt (nullable).
 * @param createdAt         creation timestamp in RFC3339.
 * @param deliveryAttempts  per-attempt details when requested (nullable).
 */
public record Delivery(
        @JsonProperty("id") String id,
        @JsonProperty("eventId") String eventId,
        @JsonProperty("subscriptionId") String subscriptionId,
        @JsonProperty("destinationId") String destinationId,
        @JsonProperty("status") String status,
        @JsonProperty("attempts") int attempts,
        @JsonProperty("maxAttempts") int maxAttempts,
        @JsonProperty("nextAttemptAt") String nextAttemptAt,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("deliveryAttempts") List<DeliveryAttempt> deliveryAttempts
) {
}
