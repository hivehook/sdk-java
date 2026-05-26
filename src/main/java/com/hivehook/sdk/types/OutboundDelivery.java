package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A delivery of an outbound message to a specific endpoint.
 *
 * @param id                delivery UUID.
 * @param messageId         UUID of the message being delivered.
 * @param endpointId        UUID of the target endpoint.
 * @param status            delivery status.
 * @param attempts          number of attempts performed so far.
 * @param maxAttempts       maximum number of attempts allowed.
 * @param nextAttemptAt     scheduled timestamp for the next attempt (nullable).
 * @param createdAt         creation timestamp in RFC3339.
 * @param deliveryAttempts  per-attempt details when requested (nullable).
 */
public record OutboundDelivery(
        @JsonProperty("id") String id,
        @JsonProperty("messageId") String messageId,
        @JsonProperty("endpointId") String endpointId,
        @JsonProperty("status") String status,
        @JsonProperty("attempts") int attempts,
        @JsonProperty("maxAttempts") int maxAttempts,
        @JsonProperty("nextAttemptAt") String nextAttemptAt,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("deliveryAttempts") List<OutboundDeliveryAttempt> deliveryAttempts
) {
}
