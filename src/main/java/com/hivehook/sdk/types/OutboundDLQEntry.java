package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dead-letter queue entry for an outbound delivery that exhausted its retries.
 *
 * @param id          DLQ entry UUID.
 * @param deliveryId  UUID of the failed delivery.
 * @param messageId   UUID of the originating message.
 * @param lastError   final error captured from the endpoint.
 * @param replayedAt  timestamp of the most recent replay (nullable).
 * @param createdAt   creation timestamp in RFC3339.
 */
public record OutboundDLQEntry(
        @JsonProperty("id") String id,
        @JsonProperty("deliveryId") String deliveryId,
        @JsonProperty("messageId") String messageId,
        @JsonProperty("lastError") String lastError,
        @JsonProperty("replayedAt") String replayedAt,
        @JsonProperty("createdAt") String createdAt
) {
}
