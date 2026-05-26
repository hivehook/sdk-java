package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A generic page of typed entities together with pagination metadata.
 *
 * <p>Returned by the {@code list(...)} method of every resource service. The {@code nodes} field
 * is never {@code null}; an empty page yields an empty list.
 *
 * @param <T>      element type carried by this page.
 * @param nodes    items on the current page (never {@code null}).
 * @param pageInfo pagination metadata describing this page.
 */
public record ListResult<T>(
        @JsonProperty("nodes") List<T> nodes,
        @JsonProperty("pageInfo") PageInfo pageInfo
) {
}
