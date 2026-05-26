package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A composable filter rule. Leaf rules carry {@code path}/{@code value}/{@code operator};
 * boolean rules (e.g. {@code and}, {@code or}) carry a list of nested {@code rules}.
 *
 * @param operator boolean operator or comparison name.
 * @param path     JSON path the rule applies to (nullable for boolean rules).
 * @param value    comparison value, may be any JSON-serialisable type (nullable for boolean rules).
 * @param rules    nested rules combined by {@code operator} (nullable for leaf rules).
 */
public record FilterRule(
        @JsonProperty("operator") String operator,
        @JsonProperty("path") String path,
        @JsonProperty("value") Object value,
        @JsonProperty("rules") List<FilterRule> rules
) {
}
