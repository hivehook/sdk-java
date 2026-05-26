package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.Endpoint;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.OutboundDelivery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for outbound {@link Endpoint}s, plus poll-mode helpers.
 */
public final class EndpointService extends BaseService {
    private static final String FIELDS = "id applicationId url signingSecret filterConfig { eventTypes regex bodyMatch { path value operator } rules { path operator value rules { path operator value } } } status type typeConfig rateLimitRps timeoutMs retryPolicy { maxAttempts initialDelay maxDelay backoffFactor } headers authType oauth2Config { tokenUrl clientId clientSecret scopes audience } mtlsCert mtlsKey deliveryMode pollApiKeyPrefix pollApiKey ordered blockedDeliveryId healthScore disabledReason healthConfig { windowHours disableBelow } outputFormat createdAt";
    private static final String LIST = "query($applicationId: UUID, $status: EndpointStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { endpoints(applicationId: $applicationId, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { endpoint(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateEndpointInput!) { createEndpoint(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateEndpointInput!) { updateEndpoint(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteEndpoint(id: $id) }";
    private static final String ROTATE = "mutation($id: UUID!) { rotateEndpointSecret(id: $id) { " + FIELDS + " } }";
    private static final String POLL_DELIVERIES = "query($endpointId: UUID!, $cursor: String, $limit: Int) { pollOutboundDeliveries(endpointId: $endpointId, cursor: $cursor, limit: $limit) { nodes { id messageId endpointId status attempts maxAttempts nextAttemptAt createdAt } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String ACK_DELIVERIES = "mutation($endpointId: UUID!, $deliveryIds: [UUID!]!) { ackOutboundDeliveries(endpointId: $endpointId, deliveryIds: $deliveryIds) }";
    private static final String REGENERATE_POLL_KEY = "mutation($endpointId: UUID!) { regenerateOutboundPollApiKey(endpointId: $endpointId) { " + FIELDS + " } }";
    private static final String SKIP_OUTBOUND_DLQ_ENTRY = "mutation($id: UUID!) { skipOutboundDlqEntry(id: $id) }";

    /** @param transport transport to use. */
    public EndpointService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List endpoints.
     *
     * @param applicationId optional application UUID filter.
     * @param status        optional status filter.
     * @param search        optional substring search.
     * @param limit         page size (offset pagination).
     * @param offset        page offset (offset pagination).
     * @param after         cursor for cursor pagination.
     * @param first         page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Endpoint> list(String applicationId, String status, String search,
                                     Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "applicationId", applicationId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("endpoints"), Endpoint.class);
    }

    /**
     * Fetch an endpoint by id.
     *
     * @param id endpoint UUID.
     * @return the endpoint, or {@code null}.
     */
    public Endpoint get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("endpoint"), Endpoint.class);
    }

    /**
     * Create an endpoint.
     *
     * @param input map matching {@code CreateEndpointInput}.
     * @return the created endpoint.
     */
    public Endpoint create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createEndpoint"), Endpoint.class);
    }

    /**
     * Update an endpoint.
     *
     * @param id    endpoint UUID.
     * @param input map of fields to update.
     * @return the updated endpoint.
     */
    public Endpoint update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateEndpoint"), Endpoint.class);
    }

    /**
     * Delete an endpoint.
     *
     * @param id endpoint UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteEndpoint").asBoolean(false);
    }

    /**
     * Rotate the signing secret for an endpoint.
     *
     * @param id endpoint UUID.
     * @return the updated endpoint.
     */
    public Endpoint rotateSecret(String id) {
        JsonNode data = transport.execute(ROTATE, vars("id", id));
        return toType(data.get("rotateEndpointSecret"), Endpoint.class);
    }

    /**
     * Pull pending outbound deliveries for a poll-mode endpoint.
     *
     * @param endpointId endpoint UUID.
     * @param cursor     opaque cursor returned by a prior call.
     * @param limit      maximum number of deliveries to return.
     * @return paginated list of outbound deliveries.
     */
    public ListResult<OutboundDelivery> pollDeliveries(String endpointId, String cursor, Integer limit) {
        JsonNode data = transport.execute(POLL_DELIVERIES, vars(
                "endpointId", endpointId, "cursor", cursor, "limit", limit));
        return parseList(data.get("pollOutboundDeliveries"), OutboundDelivery.class);
    }

    /**
     * Acknowledge outbound deliveries so the server can advance the cursor.
     *
     * @param endpointId  endpoint UUID.
     * @param deliveryIds UUIDs to acknowledge.
     * @return the number of deliveries acknowledged.
     */
    public int ackDeliveries(String endpointId, List<String> deliveryIds) {
        JsonNode data = transport.execute(ACK_DELIVERIES, vars(
                "endpointId", endpointId, "deliveryIds", deliveryIds));
        return data.path("ackOutboundDeliveries").asInt(0);
    }

    /**
     * Regenerate the poll API key for an endpoint.
     *
     * @param endpointId endpoint UUID.
     * @return the updated endpoint.
     */
    public Endpoint regeneratePollApiKey(String endpointId) {
        JsonNode data = transport.execute(REGENERATE_POLL_KEY, vars("endpointId", endpointId));
        return toType(data.get("regenerateOutboundPollApiKey"), Endpoint.class);
    }

    /**
     * Skip an outbound DLQ entry tied to this endpoint.
     *
     * @param id DLQ entry UUID.
     * @return {@code true} on success.
     */
    public boolean skipOutboundDLQEntry(String id) {
        JsonNode data = transport.execute(SKIP_OUTBOUND_DLQ_ENTRY, vars("id", id));
        return data.path("skipOutboundDlqEntry").asBoolean(false);
    }

    /**
     * Asynchronously list endpoints.
     *
     * @param applicationId optional application UUID filter.
     * @param status        optional status filter.
     * @param search        optional substring search.
     * @param limit         page size (offset pagination).
     * @param offset        page offset (offset pagination).
     * @param after         cursor for cursor pagination.
     * @param first         page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Endpoint>> listAsync(String applicationId, String status, String search,
                                                             Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "applicationId", applicationId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("endpoints"), Endpoint.class));
    }

    /**
     * Asynchronously fetch an endpoint by id.
     *
     * @param id endpoint UUID.
     * @return future completing with the endpoint, or {@code null}.
     */
    public CompletableFuture<Endpoint> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("endpoint"), Endpoint.class));
    }

    /**
     * Asynchronously create an endpoint.
     *
     * @param input map matching {@code CreateEndpointInput}.
     * @return future completing with the created endpoint.
     */
    public CompletableFuture<Endpoint> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createEndpoint"), Endpoint.class));
    }

    /**
     * Asynchronously update an endpoint.
     *
     * @param id    endpoint UUID.
     * @param input map of fields to update.
     * @return future completing with the updated endpoint.
     */
    public CompletableFuture<Endpoint> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateEndpoint"), Endpoint.class));
    }

    /**
     * Asynchronously delete an endpoint.
     *
     * @param id endpoint UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteEndpoint").asBoolean(false));
    }

    /**
     * Asynchronously rotate the signing secret for an endpoint.
     *
     * @param id endpoint UUID.
     * @return future completing with the updated endpoint.
     */
    public CompletableFuture<Endpoint> rotateSecretAsync(String id) {
        return transport.executeAsync(ROTATE, vars("id", id))
                .thenApply(data -> toType(data.get("rotateEndpointSecret"), Endpoint.class));
    }

    /**
     * Asynchronously pull pending outbound deliveries for a poll-mode endpoint.
     *
     * @param endpointId endpoint UUID.
     * @param cursor     opaque cursor returned by a prior call.
     * @param limit      maximum number of deliveries to return.
     * @return future completing with the paginated list of outbound deliveries.
     */
    public CompletableFuture<ListResult<OutboundDelivery>> pollDeliveriesAsync(String endpointId, String cursor, Integer limit) {
        return transport.executeAsync(POLL_DELIVERIES, vars(
                "endpointId", endpointId, "cursor", cursor, "limit", limit))
                .thenApply(data -> parseList(data.get("pollOutboundDeliveries"), OutboundDelivery.class));
    }

    /**
     * Asynchronously acknowledge outbound deliveries so the server can advance the cursor.
     *
     * @param endpointId  endpoint UUID.
     * @param deliveryIds UUIDs to acknowledge.
     * @return future completing with the number of deliveries acknowledged.
     */
    public CompletableFuture<Integer> ackDeliveriesAsync(String endpointId, List<String> deliveryIds) {
        return transport.executeAsync(ACK_DELIVERIES, vars(
                "endpointId", endpointId, "deliveryIds", deliveryIds))
                .thenApply(data -> data.path("ackOutboundDeliveries").asInt(0));
    }

    /**
     * Asynchronously regenerate the poll API key for an endpoint.
     *
     * @param endpointId endpoint UUID.
     * @return future completing with the updated endpoint.
     */
    public CompletableFuture<Endpoint> regeneratePollApiKeyAsync(String endpointId) {
        return transport.executeAsync(REGENERATE_POLL_KEY, vars("endpointId", endpointId))
                .thenApply(data -> toType(data.get("regenerateOutboundPollApiKey"), Endpoint.class));
    }

    /**
     * Asynchronously skip an outbound DLQ entry tied to this endpoint.
     *
     * @param id DLQ entry UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> skipOutboundDLQEntryAsync(String id) {
        return transport.executeAsync(SKIP_OUTBOUND_DLQ_ENTRY, vars("id", id))
                .thenApply(data -> data.path("skipOutboundDlqEntry").asBoolean(false));
    }
}
