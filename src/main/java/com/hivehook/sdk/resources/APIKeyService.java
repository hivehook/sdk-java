package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.APIKey;
import com.hivehook.sdk.types.APIKeyWithSecret;
import com.hivehook.sdk.types.ListResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link APIKey}s.
 */
public final class APIKeyService extends BaseService {
    private static final String FIELDS = "id name keyPrefix scopes sourceIds createdAt expiresAt revokedAt lastUsedAt";
    private static final String LIST = "query($search: String, $limit: Int, $offset: Int) { apiKeys(search: $search, limit: $limit, offset: $offset) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { apiKey(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateAPIKeyInput!) { createAPIKey(input: $input) { apiKey { " + FIELDS + " } rawKey } }";
    private static final String REVOKE = "mutation($id: UUID!) { revokeAPIKey(id: $id) }";

    /** @param transport transport to use. */
    public APIKeyService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List API keys.
     *
     * @param search optional substring search.
     * @param limit  page size.
     * @param offset page offset.
     * @return paginated result.
     */
    public ListResult<APIKey> list(String search, Integer limit, Integer offset) {
        JsonNode data = transport.execute(LIST, vars("search", search, "limit", limit, "offset", offset));
        return parseList(data.get("apiKeys"), APIKey.class);
    }

    /**
     * Fetch an API key by id.
     *
     * @param id API key UUID.
     * @return the key (without the raw secret), or {@code null}.
     */
    public APIKey get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("apiKey"), APIKey.class);
    }

    /**
     * Create an API key. The returned object exposes the raw secret exactly once.
     *
     * @param input map of fields matching {@code CreateAPIKeyInput}.
     * @return the created key plus its raw secret.
     */
    public APIKeyWithSecret create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createAPIKey"), APIKeyWithSecret.class);
    }

    /**
     * Revoke an API key.
     *
     * @param id API key UUID.
     * @return {@code true} on success.
     */
    public boolean revoke(String id) {
        JsonNode data = transport.execute(REVOKE, vars("id", id));
        return data.path("revokeAPIKey").asBoolean(false);
    }

    /**
     * Asynchronously list API keys.
     *
     * @param search optional substring search.
     * @param limit  page size.
     * @param offset page offset.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<APIKey>> listAsync(String search, Integer limit, Integer offset) {
        return transport.executeAsync(LIST, vars("search", search, "limit", limit, "offset", offset))
                .thenApply(data -> parseList(data.get("apiKeys"), APIKey.class));
    }

    /**
     * Asynchronously fetch an API key by id.
     *
     * @param id API key UUID.
     * @return future completing with the key (without the raw secret), or {@code null}.
     */
    public CompletableFuture<APIKey> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("apiKey"), APIKey.class));
    }

    /**
     * Asynchronously create an API key. The returned object exposes the raw secret exactly once.
     *
     * @param input map of fields matching {@code CreateAPIKeyInput}.
     * @return future completing with the created key plus its raw secret.
     */
    public CompletableFuture<APIKeyWithSecret> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createAPIKey"), APIKeyWithSecret.class));
    }

    /**
     * Asynchronously revoke an API key.
     *
     * @param id API key UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> revokeAsync(String id) {
        return transport.executeAsync(REVOKE, vars("id", id))
                .thenApply(data -> data.path("revokeAPIKey").asBoolean(false));
    }

    public java.util.List<APIKey> listAll(String search) {
        return paginate(after -> list(search, null, null));
    }
}
