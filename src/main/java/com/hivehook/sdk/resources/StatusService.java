package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.SystemStatus;

import java.util.concurrent.CompletableFuture;

/**
 * Read-only service exposing aggregate gateway status.
 */
public final class StatusService extends BaseService {
    private static final String GET = "query { status { status dlqSize outboundDlqSize queueDepth activeWorkers totalWorkers uptime version sourcesTotal destinationsTotal subscriptionsTotal eventsTotal eventsFailed deliveriesTotal deliveriesPending deliveriesDelivered messagesTotal outboundDeliveriesTotal outboundDeliveriesPending outboundDeliveriesFailed } }";

    /** @param transport transport to use. */
    public StatusService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * Fetch the gateway system status.
     *
     * @return system status snapshot.
     */
    public SystemStatus get() {
        JsonNode data = transport.execute(GET, null);
        return toType(data.get("status"), SystemStatus.class);
    }

    /**
     * Asynchronously fetch the gateway system status.
     *
     * @return future completing with the system status snapshot.
     */
    public CompletableFuture<SystemStatus> getAsync() {
        return transport.executeAsync(GET, null)
                .thenApply(data -> toType(data.get("status"), SystemStatus.class));
    }
}
