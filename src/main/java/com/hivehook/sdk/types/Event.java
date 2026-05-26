package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A webhook event received from a source.
 *
 * @param id             event UUID.
 * @param sourceId       UUID of the originating source.
 * @param idempotencyKey idempotency key used to deduplicate the event.
 * @param eventType      provider event type string.
 * @param headers        captured request headers (nullable).
 * @param rawBody        raw payload as the provider sent it (nullable; may be omitted on listings).
 * @param status         event status (e.g. {@code RECEIVED}, {@code FAILED}).
 * @param receivedAt     receipt timestamp in RFC3339.
 */
public record Event(
        @JsonProperty("id") String id,
        @JsonProperty("sourceId") String sourceId,
        @JsonProperty("idempotencyKey") String idempotencyKey,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("headers") Map<String, Object> headers,
        @JsonProperty("rawBody") String rawBody,
        @JsonProperty("status") String status,
        @JsonProperty("receivedAt") String receivedAt
) {
}
