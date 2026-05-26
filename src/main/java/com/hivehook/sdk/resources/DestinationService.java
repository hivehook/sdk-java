package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.Delivery;
import com.hivehook.sdk.types.Destination;
import com.hivehook.sdk.types.ListResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for outbound {@link Destination}s, plus poll-mode helpers.
 */
public final class DestinationService extends BaseService {
    private static final String FIELDS = "id name url signingSecret status type typeConfig timeoutMs rateLimitRps retryPolicy { maxAttempts initialDelay maxDelay backoffFactor } headers authType oauth2Config { tokenUrl clientId clientSecret scopes audience } mtlsCert mtlsKey deliveryMode pollApiKeyPrefix pollApiKey ordered blockedDeliveryId healthScore disabledReason healthConfig { windowHours disableBelow } outputFormat createdAt";

    private static final String LIST = "query($status: DestinationStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { destinations(status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { destination(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateDestinationInput!) { createDestination(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateDestinationInput!) { updateDestination(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteDestination(id: $id) }";
    private static final String ROTATE = "mutation($id: UUID!) { rotateDestinationSecret(id: $id) { " + FIELDS + " } }";
    private static final String POLL_DELIVERIES = "query($destinationId: UUID!, $cursor: String, $limit: Int) { pollDeliveries(destinationId: $destinationId, cursor: $cursor, limit: $limit) { nodes { id eventId subscriptionId destinationId status attempts maxAttempts nextAttemptAt createdAt } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String ACK_DELIVERIES = "mutation($destinationId: UUID!, $deliveryIds: [UUID!]!) { ackDeliveries(destinationId: $destinationId, deliveryIds: $deliveryIds) }";
    private static final String REGENERATE_POLL_KEY = "mutation($destinationId: UUID!) { regeneratePollApiKey(destinationId: $destinationId) { " + FIELDS + " } }";
    private static final String SKIP_DLQ_ENTRY = "mutation($id: UUID!) { skipDLQEntry(id: $id) }";

    /** @param transport transport to use. */
    public DestinationService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List destinations with filtering and pagination.
     *
     * @param status optional status filter.
     * @param search optional substring search.
     * @param limit  page size (offset pagination).
     * @param offset page offset (offset pagination).
     * @param after  cursor for cursor pagination.
     * @param first  page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Destination> list(String status, String search, Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("destinations"), Destination.class);
    }

    /**
     * Fetch a destination by id.
     *
     * @param id destination UUID.
     * @return the destination, or {@code null}.
     */
    public Destination get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("destination"), Destination.class);
    }

    /**
     * Create a destination.
     *
     * @param input map of fields matching {@code CreateDestinationInput}.
     * @return the created destination.
     */
    public Destination create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createDestination"), Destination.class);
    }

    /**
     * Update a destination.
     *
     * @param id    destination UUID.
     * @param input map of fields to update.
     * @return the updated destination.
     */
    public Destination update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateDestination"), Destination.class);
    }

    /**
     * Delete a destination.
     *
     * @param id destination UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteDestination").asBoolean(false);
    }

    /**
     * Rotate the signing secret for a destination.
     *
     * @param id destination UUID.
     * @return the updated destination.
     */
    public Destination rotateSecret(String id) {
        JsonNode data = transport.execute(ROTATE, vars("id", id));
        return toType(data.get("rotateDestinationSecret"), Destination.class);
    }

    /**
     * Pull pending deliveries for a poll-mode destination.
     *
     * @param destinationId destination UUID.
     * @param cursor        opaque cursor returned by a prior call.
     * @param limit         maximum number of deliveries to return.
     * @return paginated list of deliveries.
     */
    public ListResult<Delivery> pollDeliveries(String destinationId, String cursor, Integer limit) {
        JsonNode data = transport.execute(POLL_DELIVERIES, vars(
                "destinationId", destinationId, "cursor", cursor, "limit", limit));
        return parseList(data.get("pollDeliveries"), Delivery.class);
    }

    /**
     * Acknowledge poll-mode deliveries so the server can advance the cursor.
     *
     * @param destinationId destination UUID.
     * @param deliveryIds   list of delivery UUIDs to acknowledge.
     * @return the number of deliveries acknowledged.
     */
    public int ackDeliveries(String destinationId, List<String> deliveryIds) {
        JsonNode data = transport.execute(ACK_DELIVERIES, vars(
                "destinationId", destinationId, "deliveryIds", deliveryIds));
        return data.path("ackDeliveries").asInt(0);
    }

    /**
     * Regenerate the poll API key for a destination.
     *
     * @param destinationId destination UUID.
     * @return the updated destination.
     */
    public Destination regeneratePollApiKey(String destinationId) {
        JsonNode data = transport.execute(REGENERATE_POLL_KEY, vars("destinationId", destinationId));
        return toType(data.get("regeneratePollApiKey"), Destination.class);
    }

    /**
     * Skip a DLQ entry tied to this destination.
     *
     * @param id DLQ entry UUID.
     * @return {@code true} on success.
     */
    public boolean skipDLQEntry(String id) {
        JsonNode data = transport.execute(SKIP_DLQ_ENTRY, vars("id", id));
        return data.path("skipDLQEntry").asBoolean(false);
    }

    /**
     * Asynchronously list destinations with filtering and pagination.
     *
     * @param status optional status filter.
     * @param search optional substring search.
     * @param limit  page size (offset pagination).
     * @param offset page offset (offset pagination).
     * @param after  cursor for cursor pagination.
     * @param first  page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Destination>> listAsync(String status, String search, Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("destinations"), Destination.class));
    }

    /**
     * Asynchronously fetch a destination by id.
     *
     * @param id destination UUID.
     * @return future completing with the destination, or {@code null}.
     */
    public CompletableFuture<Destination> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("destination"), Destination.class));
    }

    /**
     * Asynchronously create a destination.
     *
     * @param input map of fields matching {@code CreateDestinationInput}.
     * @return future completing with the created destination.
     */
    public CompletableFuture<Destination> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createDestination"), Destination.class));
    }

    /**
     * Asynchronously update a destination.
     *
     * @param id    destination UUID.
     * @param input map of fields to update.
     * @return future completing with the updated destination.
     */
    public CompletableFuture<Destination> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateDestination"), Destination.class));
    }

    /**
     * Asynchronously delete a destination.
     *
     * @param id destination UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteDestination").asBoolean(false));
    }

    /**
     * Asynchronously rotate the signing secret for a destination.
     *
     * @param id destination UUID.
     * @return future completing with the updated destination.
     */
    public CompletableFuture<Destination> rotateSecretAsync(String id) {
        return transport.executeAsync(ROTATE, vars("id", id))
                .thenApply(data -> toType(data.get("rotateDestinationSecret"), Destination.class));
    }

    /**
     * Asynchronously pull pending deliveries for a poll-mode destination.
     *
     * @param destinationId destination UUID.
     * @param cursor        opaque cursor returned by a prior call.
     * @param limit         maximum number of deliveries to return.
     * @return future completing with the paginated list of deliveries.
     */
    public CompletableFuture<ListResult<Delivery>> pollDeliveriesAsync(String destinationId, String cursor, Integer limit) {
        return transport.executeAsync(POLL_DELIVERIES, vars(
                "destinationId", destinationId, "cursor", cursor, "limit", limit))
                .thenApply(data -> parseList(data.get("pollDeliveries"), Delivery.class));
    }

    /**
     * Asynchronously acknowledge poll-mode deliveries so the server can advance the cursor.
     *
     * @param destinationId destination UUID.
     * @param deliveryIds   list of delivery UUIDs to acknowledge.
     * @return future completing with the number of deliveries acknowledged.
     */
    public CompletableFuture<Integer> ackDeliveriesAsync(String destinationId, List<String> deliveryIds) {
        return transport.executeAsync(ACK_DELIVERIES, vars(
                "destinationId", destinationId, "deliveryIds", deliveryIds))
                .thenApply(data -> data.path("ackDeliveries").asInt(0));
    }

    /**
     * Asynchronously regenerate the poll API key for a destination.
     *
     * @param destinationId destination UUID.
     * @return future completing with the updated destination.
     */
    public CompletableFuture<Destination> regeneratePollApiKeyAsync(String destinationId) {
        return transport.executeAsync(REGENERATE_POLL_KEY, vars("destinationId", destinationId))
                .thenApply(data -> toType(data.get("regeneratePollApiKey"), Destination.class));
    }

    /**
     * Asynchronously skip a DLQ entry tied to this destination.
     *
     * @param id DLQ entry UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> skipDLQEntryAsync(String id) {
        return transport.executeAsync(SKIP_DLQ_ENTRY, vars("id", id))
                .thenApply(data -> data.path("skipDLQEntry").asBoolean(false));
    }
}
