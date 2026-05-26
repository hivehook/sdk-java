package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.EventTypeSchema;
import com.hivehook.sdk.types.ListResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link EventTypeSchema}s.
 */
public final class EventTypeSchemaService extends BaseService {
    private static final String FIELDS = "id eventType description schema example createdAt updatedAt";
    private static final String LIST = "query($search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { eventTypeSchemas(search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { eventTypeSchema(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateEventTypeSchemaInput!) { createEventTypeSchema(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateEventTypeSchemaInput!) { updateEventTypeSchema(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteEventTypeSchema(id: $id) }";

    /** @param transport transport to use. */
    public EventTypeSchemaService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List event-type schemas.
     *
     * @param search optional substring search.
     * @param limit  page size (offset pagination).
     * @param offset page offset (offset pagination).
     * @param after  cursor for cursor pagination.
     * @param first  page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<EventTypeSchema> list(String search, Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("eventTypeSchemas"), EventTypeSchema.class);
    }

    /**
     * Fetch an event-type schema by id.
     *
     * @param id schema UUID.
     * @return the schema, or {@code null}.
     */
    public EventTypeSchema get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("eventTypeSchema"), EventTypeSchema.class);
    }

    /**
     * Create an event-type schema.
     *
     * @param input map matching {@code CreateEventTypeSchemaInput}.
     * @return the created schema.
     */
    public EventTypeSchema create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createEventTypeSchema"), EventTypeSchema.class);
    }

    /**
     * Update an event-type schema.
     *
     * @param id    schema UUID.
     * @param input map of fields to update.
     * @return the updated schema.
     */
    public EventTypeSchema update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateEventTypeSchema"), EventTypeSchema.class);
    }

    /**
     * Delete an event-type schema.
     *
     * @param id schema UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteEventTypeSchema").asBoolean(false);
    }

    /**
     * Asynchronously list event-type schemas.
     *
     * @param search optional substring search.
     * @param limit  page size (offset pagination).
     * @param offset page offset (offset pagination).
     * @param after  cursor for cursor pagination.
     * @param first  page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<EventTypeSchema>> listAsync(String search, Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("eventTypeSchemas"), EventTypeSchema.class));
    }

    /**
     * Asynchronously fetch an event-type schema by id.
     *
     * @param id schema UUID.
     * @return future completing with the schema, or {@code null}.
     */
    public CompletableFuture<EventTypeSchema> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("eventTypeSchema"), EventTypeSchema.class));
    }

    /**
     * Asynchronously create an event-type schema.
     *
     * @param input map matching {@code CreateEventTypeSchemaInput}.
     * @return future completing with the created schema.
     */
    public CompletableFuture<EventTypeSchema> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createEventTypeSchema"), EventTypeSchema.class));
    }

    /**
     * Asynchronously update an event-type schema.
     *
     * @param id    schema UUID.
     * @param input map of fields to update.
     * @return future completing with the updated schema.
     */
    public CompletableFuture<EventTypeSchema> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateEventTypeSchema"), EventTypeSchema.class));
    }

    /**
     * Asynchronously delete an event-type schema.
     *
     * @param id schema UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteEventTypeSchema").asBoolean(false));
    }
}
