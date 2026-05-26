package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.StreamSink;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link StreamSink}s.
 */
public final class StreamSinkService extends BaseService {
    private static final String FIELDS = "id streamId name sinkType config batchSize flushInterval cursorSequence status lastFlushedAt createdAt";

    private static final String LIST = "query($streamId: UUID!, $status: SinkStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { streamSinks(streamId: $streamId, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { streamSink(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateStreamSinkInput!) { createStreamSink(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateStreamSinkInput!) { updateStreamSink(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteStreamSink(id: $id) }";

    /** @param transport transport to use. */
    public StreamSinkService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List sinks attached to a stream.
     *
     * @param streamId stream UUID (required).
     * @param status   optional status filter.
     * @param search   optional substring search.
     * @param limit    page size (offset pagination).
     * @param offset   page offset (offset pagination).
     * @param after    cursor for cursor pagination.
     * @param first    page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<StreamSink> list(String streamId, String status, String search,
                                       Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "streamId", streamId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("streamSinks"), StreamSink.class);
    }

    /**
     * Fetch a sink by id.
     *
     * @param id sink UUID.
     * @return the sink, or {@code null}.
     */
    public StreamSink get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("streamSink"), StreamSink.class);
    }

    /**
     * Create a sink.
     *
     * @param streamId      stream UUID.
     * @param name          sink name.
     * @param sinkType      sink implementation id (e.g. {@code s3}, {@code bigquery}).
     * @param config        sink-specific configuration.
     * @param batchSize     maximum events per flush (nullable).
     * @param flushInterval maximum delay between flushes as a Go duration string (nullable).
     * @return the created sink.
     */
    public StreamSink create(String streamId, String name, String sinkType,
                             Map<String, Object> config, Integer batchSize, String flushInterval) {
        Map<String, Object> input = vars(
                "streamId", streamId, "name", name, "sinkType", sinkType,
                "config", config, "batchSize", batchSize, "flushInterval", flushInterval);
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createStreamSink"), StreamSink.class);
    }

    /**
     * Update a sink.
     *
     * @param id    sink UUID.
     * @param input map of fields to update.
     * @return the updated sink.
     */
    public StreamSink update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateStreamSink"), StreamSink.class);
    }

    /**
     * Delete a sink.
     *
     * @param id sink UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteStreamSink").asBoolean(false);
    }

    /**
     * Asynchronously list sinks attached to a stream.
     *
     * @param streamId stream UUID (required).
     * @param status   optional status filter.
     * @param search   optional substring search.
     * @param limit    page size (offset pagination).
     * @param offset   page offset (offset pagination).
     * @param after    cursor for cursor pagination.
     * @param first    page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<StreamSink>> listAsync(String streamId, String status, String search,
                                                               Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "streamId", streamId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("streamSinks"), StreamSink.class));
    }

    /**
     * Asynchronously fetch a sink by id.
     *
     * @param id sink UUID.
     * @return future completing with the sink, or {@code null}.
     */
    public CompletableFuture<StreamSink> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("streamSink"), StreamSink.class));
    }

    /**
     * Asynchronously create a sink.
     *
     * @param streamId      stream UUID.
     * @param name          sink name.
     * @param sinkType      sink implementation id (e.g. {@code s3}, {@code bigquery}).
     * @param config        sink-specific configuration.
     * @param batchSize     maximum events per flush (nullable).
     * @param flushInterval maximum delay between flushes as a Go duration string (nullable).
     * @return future completing with the created sink.
     */
    public CompletableFuture<StreamSink> createAsync(String streamId, String name, String sinkType,
                                                     Map<String, Object> config, Integer batchSize, String flushInterval) {
        Map<String, Object> input = vars(
                "streamId", streamId, "name", name, "sinkType", sinkType,
                "config", config, "batchSize", batchSize, "flushInterval", flushInterval);
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createStreamSink"), StreamSink.class));
    }

    /**
     * Asynchronously update a sink.
     *
     * @param id    sink UUID.
     * @param input map of fields to update.
     * @return future completing with the updated sink.
     */
    public CompletableFuture<StreamSink> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateStreamSink"), StreamSink.class));
    }

    /**
     * Asynchronously delete a sink.
     *
     * @param id sink UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteStreamSink").asBoolean(false));
    }
}
