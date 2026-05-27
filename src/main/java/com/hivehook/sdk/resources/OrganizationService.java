package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.Organization;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link Organization}s.
 */
public final class OrganizationService extends BaseService {
    private static final String FIELDS = "id name slug ssoEnabled ssoProvider retentionEvents retentionMessages otlpConfig { endpoint headers insecure sampleRate } createdAt updatedAt";
    private static final String LIST = "query($search: String, $limit: Int, $offset: Int) { organizations(search: $search, limit: $limit, offset: $offset) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { organization(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateOrganizationInput!) { createOrganization(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateOrganizationInput!) { updateOrganization(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteOrganization(id: $id) }";
    private static final String CONFIGURE_SSO = "mutation($organizationId: UUID!, $input: SSOConfigInput!) { configureSSO(organizationId: $organizationId, input: $input) { " + FIELDS + " } }";
    private static final String DISABLE_SSO = "mutation($organizationId: UUID!) { disableSSO(organizationId: $organizationId) { " + FIELDS + " } }";
    private static final String UPDATE_RETENTION = "mutation($organizationId: UUID!, $input: RetentionInput!) { updateOrganizationRetention(organizationId: $organizationId, input: $input) { " + FIELDS + " } }";
    private static final String DELETE_DATA = "mutation($organizationId: UUID!) { deleteOrganizationData(organizationId: $organizationId) }";
    private static final String EXPORT_DATA = "mutation($organizationId: UUID!) { exportOrganizationData(organizationId: $organizationId) }";
    private static final String CONFIGURE_OTLP = "mutation($organizationId: UUID!, $input: OTLPConfigInput!) { configureOTLP(organizationId: $organizationId, input: $input) { " + FIELDS + " } }";
    private static final String DISABLE_OTLP = "mutation($organizationId: UUID!) { disableOTLP(organizationId: $organizationId) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public OrganizationService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List organizations.
     *
     * @param search optional substring search.
     * @param limit  page size.
     * @param offset page offset.
     * @return paginated result.
     */
    public ListResult<Organization> list(String search, Integer limit, Integer offset) {
        JsonNode data = transport.execute(LIST, vars("search", search, "limit", limit, "offset", offset));
        return parseList(data.get("organizations"), Organization.class);
    }

    /**
     * Fetch an organization by id.
     *
     * @param id organization UUID.
     * @return the organization, or {@code null}.
     */
    public Organization get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("organization"), Organization.class);
    }

    /**
     * Create an organization.
     *
     * @param input map matching {@code CreateOrganizationInput}.
     * @return the created organization.
     */
    public Organization create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createOrganization"), Organization.class);
    }

    /**
     * Update an organization.
     *
     * @param id    organization UUID.
     * @param input map of fields to update.
     * @return the updated organization.
     */
    public Organization update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateOrganization"), Organization.class);
    }

    /**
     * Delete an organization.
     *
     * @param id organization UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteOrganization").asBoolean(false);
    }

    /**
     * Configure SSO for an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code SSOConfigInput}.
     * @return the updated organization.
     */
    public Organization configureSSO(String organizationId, Map<String, Object> input) {
        JsonNode data = transport.execute(CONFIGURE_SSO, vars("organizationId", organizationId, "input", input));
        return toType(data.get("configureSSO"), Organization.class);
    }

    /**
     * Disable SSO for an organization.
     *
     * @param organizationId organization UUID.
     * @return the updated organization.
     */
    public Organization disableSSO(String organizationId) {
        JsonNode data = transport.execute(DISABLE_SSO, vars("organizationId", organizationId));
        return toType(data.get("disableSSO"), Organization.class);
    }

    /**
     * Update retention settings for an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code RetentionInput}.
     * @return the updated organization.
     */
    public Organization updateRetention(String organizationId, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE_RETENTION, vars("organizationId", organizationId, "input", input));
        return toType(data.get("updateOrganizationRetention"), Organization.class);
    }

    /**
     * Delete all data belonging to an organization.
     *
     * @param organizationId organization UUID.
     * @return {@code true} on success.
     */
    public boolean deleteData(String organizationId) {
        JsonNode data = transport.execute(DELETE_DATA, vars("organizationId", organizationId));
        return data.path("deleteOrganizationData").asBoolean(false);
    }

    /**
     * Trigger a full data export for an organization.
     *
     * @param organizationId organization UUID.
     * @return raw export metadata map (e.g. download URL); {@code null} when the server omits it.
     */
    public Map<String, Object> exportData(String organizationId) {
        JsonNode data = transport.execute(EXPORT_DATA, vars("organizationId", organizationId));
        JsonNode result = data.get("exportOrganizationData");
        if (result == null || result.isNull() || !result.isObject()) {
            return null;
        }
        return mapper.convertValue(result, MAP_TYPE);
    }

    /**
     * Configure the OTLP exporter for an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code OTLPConfigInput}.
     * @return the updated organization.
     */
    public Organization configureOTLP(String organizationId, Map<String, Object> input) {
        JsonNode data = transport.execute(CONFIGURE_OTLP, vars("organizationId", organizationId, "input", input));
        return toType(data.get("configureOTLP"), Organization.class);
    }

    /**
     * Disable the OTLP exporter for an organization.
     *
     * @param organizationId organization UUID.
     * @return the updated organization.
     */
    public Organization disableOTLP(String organizationId) {
        JsonNode data = transport.execute(DISABLE_OTLP, vars("organizationId", organizationId));
        return toType(data.get("disableOTLP"), Organization.class);
    }

    /**
     * Asynchronously list organizations.
     *
     * @param search optional substring search.
     * @param limit  page size.
     * @param offset page offset.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Organization>> listAsync(String search, Integer limit, Integer offset) {
        return transport.executeAsync(LIST, vars("search", search, "limit", limit, "offset", offset))
                .thenApply(data -> parseList(data.get("organizations"), Organization.class));
    }

    /**
     * Asynchronously fetch an organization by id.
     *
     * @param id organization UUID.
     * @return future completing with the organization, or {@code null}.
     */
    public CompletableFuture<Organization> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("organization"), Organization.class));
    }

    /**
     * Asynchronously create an organization.
     *
     * @param input map matching {@code CreateOrganizationInput}.
     * @return future completing with the created organization.
     */
    public CompletableFuture<Organization> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createOrganization"), Organization.class));
    }

    /**
     * Asynchronously update an organization.
     *
     * @param id    organization UUID.
     * @param input map of fields to update.
     * @return future completing with the updated organization.
     */
    public CompletableFuture<Organization> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateOrganization"), Organization.class));
    }

    /**
     * Asynchronously delete an organization.
     *
     * @param id organization UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteOrganization").asBoolean(false));
    }

    /**
     * Asynchronously configure SSO for an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code SSOConfigInput}.
     * @return future completing with the updated organization.
     */
    public CompletableFuture<Organization> configureSSOAsync(String organizationId, Map<String, Object> input) {
        return transport.executeAsync(CONFIGURE_SSO, vars("organizationId", organizationId, "input", input))
                .thenApply(data -> toType(data.get("configureSSO"), Organization.class));
    }

    /**
     * Asynchronously disable SSO for an organization.
     *
     * @param organizationId organization UUID.
     * @return future completing with the updated organization.
     */
    public CompletableFuture<Organization> disableSSOAsync(String organizationId) {
        return transport.executeAsync(DISABLE_SSO, vars("organizationId", organizationId))
                .thenApply(data -> toType(data.get("disableSSO"), Organization.class));
    }

    /**
     * Asynchronously update retention settings for an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code RetentionInput}.
     * @return future completing with the updated organization.
     */
    public CompletableFuture<Organization> updateRetentionAsync(String organizationId, Map<String, Object> input) {
        return transport.executeAsync(UPDATE_RETENTION, vars("organizationId", organizationId, "input", input))
                .thenApply(data -> toType(data.get("updateOrganizationRetention"), Organization.class));
    }

    /**
     * Asynchronously delete all data belonging to an organization.
     *
     * @param organizationId organization UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteDataAsync(String organizationId) {
        return transport.executeAsync(DELETE_DATA, vars("organizationId", organizationId))
                .thenApply(data -> data.path("deleteOrganizationData").asBoolean(false));
    }

    /**
     * Asynchronously trigger a full data export for an organization.
     *
     * @param organizationId organization UUID.
     * @return future completing with the raw export metadata map (e.g. download URL); {@code null} when the server omits it.
     */
    public CompletableFuture<Map<String, Object>> exportDataAsync(String organizationId) {
        return transport.executeAsync(EXPORT_DATA, vars("organizationId", organizationId))
                .thenApply(data -> {
                    JsonNode result = data.get("exportOrganizationData");
                    if (result == null || result.isNull() || !result.isObject()) {
                        return null;
                    }
                    return mapper.convertValue(result, MAP_TYPE);
                });
    }

    /**
     * Asynchronously configure the OTLP exporter for an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code OTLPConfigInput}.
     * @return future completing with the updated organization.
     */
    public CompletableFuture<Organization> configureOTLPAsync(String organizationId, Map<String, Object> input) {
        return transport.executeAsync(CONFIGURE_OTLP, vars("organizationId", organizationId, "input", input))
                .thenApply(data -> toType(data.get("configureOTLP"), Organization.class));
    }

    /**
     * Asynchronously disable the OTLP exporter for an organization.
     *
     * @param organizationId organization UUID.
     * @return future completing with the updated organization.
     */
    public CompletableFuture<Organization> disableOTLPAsync(String organizationId) {
        return transport.executeAsync(DISABLE_OTLP, vars("organizationId", organizationId))
                .thenApply(data -> toType(data.get("disableOTLP"), Organization.class));
    }

    public java.util.List<Organization> listAll(String search) {
        return paginate(after -> list(search, null, null));
    }
}
