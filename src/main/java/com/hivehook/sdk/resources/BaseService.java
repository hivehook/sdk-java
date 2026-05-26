package com.hivehook.sdk.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.HivehookException;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.PageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for resource services. Holds the shared transport and provides Jackson-aware helpers
 * that convert {@link JsonNode} subtrees into typed records.
 */
public abstract class BaseService {
    /** Transport used to issue GraphQL requests. */
    protected final GraphQLTransport transport;

    /** Shared Jackson mapper, cached for fast access. */
    protected final ObjectMapper mapper;

    /**
     * @param transport transport to use for this service.
     */
    protected BaseService(GraphQLTransport transport) {
        this.transport = transport;
        this.mapper = transport.getMapper();
    }

    /**
     * Build a variables map from alternating key/value pairs, dropping {@code null} values so that
     * optional GraphQL arguments are omitted from the request rather than sent as JSON {@code null}.
     *
     * @param keyValues alternating {@code String} keys and arbitrary values.
     * @return mutable, insertion-ordered map of non-null variables.
     */
    protected Map<String, Object> vars(Object... keyValues) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            Object val = keyValues[i + 1];
            if (val != null) {
                m.put((String) keyValues[i], val);
            }
        }
        return m;
    }

    /**
     * Convert a {@link JsonNode} into a record of the given type using the shared mapper.
     *
     * @param node node to convert; may be {@code null} or a missing/null node.
     * @param type target record class.
     * @param <T>  target type.
     * @return parsed record, or {@code null} if {@code node} is missing or JSON null.
     */
    protected <T> T toType(JsonNode node, Class<T> type) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        try {
            return mapper.treeToValue(node, type);
        } catch (Exception e) {
            throw new HivehookException("failed to parse " + type.getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Parse a GraphQL paginated connection node into a typed {@link ListResult}.
     *
     * @param connection node containing {@code nodes} and {@code pageInfo} fields.
     * @param nodeType   target element class.
     * @param <T>        element type.
     * @return list result with hydrated nodes and parsed page info.
     */
    protected <T> ListResult<T> parseList(JsonNode connection, Class<T> nodeType) {
        if (connection == null || connection.isMissingNode() || connection.isNull()) {
            return new ListResult<>(Collections.emptyList(), new PageInfo(0, 0, 0, null, false));
        }
        JsonNode nodesNode = connection.path("nodes");
        List<T> nodes = new ArrayList<>();
        if (nodesNode.isArray()) {
            for (JsonNode n : nodesNode) {
                T item = toType(n, nodeType);
                if (item != null) {
                    nodes.add(item);
                }
            }
        }
        PageInfo pi = toType(connection.get("pageInfo"), PageInfo.class);
        if (pi == null) {
            pi = new PageInfo(0, 0, 0, null, false);
        }
        return new ListResult<>(nodes, pi);
    }

    /**
     * {@link TypeReference} for a generic {@code Map<String, Object>}.
     */
    protected static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
}
