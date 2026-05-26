package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A user-saved reference to an event.
 *
 * @param id        bookmark UUID.
 * @param eventId   UUID of the event.
 * @param name      bookmark name.
 * @param notes     freeform user notes.
 * @param createdAt creation timestamp in RFC3339.
 */
public record Bookmark(
        @JsonProperty("id") String id,
        @JsonProperty("eventId") String eventId,
        @JsonProperty("name") String name,
        @JsonProperty("notes") String notes,
        @JsonProperty("createdAt") String createdAt
) {
}
