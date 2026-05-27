package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.StreamConsumer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link StreamConsumer}s.
 */
public final class StreamConsumerService extends BaseService {
    private static final String FIELDS = "id streamId name cursorSequence createdAt updatedAt";

    private static final String LIST = "query($streamId: UUID!, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { streamConsumers(streamId: $streamId, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { streamConsumer(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateStreamConsumerInput!) { createStreamConsumer(input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteStreamConsumer(id: $id) }";
    private static final String ADVANCE_CURSOR = "mutation($id: UUID!, $sequence: Int!) { advanceConsumerCursor(id: $id, sequence: $sequence) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public StreamConsumerService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List consumers attached to a stream.
     *
     * @param streamId stream UUID (required).
     * @param search   optional substring search.
     * @param limit    page size (offset pagination).
     * @param offset   page offset (offset pagination).
     * @param after    cursor for cursor pagination.
     * @param first    page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<StreamConsumer> list(String streamId, String search,
                                           Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "streamId", streamId, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("streamConsumers"), StreamConsumer.class);
    }

    /**
     * Fetch a consumer by id.
     *
     * @param id consumer UUID.
     * @return the consumer, or {@code null}.
     */
    public StreamConsumer get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("streamConsumer"), StreamConsumer.class);
    }

    /**
     * Create a consumer.
     *
     * @param streamId stream UUID.
     * @param name     consumer name.
     * @return the created consumer.
     */
    public StreamConsumer create(String streamId, String name) {
        Map<String, Object> input = vars("streamId", streamId, "name", name);
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createStreamConsumer"), StreamConsumer.class);
    }

    /**
     * Delete a consumer.
     *
     * @param id consumer UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteStreamConsumer").asBoolean(false);
    }

    /**
     * Advance a consumer cursor to a specific sequence number.
     *
     * @param id       consumer UUID.
     * @param sequence target sequence.
     * @return the updated consumer.
     */
    public StreamConsumer advanceCursor(String id, long sequence) {
        JsonNode data = transport.execute(ADVANCE_CURSOR, vars("id", id, "sequence", sequence));
        return toType(data.get("advanceConsumerCursor"), StreamConsumer.class);
    }

    /**
     * Asynchronously list consumers attached to a stream.
     *
     * @param streamId stream UUID (required).
     * @param search   optional substring search.
     * @param limit    page size (offset pagination).
     * @param offset   page offset (offset pagination).
     * @param after    cursor for cursor pagination.
     * @param first    page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<StreamConsumer>> listAsync(String streamId, String search,
                                                                   Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "streamId", streamId, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("streamConsumers"), StreamConsumer.class));
    }

    /**
     * Asynchronously fetch a consumer by id.
     *
     * @param id consumer UUID.
     * @return future completing with the consumer, or {@code null}.
     */
    public CompletableFuture<StreamConsumer> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("streamConsumer"), StreamConsumer.class));
    }

    /**
     * Asynchronously create a consumer.
     *
     * @param streamId stream UUID.
     * @param name     consumer name.
     * @return future completing with the created consumer.
     */
    public CompletableFuture<StreamConsumer> createAsync(String streamId, String name) {
        Map<String, Object> input = vars("streamId", streamId, "name", name);
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createStreamConsumer"), StreamConsumer.class));
    }

    /**
     * Asynchronously delete a consumer.
     *
     * @param id consumer UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteStreamConsumer").asBoolean(false));
    }

    /**
     * Asynchronously advance a consumer cursor to a specific sequence number.
     *
     * @param id       consumer UUID.
     * @param sequence target sequence.
     * @return future completing with the updated consumer.
     */
    public CompletableFuture<StreamConsumer> advanceCursorAsync(String id, long sequence) {
        return transport.executeAsync(ADVANCE_CURSOR, vars("id", id, "sequence", sequence))
                .thenApply(data -> toType(data.get("advanceConsumerCursor"), StreamConsumer.class));
    }

    public java.util.List<StreamConsumer> listAll(String streamId, String search) {
        return paginate(after -> list(streamId, search, null, null, after, null));
    }
}
