package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A retained event stream owned by an application.
 *
 * @param id             stream UUID.
 * @param applicationId  application UUID.
 * @param name           stream name.
 * @param status         stream status (e.g. {@code ACTIVE}, {@code PAUSED}).
 * @param retentionDays  retention period in days.
 * @param createdAt      creation timestamp in RFC3339.
 */
public record Stream(
        @JsonProperty("id") String id,
        @JsonProperty("applicationId") String applicationId,
        @JsonProperty("name") String name,
        @JsonProperty("status") String status,
        @JsonProperty("retentionDays") int retentionDays,
        @JsonProperty("createdAt") String createdAt
) {
}
