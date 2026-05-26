package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A rule that fires an alert when a system metric crosses a threshold.
 *
 * @param id            rule UUID.
 * @param name          human-readable name.
 * @param conditionType which metric to evaluate (e.g. {@code DLQ_SIZE}).
 * @param threshold     numeric threshold.
 * @param webhookUrl    URL to POST when the alert fires (used for {@code WEBHOOK} channel).
 * @param channel       {@code WEBHOOK}, {@code EMAIL}, or {@code SLACK}.
 * @param emailConfig   email-specific config (nullable).
 * @param slackConfig   Slack-specific config (nullable).
 * @param cooldown      minimum interval between firings, as a Go duration string.
 * @param enabled       whether the rule is active.
 * @param createdAt     creation timestamp in RFC3339.
 */
public record AlertRule(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("conditionType") String conditionType,
        @JsonProperty("threshold") int threshold,
        @JsonProperty("webhookUrl") String webhookUrl,
        @JsonProperty("channel") String channel,
        @JsonProperty("emailConfig") EmailAlertConfig emailConfig,
        @JsonProperty("slackConfig") SlackAlertConfig slackConfig,
        @JsonProperty("cooldown") String cooldown,
        @JsonProperty("enabled") boolean enabled,
        @JsonProperty("createdAt") String createdAt
) {
}
