package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Synchronous response returned to the provider when an event is accepted.
 *
 * @param statusCode  HTTP status code to return (defaults to 202 server-side when 0).
 * @param body        response body to return.
 * @param contentType Content-Type header to set on the response.
 */
public record ResponseConfig(
        @JsonProperty("statusCode") int statusCode,
        @JsonProperty("body") String body,
        @JsonProperty("contentType") String contentType
) {
}
