package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.Delivery;
import com.hivehook.sdk.types.ListResult;

import java.util.concurrent.CompletableFuture;

/**
 * Read-only service for inbound {@link Delivery}s.
 */
public final class DeliveryService extends BaseService {
    private static final String FIELDS = "id eventId subscriptionId destinationId status attempts maxAttempts nextAttemptAt createdAt deliveryAttempts { id deliveryId attemptNumber responseStatus responseBody error durationMs attemptedAt }";
    private static final String LIST = "query($eventId: UUID, $destinationId: UUID, $subscriptionId: UUID, $status: DeliveryStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { deliveries(eventId: $eventId, destinationId: $destinationId, subscriptionId: $subscriptionId, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { delivery(id: $id) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public DeliveryService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List deliveries.
     *
     * @param eventId        optional event UUID filter.
     * @param destinationId  optional destination UUID filter.
     * @param subscriptionId optional subscription UUID filter.
     * @param status         optional status filter.
     * @param search         optional substring search.
     * @param limit          page size (offset pagination).
     * @param offset         page offset (offset pagination).
     * @param after          cursor for cursor pagination.
     * @param first          page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Delivery> list(String eventId, String destinationId, String subscriptionId, String status,
                                     String search, Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "eventId", eventId, "destinationId", destinationId, "subscriptionId", subscriptionId,
                "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("deliveries"), Delivery.class);
    }

    /**
     * Fetch a delivery by id.
     *
     * @param id delivery UUID.
     * @return the delivery, or {@code null}.
     */
    public Delivery get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("delivery"), Delivery.class);
    }

    /**
     * Asynchronously list deliveries.
     *
     * @param eventId        optional event UUID filter.
     * @param destinationId  optional destination UUID filter.
     * @param subscriptionId optional subscription UUID filter.
     * @param status         optional status filter.
     * @param search         optional substring search.
     * @param limit          page size (offset pagination).
     * @param offset         page offset (offset pagination).
     * @param after          cursor for cursor pagination.
     * @param first          page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Delivery>> listAsync(String eventId, String destinationId, String subscriptionId, String status,
                                                             String search, Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "eventId", eventId, "destinationId", destinationId, "subscriptionId", subscriptionId,
                "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("deliveries"), Delivery.class));
    }

    /**
     * Asynchronously fetch a delivery by id.
     *
     * @param id delivery UUID.
     * @return future completing with the delivery, or {@code null}.
     */
    public CompletableFuture<Delivery> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("delivery"), Delivery.class));
    }
}
