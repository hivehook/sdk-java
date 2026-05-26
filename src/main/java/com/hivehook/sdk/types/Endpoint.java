package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Outbound endpoint owned by an {@link Application}.
 *
 * @param id                endpoint UUID.
 * @param applicationId     owning application UUID.
 * @param url               target URL.
 * @param signingSecret     signing secret used to HMAC outbound requests.
 * @param filterConfig      filter applied before delivery (nullable).
 * @param status            current status.
 * @param type              endpoint type (defaults to {@code "HTTP"}).
 * @param typeConfig        type-specific config (nullable).
 * @param rateLimitRps      per-endpoint rate limit.
 * @param timeoutMs         request timeout in milliseconds.
 * @param retryPolicy       retry policy (nullable).
 * @param headers           static request headers (nullable).
 * @param authType          auth strategy.
 * @param oauth2Config      OAuth2 config (nullable).
 * @param mtlsCert          PEM-encoded mTLS client cert.
 * @param mtlsKey           PEM-encoded mTLS client key.
 * @param deliveryMode      {@code PUSH} or {@code POLL}.
 * @param pollApiKeyPrefix  prefix of the poll API key.
 * @param pollApiKey        full poll API key (only returned on creation/rotation).
 * @param ordered           whether ordered delivery is enabled.
 * @param blockedDeliveryId id of the delivery that is blocking the queue (nullable).
 * @param healthScore       last computed health score in {@code [0, 1]}.
 * @param disabledReason    human-readable reason if disabled (nullable).
 * @param healthConfig      health-based auto-disable configuration (nullable).
 * @param outputFormat      output format identifier.
 * @param createdAt         creation timestamp in RFC3339.
 */
public record Endpoint(
        @JsonProperty("id") String id,
        @JsonProperty("applicationId") String applicationId,
        @JsonProperty("url") String url,
        @JsonProperty("signingSecret") String signingSecret,
        @JsonProperty("filterConfig") FilterConfig filterConfig,
        @JsonProperty("status") String status,
        @JsonProperty("type") String type,
        @JsonProperty("typeConfig") Map<String, Object> typeConfig,
        @JsonProperty("rateLimitRps") int rateLimitRps,
        @JsonProperty("timeoutMs") int timeoutMs,
        @JsonProperty("retryPolicy") RetryPolicy retryPolicy,
        @JsonProperty("headers") Map<String, Object> headers,
        @JsonProperty("authType") String authType,
        @JsonProperty("oauth2Config") OAuth2Config oauth2Config,
        @JsonProperty("mtlsCert") String mtlsCert,
        @JsonProperty("mtlsKey") String mtlsKey,
        @JsonProperty("deliveryMode") String deliveryMode,
        @JsonProperty("pollApiKeyPrefix") String pollApiKeyPrefix,
        @JsonProperty("pollApiKey") String pollApiKey,
        @JsonProperty("ordered") boolean ordered,
        @JsonProperty("blockedDeliveryId") String blockedDeliveryId,
        @JsonProperty("healthScore") double healthScore,
        @JsonProperty("disabledReason") String disabledReason,
        @JsonProperty("healthConfig") HealthConfig healthConfig,
        @JsonProperty("outputFormat") String outputFormat,
        @JsonProperty("createdAt") String createdAt
) {
}
