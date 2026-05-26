package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.Subscription;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link Subscription}s (source-to-destination wiring).
 */
public final class SubscriptionService extends BaseService {
    private static final String FIELDS = "id name sourceId destinationId filterConfig { eventTypes regex bodyMatch { path value operator } rules { path operator value rules { path operator value } } } transformConfig { envelope headers } enabled createdAt";

    private static final String LIST = "query($sourceId: UUID, $destinationId: UUID, $enabled: Boolean, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { subscriptions(sourceId: $sourceId, destinationId: $destinationId, enabled: $enabled, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { subscription(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateSubscriptionInput!) { createSubscription(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateSubscriptionInput!) { updateSubscription(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteSubscription(id: $id) }";

    /** @param transport transport to use. */
    public SubscriptionService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List subscriptions with filtering and pagination.
     *
     * @param sourceId       optional source UUID filter.
     * @param destinationId  optional destination UUID filter.
     * @param enabled        optional enabled filter.
     * @param search         optional substring search.
     * @param limit          page size (offset pagination).
     * @param offset         page offset (offset pagination).
     * @param after          cursor for cursor pagination.
     * @param first          page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Subscription> list(String sourceId, String destinationId, Boolean enabled, String search,
                                         Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "sourceId", sourceId, "destinationId", destinationId, "enabled", enabled, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("subscriptions"), Subscription.class);
    }

    /**
     * Fetch a subscription by id.
     *
     * @param id subscription UUID.
     * @return the subscription, or {@code null}.
     */
    public Subscription get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("subscription"), Subscription.class);
    }

    /**
     * Create a subscription.
     *
     * @param input map of fields matching {@code CreateSubscriptionInput}.
     * @return the created subscription.
     */
    public Subscription create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createSubscription"), Subscription.class);
    }

    /**
     * Update a subscription.
     *
     * @param id    subscription UUID.
     * @param input map of fields to update.
     * @return the updated subscription.
     */
    public Subscription update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateSubscription"), Subscription.class);
    }

    /**
     * Delete a subscription.
     *
     * @param id subscription UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteSubscription").asBoolean(false);
    }

    /**
     * Asynchronously list subscriptions with filtering and pagination.
     *
     * @param sourceId       optional source UUID filter.
     * @param destinationId  optional destination UUID filter.
     * @param enabled        optional enabled filter.
     * @param search         optional substring search.
     * @param limit          page size (offset pagination).
     * @param offset         page offset (offset pagination).
     * @param after          cursor for cursor pagination.
     * @param first          page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Subscription>> listAsync(String sourceId, String destinationId, Boolean enabled, String search,
                                                                 Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "sourceId", sourceId, "destinationId", destinationId, "enabled", enabled, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("subscriptions"), Subscription.class));
    }

    /**
     * Asynchronously fetch a subscription by id.
     *
     * @param id subscription UUID.
     * @return future completing with the subscription, or {@code null}.
     */
    public CompletableFuture<Subscription> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("subscription"), Subscription.class));
    }

    /**
     * Asynchronously create a subscription.
     *
     * @param input map of fields matching {@code CreateSubscriptionInput}.
     * @return future completing with the created subscription.
     */
    public CompletableFuture<Subscription> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createSubscription"), Subscription.class));
    }

    /**
     * Asynchronously update a subscription.
     *
     * @param id    subscription UUID.
     * @param input map of fields to update.
     * @return future completing with the updated subscription.
     */
    public CompletableFuture<Subscription> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateSubscription"), Subscription.class));
    }

    /**
     * Asynchronously delete a subscription.
     *
     * @param id subscription UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteSubscription").asBoolean(false));
    }
}
