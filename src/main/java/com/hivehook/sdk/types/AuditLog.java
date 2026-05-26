package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single audit-log entry recording a user or API-key action.
 *
 * @param id           audit-log UUID.
 * @param actorType    type of actor (e.g. {@code USER}, {@code API_KEY}).
 * @param actorId      UUID of the actor.
 * @param actorName    display name captured at the time.
 * @param action       action string (e.g. {@code source.create}).
 * @param resourceType type of resource the action targeted.
 * @param resourceId   UUID of the targeted resource.
 * @param orgId        UUID of the organization scope.
 * @param ipAddress    captured client IP.
 * @param userAgent    captured user agent.
 * @param details      additional details, free-form JSON (nullable).
 * @param createdAt    timestamp in RFC3339.
 */
public record AuditLog(
        @JsonProperty("id") String id,
        @JsonProperty("actorType") String actorType,
        @JsonProperty("actorId") String actorId,
        @JsonProperty("actorName") String actorName,
        @JsonProperty("action") String action,
        @JsonProperty("resourceType") String resourceType,
        @JsonProperty("resourceId") String resourceId,
        @JsonProperty("orgId") String orgId,
        @JsonProperty("ipAddress") String ipAddress,
        @JsonProperty("userAgent") String userAgent,
        @JsonProperty("details") Object details,
        @JsonProperty("createdAt") String createdAt
) {
}
