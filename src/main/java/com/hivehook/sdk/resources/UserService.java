package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.User;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for {@link User}s.
 */
public final class UserService extends BaseService {
    private static final String FIELDS = "id organizationId email name role lastLoginAt createdAt updatedAt";
    private static final String LIST = "query($organizationId: UUID, $search: String, $limit: Int, $offset: Int) { users(organizationId: $organizationId, search: $search, limit: $limit, offset: $offset) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String ME = "query { me { " + FIELDS + " } }";
    private static final String INVITE = "mutation($organizationId: UUID!, $input: InviteUserInput!) { inviteUser(organizationId: $organizationId, input: $input) { " + FIELDS + " } }";
    private static final String REMOVE = "mutation($id: UUID!) { removeUser(id: $id) }";
    private static final String UPDATE_ROLE = "mutation($id: UUID!, $input: UpdateUserRoleInput!) { updateUserRole(id: $id, input: $input) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public UserService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List users.
     *
     * @param organizationId optional organization UUID filter.
     * @param search         optional substring search.
     * @param limit          page size.
     * @param offset         page offset.
     * @return paginated result.
     */
    public ListResult<User> list(String organizationId, String search, Integer limit, Integer offset) {
        JsonNode data = transport.execute(LIST, vars(
                "organizationId", organizationId, "search", search, "limit", limit, "offset", offset));
        return parseList(data.get("users"), User.class);
    }

    /**
     * Asynchronously list users.
     *
     * @param organizationId optional organization UUID filter.
     * @param search         optional substring search.
     * @param limit          page size.
     * @param offset         page offset.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<User>> listAsync(String organizationId, String search, Integer limit, Integer offset) {
        return transport.executeAsync(LIST, vars(
                "organizationId", organizationId, "search", search, "limit", limit, "offset", offset))
                .thenApply(data -> parseList(data.get("users"), User.class));
    }

    /**
     * Return the currently authenticated user.
     *
     * @return the calling user.
     */
    public User me() {
        JsonNode data = transport.execute(ME, null);
        return toType(data.get("me"), User.class);
    }

    /**
     * Asynchronously return the currently authenticated user.
     *
     * @return future completing with the calling user.
     */
    public CompletableFuture<User> meAsync() {
        return transport.executeAsync(ME, null)
                .thenApply(data -> toType(data.get("me"), User.class));
    }

    /**
     * Invite a user to an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code InviteUserInput}.
     * @return the invited user.
     */
    public User invite(String organizationId, Map<String, Object> input) {
        JsonNode data = transport.execute(INVITE, vars("organizationId", organizationId, "input", input));
        return toType(data.get("inviteUser"), User.class);
    }

    /**
     * Asynchronously invite a user to an organization.
     *
     * @param organizationId organization UUID.
     * @param input          map matching {@code InviteUserInput}.
     * @return future completing with the invited user.
     */
    public CompletableFuture<User> inviteAsync(String organizationId, Map<String, Object> input) {
        return transport.executeAsync(INVITE, vars("organizationId", organizationId, "input", input))
                .thenApply(data -> toType(data.get("inviteUser"), User.class));
    }

    /**
     * Remove a user.
     *
     * @param id user UUID.
     * @return {@code true} on success.
     */
    public boolean remove(String id) {
        JsonNode data = transport.execute(REMOVE, vars("id", id));
        return data.path("removeUser").asBoolean(false);
    }

    /**
     * Asynchronously remove a user.
     *
     * @param id user UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> removeAsync(String id) {
        return transport.executeAsync(REMOVE, vars("id", id))
                .thenApply(data -> data.path("removeUser").asBoolean(false));
    }

    /**
     * Update a user's role.
     *
     * @param id    user UUID.
     * @param input map matching {@code UpdateUserRoleInput}.
     * @return the updated user.
     */
    public User updateRole(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE_ROLE, vars("id", id, "input", input));
        return toType(data.get("updateUserRole"), User.class);
    }

    /**
     * Asynchronously update a user's role.
     *
     * @param id    user UUID.
     * @param input map matching {@code UpdateUserRoleInput}.
     * @return future completing with the updated user.
     */
    public CompletableFuture<User> updateRoleAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE_ROLE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateUserRole"), User.class));
    }

    public java.util.List<User> listAll(String organizationId, String search) {
        return paginate(after -> list(organizationId, search, null, null));
    }
}
