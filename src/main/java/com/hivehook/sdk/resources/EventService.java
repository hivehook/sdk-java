package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.Event;
import com.hivehook.sdk.types.ListResult;

import java.util.concurrent.CompletableFuture;

/**
 * Read-only service for ingested {@link Event}s.
 */
public final class EventService extends BaseService {
    private static final String FIELDS = "id sourceId idempotencyKey eventType headers rawBody status receivedAt";
    private static final String LIST = "query($sourceId: UUID, $eventType: String, $status: EventStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { events(sourceId: $sourceId, eventType: $eventType, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { event(id: $id) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public EventService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List events.
     *
     * @param sourceId  optional source UUID filter.
     * @param eventType optional event-type filter.
     * @param status    optional status filter.
     * @param search    optional substring search.
     * @param limit     page size (offset pagination).
     * @param offset    page offset (offset pagination).
     * @param after     cursor for cursor pagination.
     * @param first     page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Event> list(String sourceId, String eventType, String status, String search,
                                  Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "sourceId", sourceId, "eventType", eventType, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("events"), Event.class);
    }

    /**
     * Fetch an event by id.
     *
     * @param id event UUID.
     * @return the event, or {@code null}.
     */
    public Event get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("event"), Event.class);
    }

    /**
     * Asynchronously list events.
     *
     * @param sourceId  optional source UUID filter.
     * @param eventType optional event-type filter.
     * @param status    optional status filter.
     * @param search    optional substring search.
     * @param limit     page size (offset pagination).
     * @param offset    page offset (offset pagination).
     * @param after     cursor for cursor pagination.
     * @param first     page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Event>> listAsync(String sourceId, String eventType, String status, String search,
                                                          Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "sourceId", sourceId, "eventType", eventType, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("events"), Event.class));
    }

    /**
     * Asynchronously fetch an event by id.
     *
     * @param id event UUID.
     * @return future completing with the event, or {@code null}.
     */
    public CompletableFuture<Event> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("event"), Event.class));
    }

    public java.util.List<Event> listAll(String sourceId, String eventType, String status, String search) {
        return paginate(after -> list(sourceId, eventType, status, search, null, null, after, null));
    }
}
