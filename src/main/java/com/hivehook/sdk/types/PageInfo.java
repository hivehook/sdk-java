package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pagination metadata returned alongside a paginated list of resources.
 *
 * @param total        total number of items across all pages.
 * @param limit        page size that the server returned.
 * @param offset       offset of the first item in the current page.
 * @param endCursor    cursor pointing to the last item; pass it as {@code after} to fetch the next
 *                     page. {@code null} when cursor-based pagination is not in use.
 * @param hasNextPage  {@code true} when more items can be fetched.
 */
public record PageInfo(
        @JsonProperty("total") int total,
        @JsonProperty("limit") int limit,
        @JsonProperty("offset") int offset,
        @JsonProperty("endCursor") String endCursor,
        @JsonProperty("hasNextPage") boolean hasNextPage
) {
}
