package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.OutboundDelivery;

import java.util.concurrent.CompletableFuture;

/**
 * Read-only service for {@link OutboundDelivery}s.
 */
public final class OutboundDeliveryService extends BaseService {
    private static final String FIELDS = "id messageId endpointId status attempts maxAttempts nextAttemptAt createdAt deliveryAttempts { id deliveryId attemptNumber responseStatus responseBody error durationMs attemptedAt }";
    private static final String LIST = "query($messageId: UUID, $endpointId: UUID, $status: DeliveryStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { outboundDeliveries(messageId: $messageId, endpointId: $endpointId, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { outboundDelivery(id: $id) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public OutboundDeliveryService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List outbound deliveries.
     *
     * @param messageId  optional message UUID filter.
     * @param endpointId optional endpoint UUID filter.
     * @param status     optional status filter.
     * @param search     optional substring search.
     * @param limit      page size (offset pagination).
     * @param offset     page offset (offset pagination).
     * @param after      cursor for cursor pagination.
     * @param first      page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<OutboundDelivery> list(String messageId, String endpointId, String status, String search,
                                             Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "messageId", messageId, "endpointId", endpointId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("outboundDeliveries"), OutboundDelivery.class);
    }

    /**
     * Fetch an outbound delivery by id.
     *
     * @param id outbound delivery UUID.
     * @return the delivery, or {@code null}.
     */
    public OutboundDelivery get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("outboundDelivery"), OutboundDelivery.class);
    }

    /**
     * Asynchronously list outbound deliveries.
     *
     * @param messageId  optional message UUID filter.
     * @param endpointId optional endpoint UUID filter.
     * @param status     optional status filter.
     * @param search     optional substring search.
     * @param limit      page size (offset pagination).
     * @param offset     page offset (offset pagination).
     * @param after      cursor for cursor pagination.
     * @param first      page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<OutboundDelivery>> listAsync(String messageId, String endpointId, String status, String search,
                                                                     Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "messageId", messageId, "endpointId", endpointId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("outboundDeliveries"), OutboundDelivery.class));
    }

    /**
     * Asynchronously fetch an outbound delivery by id.
     *
     * @param id outbound delivery UUID.
     * @return future completing with the delivery, or {@code null}.
     */
    public CompletableFuture<OutboundDelivery> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("outboundDelivery"), OutboundDelivery.class));
    }

    public java.util.List<OutboundDelivery> listAll(String messageId, String endpointId, String status, String search) {
        return paginate(after -> list(messageId, endpointId, status, search, null, null, after, null));
    }
}
