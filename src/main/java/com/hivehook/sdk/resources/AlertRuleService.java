package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.AlertRule;
import com.hivehook.sdk.types.ListResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for {@link AlertRule}s.
 */
public final class AlertRuleService extends BaseService {
    private static final String FIELDS = "id name conditionType threshold webhookUrl channel emailConfig { to subjectTemplate } slackConfig { webhookUrl channel } cooldown enabled createdAt";
    private static final String LIST = "query($enabled: Boolean, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { alertRules(enabled: $enabled, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { alertRule(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateAlertRuleInput!) { createAlertRule(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateAlertRuleInput!) { updateAlertRule(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteAlertRule(id: $id) }";
    private static final String TEST = "mutation($id: UUID!) { testAlertRule(id: $id) }";

    /** @param transport transport to use. */
    public AlertRuleService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List alert rules.
     *
     * @param enabled optional enabled filter.
     * @param search  optional substring search.
     * @param limit   page size (offset pagination).
     * @param offset  page offset (offset pagination).
     * @param after   cursor for cursor pagination.
     * @param first   page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<AlertRule> list(Boolean enabled, String search,
                                      Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "enabled", enabled, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("alertRules"), AlertRule.class);
    }

    /**
     * Fetch an alert rule by id.
     *
     * @param id rule UUID.
     * @return the rule, or {@code null}.
     */
    public AlertRule get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("alertRule"), AlertRule.class);
    }

    /**
     * Create an alert rule.
     *
     * @param input map matching {@code CreateAlertRuleInput}.
     * @return the created rule.
     */
    public AlertRule create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createAlertRule"), AlertRule.class);
    }

    /**
     * Update an alert rule.
     *
     * @param id    rule UUID.
     * @param input map of fields to update.
     * @return the updated rule.
     */
    public AlertRule update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateAlertRule"), AlertRule.class);
    }

    /**
     * Delete an alert rule.
     *
     * @param id rule UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteAlertRule").asBoolean(false);
    }

    /**
     * Fire a one-off test notification for a rule.
     *
     * @param id rule UUID.
     * @return {@code true} when the notification was sent.
     */
    public boolean test(String id) {
        JsonNode data = transport.execute(TEST, vars("id", id));
        return data.path("testAlertRule").asBoolean(false);
    }

    /**
     * Asynchronously list alert rules.
     *
     * @param enabled optional enabled filter.
     * @param search  optional substring search.
     * @param limit   page size (offset pagination).
     * @param offset  page offset (offset pagination).
     * @param after   cursor for cursor pagination.
     * @param first   page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<AlertRule>> listAsync(Boolean enabled, String search,
                                                              Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "enabled", enabled, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("alertRules"), AlertRule.class));
    }

    /**
     * Asynchronously fetch an alert rule by id.
     *
     * @param id rule UUID.
     * @return future completing with the rule, or {@code null}.
     */
    public CompletableFuture<AlertRule> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("alertRule"), AlertRule.class));
    }

    /**
     * Asynchronously create an alert rule.
     *
     * @param input map matching {@code CreateAlertRuleInput}.
     * @return future completing with the created rule.
     */
    public CompletableFuture<AlertRule> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createAlertRule"), AlertRule.class));
    }

    /**
     * Asynchronously update an alert rule.
     *
     * @param id    rule UUID.
     * @param input map of fields to update.
     * @return future completing with the updated rule.
     */
    public CompletableFuture<AlertRule> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateAlertRule"), AlertRule.class));
    }

    /**
     * Asynchronously delete an alert rule.
     *
     * @param id rule UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteAlertRule").asBoolean(false));
    }

    /**
     * Asynchronously fire a one-off test notification for a rule.
     *
     * @param id rule UUID.
     * @return future completing with {@code true} when the notification was sent.
     */
    public CompletableFuture<Boolean> testAsync(String id) {
        return transport.executeAsync(TEST, vars("id", id))
                .thenApply(data -> data.path("testAlertRule").asBoolean(false));
    }

    public java.util.List<AlertRule> listAll(Boolean enabled, String search) {
        return paginate(after -> list(enabled, search, null, null, after, null));
    }
}
