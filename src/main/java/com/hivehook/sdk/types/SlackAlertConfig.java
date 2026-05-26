package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Slack channel configuration for alert rules.
 *
 * @param webhookUrl Slack incoming-webhook URL.
 * @param channel    target channel override (nullable; defaults to the webhook's channel).
 */
public record SlackAlertConfig(
        @JsonProperty("webhookUrl") String webhookUrl,
        @JsonProperty("channel") String channel
) {
}
