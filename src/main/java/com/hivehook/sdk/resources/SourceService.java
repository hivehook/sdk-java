package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.Source;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for inbound {@link Source}s.
 */
public final class SourceService extends BaseService {
    private static final String FIELDS = "id name slug providerType verifyConfig status rateLimitRps spikeProtection maxIngestRps brokerConfig responseConfig { statusCode body contentType } dedupConfig { strategy fields window } createdAt";

    private static final String LIST = "query($status: SourceStatus, $providerType: String, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { sources(status: $status, providerType: $providerType, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { source(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateSourceInput!) { createSource(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateSourceInput!) { updateSource(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteSource(id: $id) }";
    private static final String ROTATE = "mutation($id: UUID!) { rotateSourceSecret(id: $id) { " + FIELDS + " } }";
    private static final String CLEAR_SECONDARY = "mutation($id: UUID!) { clearSourceSecondarySecret(id: $id) { " + FIELDS + " } }";

    /**
     * @param transport transport to use for this service.
     */
    public SourceService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List sources with filtering and pagination.
     *
     * @param status        optional status filter.
     * @param providerType  optional provider-type filter.
     * @param search        optional substring search.
     * @param limit         page size (offset pagination).
     * @param offset        page offset (offset pagination).
     * @param after         cursor for cursor pagination.
     * @param first         page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Source> list(String status, String providerType, String search,
                                   Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "status", status, "providerType", providerType, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("sources"), Source.class);
    }

    /**
     * Fetch a source by id.
     *
     * @param id source UUID.
     * @return the source, or {@code null} when the server returns null.
     */
    public Source get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("source"), Source.class);
    }

    /**
     * Create a source.
     *
     * @param name          human-readable name.
     * @param slug          URL slug.
     * @param providerType  provider implementation id.
     * @param verifyConfig  provider-specific verification config (nullable).
     * @return the created source.
     */
    public Source create(String name, String slug, String providerType, Map<String, Object> verifyConfig) {
        Map<String, Object> input = vars(
                "name", name,
                "slug", slug,
                "providerType", providerType,
                "verifyConfig", verifyConfig);
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createSource"), Source.class);
    }

    /**
     * Update a source.
     *
     * @param id    source UUID.
     * @param input map of fields to update; keys must match the GraphQL input type.
     * @return the updated source.
     */
    public Source update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateSource"), Source.class);
    }

    /**
     * Delete a source.
     *
     * @param id source UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteSource").asBoolean(false);
    }

    /**
     * Rotate the signing secret for a source.
     *
     * @param id source UUID.
     * @return the updated source carrying the new secret.
     */
    public Source rotateSecret(String id) {
        JsonNode data = transport.execute(ROTATE, vars("id", id));
        return toType(data.get("rotateSourceSecret"), Source.class);
    }

    /**
     * Clear a source's secondary (legacy) secret.
     *
     * @param id source UUID.
     * @return the updated source.
     */
    public Source clearSecondarySecret(String id) {
        JsonNode data = transport.execute(CLEAR_SECONDARY, vars("id", id));
        return toType(data.get("clearSourceSecondarySecret"), Source.class);
    }

    /**
     * Asynchronously list sources with filtering and pagination.
     *
     * @param status        optional status filter.
     * @param providerType  optional provider-type filter.
     * @param search        optional substring search.
     * @param limit         page size (offset pagination).
     * @param offset        page offset (offset pagination).
     * @param after         cursor for cursor pagination.
     * @param first         page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Source>> listAsync(String status, String providerType, String search,
                                                           Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "status", status, "providerType", providerType, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("sources"), Source.class));
    }

    /**
     * Asynchronously fetch a source by id.
     *
     * @param id source UUID.
     * @return future completing with the source, or {@code null} when the server returns null.
     */
    public CompletableFuture<Source> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("source"), Source.class));
    }

    /**
     * Asynchronously create a source.
     *
     * @param name          human-readable name.
     * @param slug          URL slug.
     * @param providerType  provider implementation id.
     * @param verifyConfig  provider-specific verification config (nullable).
     * @return future completing with the created source.
     */
    public CompletableFuture<Source> createAsync(String name, String slug, String providerType, Map<String, Object> verifyConfig) {
        Map<String, Object> input = vars(
                "name", name,
                "slug", slug,
                "providerType", providerType,
                "verifyConfig", verifyConfig);
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createSource"), Source.class));
    }

    /**
     * Asynchronously update a source.
     *
     * @param id    source UUID.
     * @param input map of fields to update; keys must match the GraphQL input type.
     * @return future completing with the updated source.
     */
    public CompletableFuture<Source> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateSource"), Source.class));
    }

    /**
     * Asynchronously delete a source.
     *
     * @param id source UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteSource").asBoolean(false));
    }

    /**
     * Asynchronously rotate the signing secret for a source.
     *
     * @param id source UUID.
     * @return future completing with the updated source carrying the new secret.
     */
    public CompletableFuture<Source> rotateSecretAsync(String id) {
        return transport.executeAsync(ROTATE, vars("id", id))
                .thenApply(data -> toType(data.get("rotateSourceSecret"), Source.class));
    }

    /**
     * Asynchronously clear a source's secondary (legacy) secret.
     *
     * @param id source UUID.
     * @return future completing with the updated source.
     */
    public CompletableFuture<Source> clearSecondarySecretAsync(String id) {
        return transport.executeAsync(CLEAR_SECONDARY, vars("id", id))
                .thenApply(data -> toType(data.get("clearSourceSecondarySecret"), Source.class));
    }

    public java.util.List<Source> listAll(String status, String providerType, String search) {
        return paginate(after -> list(status, providerType, search, null, null, after, null));
    }
}
