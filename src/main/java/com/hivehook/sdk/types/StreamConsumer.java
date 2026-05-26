package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A cursor-bearing consumer reading from a stream.
 *
 * @param id              consumer UUID.
 * @param streamId        stream UUID.
 * @param name            consumer name.
 * @param cursorSequence  last sequence number acknowledged by the consumer.
 * @param createdAt       creation timestamp in RFC3339.
 * @param updatedAt       last-modified timestamp in RFC3339.
 */
public record StreamConsumer(
        @JsonProperty("id") String id,
        @JsonProperty("streamId") String streamId,
        @JsonProperty("name") String name,
        @JsonProperty("cursorSequence") long cursorSequence,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt
) {
}
