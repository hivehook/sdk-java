package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.Stream;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for retained event {@link Stream}s.
 */
public final class StreamService extends BaseService {
    private static final String FIELDS = "id applicationId name status retentionDays createdAt";

    private static final String LIST = "query($applicationId: UUID, $status: StreamStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { streams(applicationId: $applicationId, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { stream(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateStreamInput!) { createStream(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateStreamInput!) { updateStream(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteStream(id: $id) }";

    /** @param transport transport to use. */
    public StreamService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List streams.
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
    public ListResult<Stream> list(String applicationId, String status, String search,
                                   Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "applicationId", applicationId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("streams"), Stream.class);
    }

    /**
     * Fetch a stream by id.
     *
     * @param id stream UUID.
     * @return the stream, or {@code null}.
     */
    public Stream get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("stream"), Stream.class);
    }

    /**
     * Create a stream.
     *
     * @param applicationId application UUID.
     * @param name          stream name.
     * @param retentionDays retention period in days (nullable).
     * @param status        initial status (nullable).
     * @return the created stream.
     */
    public Stream create(String applicationId, String name, Integer retentionDays, String status) {
        Map<String, Object> input = vars(
                "applicationId", applicationId, "name", name,
                "retentionDays", retentionDays, "status", status);
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createStream"), Stream.class);
    }

    /**
     * Update a stream.
     *
     * @param id    stream UUID.
     * @param input map of fields to update.
     * @return the updated stream.
     */
    public Stream update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateStream"), Stream.class);
    }

    /**
     * Delete a stream.
     *
     * @param id stream UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteStream").asBoolean(false);
    }

    /**
     * Asynchronously list streams.
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
    public CompletableFuture<ListResult<Stream>> listAsync(String applicationId, String status, String search,
                                                           Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "applicationId", applicationId, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("streams"), Stream.class));
    }

    /**
     * Asynchronously fetch a stream by id.
     *
     * @param id stream UUID.
     * @return future completing with the stream, or {@code null}.
     */
    public CompletableFuture<Stream> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("stream"), Stream.class));
    }

    /**
     * Asynchronously create a stream.
     *
     * @param applicationId application UUID.
     * @param name          stream name.
     * @param retentionDays retention period in days (nullable).
     * @param status        initial status (nullable).
     * @return future completing with the created stream.
     */
    public CompletableFuture<Stream> createAsync(String applicationId, String name, Integer retentionDays, String status) {
        Map<String, Object> input = vars(
                "applicationId", applicationId, "name", name,
                "retentionDays", retentionDays, "status", status);
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createStream"), Stream.class));
    }

    /**
     * Asynchronously update a stream.
     *
     * @param id    stream UUID.
     * @param input map of fields to update.
     * @return future completing with the updated stream.
     */
    public CompletableFuture<Stream> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateStream"), Stream.class));
    }

    /**
     * Asynchronously delete a stream.
     *
     * @param id stream UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteStream").asBoolean(false));
    }
}
