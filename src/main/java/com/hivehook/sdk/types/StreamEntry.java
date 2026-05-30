package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single message persisted in a Stream's log.
 *
 * @param id        entry UUID.
 * @param streamId  owning stream UUID.
 * @param sequence  monotonically increasing position in the stream.
 * @param messageId source Message UUID, when the entry came from a published Message.
 * @param eventType event type carried by the entry.
 * @param payload   base64-encoded payload.
 * @param createdAt creation timestamp in RFC3339.
 */
public record StreamEntry(
        @JsonProperty("id") String id,
        @JsonProperty("streamId") String streamId,
        @JsonProperty("sequence") long sequence,
        @JsonProperty("messageId") String messageId,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("payload") String payload,
        @JsonProperty("createdAt") String createdAt
) {
}
