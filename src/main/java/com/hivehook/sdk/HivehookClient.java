package com.hivehook.sdk;

import com.hivehook.sdk.resources.*;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Top-level entry point for the Hivehook SDK. Build one via {@link #builder()} and access typed
 * resource services from the {@code sources()}/{@code destinations()}/... accessors.
 */
public final class HivehookClient implements AutoCloseable {
    private final SourceService sources;
    private final DestinationService destinations;
    private final SubscriptionService subscriptions;
    private final EventService events;
    private final DeliveryService deliveries;
    private final DLQService dlq;
    private final APIKeyService apiKeys;
    private final AlertRuleService alertRules;
    private final BookmarkService bookmarks;
    private final EventTypeSchemaService eventTypeSchemas;
    private final ApplicationService applications;
    private final EndpointService endpoints;
    private final MessageService messages;
    private final OutboundDeliveryService outboundDeliveries;
    private final OutboundDLQService outboundDlq;
    private final TransformationService transformations;
    private final StatusService status;
    private final PortalService portal;
    private final StreamService streams;
    private final StreamConsumerService streamConsumers;
    private final StreamSinkService streamSinks;
    private final OrganizationService organizations;
    private final UserService users;
    private final AuditLogService auditLogs;
    private final MetaEventConfigService metaEventConfigs;

    private HivehookClient(GraphQLTransport transport) {
        this.sources = new SourceService(transport);
        this.destinations = new DestinationService(transport);
        this.subscriptions = new SubscriptionService(transport);
        this.events = new EventService(transport);
        this.deliveries = new DeliveryService(transport);
        this.dlq = new DLQService(transport);
        this.apiKeys = new APIKeyService(transport);
        this.alertRules = new AlertRuleService(transport);
        this.bookmarks = new BookmarkService(transport);
        this.eventTypeSchemas = new EventTypeSchemaService(transport);
        this.applications = new ApplicationService(transport);
        this.endpoints = new EndpointService(transport);
        this.messages = new MessageService(transport);
        this.outboundDeliveries = new OutboundDeliveryService(transport);
        this.outboundDlq = new OutboundDLQService(transport);
        this.transformations = new TransformationService(transport);
        this.status = new StatusService(transport);
        this.portal = new PortalService(transport);
        this.streams = new StreamService(transport);
        this.streamConsumers = new StreamConsumerService(transport);
        this.streamSinks = new StreamSinkService(transport);
        this.organizations = new OrganizationService(transport);
        this.users = new UserService(transport);
        this.auditLogs = new AuditLogService(transport);
        this.metaEventConfigs = new MetaEventConfigService(transport);
    }

    public SourceService sources() { return sources; }
    public DestinationService destinations() { return destinations; }
    public SubscriptionService subscriptions() { return subscriptions; }
    public EventService events() { return events; }
    public DeliveryService deliveries() { return deliveries; }
    public DLQService dlq() { return dlq; }
    public APIKeyService apiKeys() { return apiKeys; }
    public AlertRuleService alertRules() { return alertRules; }
    public BookmarkService bookmarks() { return bookmarks; }
    public EventTypeSchemaService eventTypeSchemas() { return eventTypeSchemas; }
    public ApplicationService applications() { return applications; }
    public EndpointService endpoints() { return endpoints; }
    public MessageService messages() { return messages; }
    public OutboundDeliveryService outboundDeliveries() { return outboundDeliveries; }
    public OutboundDLQService outboundDlq() { return outboundDlq; }
    public TransformationService transformations() { return transformations; }
    public StatusService status() { return status; }
    public PortalService portal() { return portal; }
    public StreamService streams() { return streams; }
    public StreamConsumerService streamConsumers() { return streamConsumers; }
    public StreamSinkService streamSinks() { return streamSinks; }
    public OrganizationService organizations() { return organizations; }
    public UserService users() { return users; }
    public AuditLogService auditLogs() { return auditLogs; }
    public MetaEventConfigService metaEventConfigs() { return metaEventConfigs; }

    @Override
    public void close() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String apiKey = "";
        private String baseUrl = "http://localhost:8080";
        private HttpClient httpClient;
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration requestTimeout = Duration.ofSeconds(30);
        private int maxRetries = 2;

        public Builder apiKey(String apiKey) { this.apiKey = apiKey; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder httpClient(HttpClient httpClient) { this.httpClient = httpClient; return this; }

        /**
         * Connect timeout applied to the default {@link HttpClient}. Ignored when a custom
         * {@link #httpClient(HttpClient)} is supplied. Default: 10s.
         */
        public Builder connectTimeout(Duration connectTimeout) { this.connectTimeout = connectTimeout; return this; }

        /**
         * Per-request timeout applied to every outgoing HTTP request. Default: 30s.
         */
        public Builder requestTimeout(Duration requestTimeout) { this.requestTimeout = requestTimeout; return this; }

        /**
         * Maximum number of retries on transient failures ({@link RateLimitException},
         * {@link ServerException}, {@link java.io.IOException}). Default: 2. Set to 0 to disable.
         */
        public Builder maxRetries(int maxRetries) { this.maxRetries = maxRetries; return this; }

        public HivehookClient build() {
            GraphQLTransport transport = new GraphQLTransport(
                    baseUrl, apiKey, httpClient, connectTimeout, requestTimeout, maxRetries);
            return new HivehookClient(transport);
        }
    }
}
