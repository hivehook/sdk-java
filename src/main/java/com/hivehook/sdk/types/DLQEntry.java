package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dead-letter queue entry for an inbound delivery that exhausted its retries.
 *
 * @param id          DLQ entry UUID.
 * @param deliveryId  UUID of the failed delivery.
 * @param eventId     UUID of the originating event.
 * @param lastError   final error captured from the destination.
 * @param replayedAt  timestamp of the most recent replay (nullable).
 * @param createdAt   creation timestamp in RFC3339.
 */
public record DLQEntry(
        @JsonProperty("id") String id,
        @JsonProperty("deliveryId") String deliveryId,
        @JsonProperty("eventId") String eventId,
        @JsonProperty("lastError") String lastError,
        @JsonProperty("replayedAt") String replayedAt,
        @JsonProperty("createdAt") String createdAt
) {
}
