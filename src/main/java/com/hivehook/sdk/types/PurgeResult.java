package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of a DLQ purge operation.
 *
 * @param purged number of entries removed.
 */
public record PurgeResult(
        @JsonProperty("purged") int purged
) {
}
