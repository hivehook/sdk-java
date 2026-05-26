package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Deduplication configuration on a source.
 *
 * @param strategy deduplication strategy (e.g. {@code header}, {@code body_hash}).
 * @param fields   fields participating in the dedup key (nullable).
 * @param window   time window during which duplicates are suppressed, as a Go duration (nullable).
 */
public record DedupConfig(
        @JsonProperty("strategy") String strategy,
        @JsonProperty("fields") List<String> fields,
        @JsonProperty("window") String window
) {
}
