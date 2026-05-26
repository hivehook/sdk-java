package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Email channel configuration for alert rules.
 *
 * @param to              recipient addresses.
 * @param subjectTemplate optional subject-line template (nullable).
 */
public record EmailAlertConfig(
        @JsonProperty("to") List<String> to,
        @JsonProperty("subjectTemplate") String subjectTemplate
) {
}
