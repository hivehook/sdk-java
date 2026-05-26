package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Filter configuration that decides whether an event should be forwarded to a destination.
 *
 * <p>Each field is optional; populated fields combine using AND semantics.
 *
 * @param eventTypes exact event-type strings that the event must match (nullable).
 * @param regex      regular expressions matched against the event type (nullable).
 * @param bodyMatch  rules applied to the event body (nullable).
 * @param rules      compound rule tree (nullable).
 */
public record FilterConfig(
        @JsonProperty("eventTypes") List<String> eventTypes,
        @JsonProperty("regex") List<String> regex,
        @JsonProperty("bodyMatch") List<BodyMatchRule> bodyMatch,
        @JsonProperty("rules") List<FilterRule> rules
) {
}
