package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Inbound source that receives webhooks from a provider.
 *
 * <p>All fields except identifiers may be {@code null} or default-valued when not populated by the
 * server.
 *
 * @param id              source UUID.
 * @param name            human-readable name.
 * @param slug            URL slug used in the ingest path.
 * @param providerType    provider implementation id (e.g. {@code "stripe"}, {@code "generic"}).
 * @param verifyConfig    provider-specific signature verification configuration (nullable).
 * @param status          {@code ACTIVE}, {@code PAUSED}, etc.
 * @param rateLimitRps    per-source rate limit; {@code 0} means unlimited.
 * @param spikeProtection enables the burst-protection limiter.
 * @param maxIngestRps    upper bound used by the spike protector.
 * @param brokerConfig    optional message-broker forwarding settings (nullable).
 * @param responseConfig  synchronous response returned to the provider (nullable).
 * @param dedupConfig     deduplication configuration (nullable).
 * @param createdAt       creation timestamp in RFC3339.
 */
public record Source(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("slug") String slug,
        @JsonProperty("providerType") String providerType,
        @JsonProperty("verifyConfig") Map<String, Object> verifyConfig,
        @JsonProperty("status") String status,
        @JsonProperty("rateLimitRps") int rateLimitRps,
        @JsonProperty("spikeProtection") boolean spikeProtection,
        @JsonProperty("maxIngestRps") int maxIngestRps,
        @JsonProperty("brokerConfig") Map<String, Object> brokerConfig,
        @JsonProperty("responseConfig") ResponseConfig responseConfig,
        @JsonProperty("dedupConfig") DedupConfig dedupConfig,
        @JsonProperty("createdAt") String createdAt
) {
}
