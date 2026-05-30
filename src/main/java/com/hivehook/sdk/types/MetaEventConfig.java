package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A meta-event webhook configuration: forwards events about events
 * (delivery.failed, source.created, etc.) to an external receiver.
 *
 * @param id            config UUID.
 * @param name          human-readable name.
 * @param url           receiver URL.
 * @param signingSecret HMAC signing secret returned by the server.
 * @param eventTypes    meta-event types this config subscribes to.
 * @param enabled       whether the config is currently active.
 * @param createdAt     creation timestamp in RFC3339.
 */
public record MetaEventConfig(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("url") String url,
        @JsonProperty("signingSecret") String signingSecret,
        @JsonProperty("eventTypes") List<String> eventTypes,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("createdAt") String createdAt
) {
}
