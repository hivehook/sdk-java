package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of a bulk DLQ replay operation.
 *
 * @param deliveries number of deliveries re-queued.
 */
public record ReplayResult(
        @JsonProperty("deliveries") int deliveries
) {
}
