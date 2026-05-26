package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A tenant organization owning users and resources.
 *
 * @param id                 organization UUID.
 * @param name               human-readable name.
 * @param slug               URL slug.
 * @param ssoEnabled         whether SSO is configured.
 * @param ssoProvider        SSO provider identifier (nullable).
 * @param retentionEvents    inbound-event retention in days.
 * @param retentionMessages  outbound-message retention in days.
 * @param otlpConfig         OTLP exporter configuration (nullable).
 * @param createdAt          creation timestamp in RFC3339.
 * @param updatedAt          last-modified timestamp in RFC3339.
 */
public record Organization(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("slug") String slug,
        @JsonProperty("ssoEnabled") boolean ssoEnabled,
        @JsonProperty("ssoProvider") String ssoProvider,
        @JsonProperty("retentionEvents") int retentionEvents,
        @JsonProperty("retentionMessages") int retentionMessages,
        @JsonProperty("otlpConfig") OTLPConfig otlpConfig,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt
) {
}
