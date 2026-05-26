package com.hivehook.sdk.types;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Aggregate status of the Hivehook gateway.
 *
 * @param status                     {@code healthy}, {@code degraded}, or {@code unhealthy}.
 * @param dlqSize                    inbound DLQ depth.
 * @param outboundDlqSize            outbound DLQ depth.
 * @param queueDepth                 work queue depth.
 * @param activeWorkers              currently busy workers.
 * @param totalWorkers               configured worker pool size.
 * @param uptime                     process uptime in seconds.
 * @param version                    gateway version string.
 * @param sourcesTotal               total number of sources.
 * @param destinationsTotal          total number of destinations.
 * @param subscriptionsTotal         total number of subscriptions.
 * @param eventsTotal                cumulative events processed.
 * @param eventsFailed               cumulative events that failed.
 * @param deliveriesTotal            cumulative inbound deliveries created.
 * @param deliveriesPending          inbound deliveries currently pending.
 * @param deliveriesDelivered        inbound deliveries successfully delivered.
 * @param messagesTotal              cumulative outbound messages created.
 * @param outboundDeliveriesTotal    cumulative outbound deliveries.
 * @param outboundDeliveriesPending  outbound deliveries currently pending.
 * @param outboundDeliveriesFailed   cumulative outbound deliveries that failed.
 */
public record SystemStatus(
        @JsonProperty("status") String status,
        @JsonProperty("dlqSize") int dlqSize,
        @JsonProperty("outboundDlqSize") int outboundDlqSize,
        @JsonProperty("queueDepth") int queueDepth,
        @JsonProperty("activeWorkers") int activeWorkers,
        @JsonProperty("totalWorkers") int totalWorkers,
        @JsonProperty("uptime") long uptime,
        @JsonProperty("version") String version,
        @JsonProperty("sourcesTotal") int sourcesTotal,
        @JsonProperty("destinationsTotal") int destinationsTotal,
        @JsonProperty("subscriptionsTotal") int subscriptionsTotal,
        @JsonProperty("eventsTotal") long eventsTotal,
        @JsonProperty("eventsFailed") long eventsFailed,
        @JsonProperty("deliveriesTotal") long deliveriesTotal,
        @JsonProperty("deliveriesPending") long deliveriesPending,
        @JsonProperty("deliveriesDelivered") long deliveriesDelivered,
        @JsonProperty("messagesTotal") long messagesTotal,
        @JsonProperty("outboundDeliveriesTotal") long outboundDeliveriesTotal,
        @JsonProperty("outboundDeliveriesPending") long outboundDeliveriesPending,
        @JsonProperty("outboundDeliveriesFailed") long outboundDeliveriesFailed
) {
}
