package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.MetaEventConfig;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link MetaEventConfig}s.
 */
public final class MetaEventConfigService extends BaseService {
    private static final String FIELDS = "id name url signingSecret eventTypes enabled createdAt";
    private static final String LIST = "query($search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { metaEventConfigs(search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { metaEventConfig(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateMetaEventConfigInput!) { createMetaEventConfig(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateMetaEventConfigInput!) { updateMetaEventConfig(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteMetaEventConfig(id: $id) }";

    public MetaEventConfigService(GraphQLTransport transport) {
        super(transport);
    }

    public ListResult<MetaEventConfig> list(String search, Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars("search", search, "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("metaEventConfigs"), MetaEventConfig.class);
    }

    public MetaEventConfig get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("metaEventConfig"), MetaEventConfig.class);
    }

    public MetaEventConfig create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createMetaEventConfig"), MetaEventConfig.class);
    }

    public MetaEventConfig update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateMetaEventConfig"), MetaEventConfig.class);
    }

    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteMetaEventConfig").asBoolean(false);
    }

    public CompletableFuture<ListResult<MetaEventConfig>> listAsync(String search, Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars("search", search, "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("metaEventConfigs"), MetaEventConfig.class));
    }

    public CompletableFuture<MetaEventConfig> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("metaEventConfig"), MetaEventConfig.class));
    }

    public CompletableFuture<MetaEventConfig> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createMetaEventConfig"), MetaEventConfig.class));
    }

    public CompletableFuture<MetaEventConfig> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateMetaEventConfig"), MetaEventConfig.class));
    }

    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteMetaEventConfig").asBoolean(false));
    }

    public java.util.List<MetaEventConfig> listAll(String search) {
        return paginate(after -> list(search, null, null, after, null));
    }
}
