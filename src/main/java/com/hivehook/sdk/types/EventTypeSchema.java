package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * A registered JSON schema describing the shape of one event type.
 *
 * @param eventType   the event-type identifier.
 * @param description human-readable description.
 * @param schema      JSON Schema document (nullable).
 * @param example     example payload conforming to the schema (nullable).
 * @param id          schema UUID.
 * @param createdAt   creation timestamp in RFC3339.
 * @param updatedAt   last-modified timestamp in RFC3339.
 */
public record EventTypeSchema(
        @JsonProperty("id") String id,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("description") String description,
        @JsonProperty("schema") Map<String, Object> schema,
        @JsonProperty("example") Map<String, Object> example,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt
) {
}
