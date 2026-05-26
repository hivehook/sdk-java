package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An outbound message sent through an application.
 *
 * @param id             message UUID.
 * @param applicationId  application UUID.
 * @param eventType      event type string.
 * @param payload        Base64-encoded payload (nullable on listings).
 * @param idempotencyKey idempotency key.
 * @param status         message status.
 * @param createdAt      creation timestamp in RFC3339.
 */
public record Message(
        @JsonProperty("id") String id,
        @JsonProperty("applicationId") String applicationId,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("payload") String payload,
        @JsonProperty("idempotencyKey") String idempotencyKey,
        @JsonProperty("status") String status,
        @JsonProperty("createdAt") String createdAt
) {
}
