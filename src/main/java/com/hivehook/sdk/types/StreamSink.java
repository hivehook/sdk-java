package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A managed sink that drains a stream to an external system.
 *
 * @param id              sink UUID.
 * @param streamId        stream UUID.
 * @param name            sink name.
 * @param sinkType        sink implementation id (e.g. {@code s3}, {@code bigquery}).
 * @param config          sink-specific configuration (never {@code null}; may be empty).
 * @param batchSize       maximum events per flush.
 * @param flushInterval   maximum delay between flushes as a Go duration string.
 * @param cursorSequence  last sequence number written.
 * @param status          sink status.
 * @param lastFlushedAt   timestamp of the last successful flush (nullable).
 * @param createdAt       creation timestamp in RFC3339.
 */
public record StreamSink(
        @JsonProperty("id") String id,
        @JsonProperty("streamId") String streamId,
        @JsonProperty("name") String name,
        @JsonProperty("sinkType") String sinkType,
        @JsonProperty("config") Map<String, Object> config,
        @JsonProperty("batchSize") int batchSize,
        @JsonProperty("flushInterval") String flushInterval,
        @JsonProperty("cursorSequence") long cursorSequence,
        @JsonProperty("status") String status,
        @JsonProperty("lastFlushedAt") String lastFlushedAt,
        @JsonProperty("createdAt") String createdAt
) {
}
