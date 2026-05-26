package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON-path based filter rule matched against the body of an incoming event.
 *
 * @param path     JSON path to the value being inspected (e.g. {@code $.data.type}).
 * @param value    expected value to compare against.
 * @param operator comparison operator (e.g. {@code eq}, {@code contains}, {@code in}).
 */
public record BodyMatchRule(
        @JsonProperty("path") String path,
        @JsonProperty("value") String value,
        @JsonProperty("operator") String operator
) {
}
