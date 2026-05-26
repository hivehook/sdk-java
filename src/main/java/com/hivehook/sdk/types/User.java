package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A user belonging to an organization.
 *
 * @param id              user UUID.
 * @param organizationId  organization UUID.
 * @param email           user email.
 * @param name            display name.
 * @param role            role string (e.g. {@code ADMIN}, {@code MEMBER}).
 * @param lastLoginAt     last successful login timestamp (nullable).
 * @param createdAt       creation timestamp in RFC3339.
 * @param updatedAt       last-modified timestamp in RFC3339.
 */
public record User(
        @JsonProperty("id") String id,
        @JsonProperty("organizationId") String organizationId,
        @JsonProperty("email") String email,
        @JsonProperty("name") String name,
        @JsonProperty("role") String role,
        @JsonProperty("lastLoginAt") String lastLoginAt,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("updatedAt") String updatedAt
) {
}
