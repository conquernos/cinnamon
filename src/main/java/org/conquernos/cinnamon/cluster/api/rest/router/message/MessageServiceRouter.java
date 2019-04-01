package org.conquernos.cinnamon.cluster.api.rest.router.message;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import org.conquernos.cinnamon.cluster.api.rest.router.ApiRouter;
import org.conquernos.cinnamon.manager.control.shover.Shover;
import org.conquernos.cinnamon.message.Message;
import org.conquernos.cinnamon.message.broker.BrokerMessage;
import org.conquernos.cinnamon.message.broker.BrokerMetricsMessage;
import org.conquernos.cinnamon.message.broker.TopicMessage;
import org.conquernos.cinnamon.message.ingester.ConnectorMessage;
import org.conquernos.cinnamon.message.onestop.OneStopMessage;
import org.conquernos.cinnamon.message.schema.SchemaMessage;
import org.conquernos.cinnamon.message.shover.ShoverMessage;

import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;


public class MessageServiceRouter extends ApiRouter {

    private final LoggingAdapter log;

    // command
    private static final String REGISTER_PATH = "register";
    private static final String UPDATE_PATH = "update";
    private static final String URL_PATH = "url";
    private static final String METRICS_PATH = "metrics";

    // one-stop
    private static final String ONESTOP_PATH = "onestop";

    // shover
    private static final String SHOVER_PATH = "shover";

    // CMS
    private static final String CMS_PATH = "cms";

    // CMS - component
    private static final String SCHEMA_REGISTRY_PATH = "sr";
    private static final String BROKER_PATH = "broker";

    // CMS - schema
    private static final String SCHEMA_PATH = "schema";
    private static final String SUBJECTS_PATH = "subjects";
    private static final String VERSION_PARAM = "version";

    // CMS - topic
    private static final String TOPIC_PATH = "topic";

    // ingester
    private static final String INGESTER_PATH = "ingester";

    // ingester - connector
    private static final String CONNECTOR_PATH = "connector";

    private final ActorRef master;
    private final long messageTimeout;


    public MessageServiceRouter(ActorSystem system, ActorRef master, long messageTimeout) {
        this.master = master;
        this.messageTimeout = messageTimeout;
        log = Logging.getLogger(system, this);
    }

    @Override
    public Route createRoute() {
        return createGetRoute()
            .orElse(createPostRoute())
            .orElse(createPutRoute());
    }

    private Route createGetRoute() {
        return get(() ->
            route(
                pathPrefix(SHOVER_PATH, () ->
                    route(
                        routeShoverInquiry()
                    )
                )
                , pathPrefix(CMS_PATH, () ->
                    route(
                        pathPrefix(SCHEMA_REGISTRY_PATH, this::routeSchemaRegistry)
                        , pathPrefix(SCHEMA_PATH, this::routeSchemaInquiry)
                        , pathPrefix(BROKER_PATH, this::routeBroker)
                        , pathPrefix(TOPIC_PATH, this::routeTopicsInquiry)
                    )
                )
                , pathPrefix(INGESTER_PATH, () ->
                    route(
                        pathPrefix(CONNECTOR_PATH, this::routeConnectorInquiry)
                    )
                )
            )
        );
    }

    private Route createPostRoute() {
        return post(() ->
            route(
                pathPrefix(SHOVER_PATH, () ->
                    route(
                        routeShoverRegistration()
                    )
                )
                , pathPrefix(CMS_PATH, () ->
                    route(
                        pathPrefix(SCHEMA_PATH, () ->
                            route(
                                routeSchemaRegistration()
                            )
                        ),
                        pathPrefix(TOPIC_PATH, () ->
                            route(
                                routeTopicRegistration()
                            )
                        )
                    )
                )
                , pathPrefix(INGESTER_PATH, () ->
                    route(
                        pathPrefix(CONNECTOR_PATH, () ->
                            route(
                                routeConnectorRegistration()
                            )
                        )
                    )
                )
                , pathPrefix(ONESTOP_PATH, () ->
                    route(
                        routeOneStopRegistration()
                    )
                )
            )
        );
    }

    private Route createPutRoute() {
        return put(() ->
            route(
                pathPrefix(CMS_PATH, () ->
                    route(
                        pathPrefix(SCHEMA_PATH, () ->
                            route(
                                routeSchemaUpdate()
                            )
                        )
                    )
                )
                , pathPrefix(INGESTER_PATH, () ->
                    route(
                        pathPrefix(CONNECTOR_PATH, () ->
                            route(
                                routeConnectorUpdate()
                            )
                        )
                    )
                )
            )
        );
    }

    // shover (get)
    private Route routeShoverInquiry() {
        return path(shover -> {
            if (shover.equalsIgnoreCase("all")) {
                CompletionStage<ShoverMessage.ShoversInquiryResponse> shovers = PatternsCS.ask(master, new ShoverMessage.ShoversInquiryRequest(), messageTimeout)
                    .thenApply(ShoverMessage.ShoversInquiryResponse.class::cast);

                return completeOKWithFuture(shovers, Jackson.marshaller());
            } else {
                CompletionStage<ShoverMessage.ShoverInquiryResponse> result = PatternsCS.ask(master, new ShoverMessage.ShoverInquiryRequest(shover), messageTimeout)
                    .thenApply(ShoverMessage.ShoverInquiryResponse.class::cast);

                return completeOKWithFuture(result, Jackson.marshaller());
            }
        });
    }

    // shover (register)
    private Route routeShoverRegistration() {
        return pathPrefix(REGISTER_PATH, () ->
            extractClientIP(ip ->
                entity(Jackson.unmarshaller(Shover.class), shover -> {
                    Optional<InetAddress> address = ip.getAddress();
                    shover.setAddress(address.isPresent() ? address.get().getHostAddress() : "unknown");
                    CompletionStage<ShoverMessage.ShoverRegistrationResponse> result = PatternsCS.ask(master, new ShoverMessage.ShoverRegistrationRequest(shover), messageTimeout)
                        .thenApply(ShoverMessage.ShoverRegistrationResponse.class::cast);

                    return completeOKWithFuture(result, Jackson.marshaller());
                })
            )
        );
    }

    // schema-registry (get)
    private Route routeSchemaRegistry() {
        return route(
            pathPrefix(URL_PATH, () -> {
                CompletionStage<SchemaMessage.SchemaRegistryUrlResponse> urls = PatternsCS.ask(master, new SchemaMessage.SchemaRegistryUrlRequest(), messageTimeout)
                    .thenApply(SchemaMessage.SchemaRegistryUrlResponse.class::cast);

                return completeOKWithFuture(urls, Jackson.marshaller());
            })
        );
    }

    // schema-registry -> schema (get)
    private Route routeSchemaInquiry() {
        return pathPrefix(SUBJECTS_PATH, () -> {
            CompletionStage<SchemaMessage.SubjectsInquiryResponse> subjects = PatternsCS.ask(master, new SchemaMessage.SubjectsInquiryRequest(), messageTimeout)
                .thenApply(SchemaMessage.SubjectsInquiryResponse.class::cast);

            return completeOKWithFuture(subjects, Jackson.marshaller());
        }).orElse(
            path(subject ->
                parameter(VERSION_PARAM, (versionParam) -> {
                    int version = Integer.parseInt(versionParam);

                    CompletionStage<?> schema = PatternsCS.ask(master, new SchemaMessage.SchemaInquiryRequest(subject, version), messageTimeout)
                        .thenApply(version == 0 ? SchemaMessage.SchemaAllVersionInquiryResponse.class::cast : SchemaMessage.SchemaInquiryResponse.class::cast);

                    return completeOKWithFuture(schema, Jackson.marshaller());
                })
            )
        );
    }

    // schema-registry -> schema (register)
    private Route routeSchemaRegistration() {
        return pathPrefix(REGISTER_PATH, () ->
            entity(Jackson.unmarshaller(SchemaMessage.SchemaRegistrationRequest.class), request -> {
                CompletionStage<?> result = ask(master, request, messageTimeout)
                    .thenApply(SchemaMessage.SchemaRegistrationResponse.class::cast);

                return completeOKWithFuture(result, Jackson.marshaller());
            })
        );
    }

    // schema-registry -> schema (update)
    private Route routeSchemaUpdate() {
        return pathPrefix(UPDATE_PATH, () ->
            path(topic ->
                entity(Jackson.unmarshaller(SchemaMessage.SchemaUpdateRequest.class), request -> {
                    CompletionStage<?> result = ask(master, request, messageTimeout)
                        .thenApply(SchemaMessage.SchemaUpdateResponse.class::cast);

                    return completeOKWithFuture(result, Jackson.marshaller());
                })
            )
        );
    }

    // broker -> url, metrics (get)
    private Route routeBroker() {
        return route(
            pathPrefix(URL_PATH, () -> {
                CompletionStage<BrokerMessage.BrokerUrlResponse> urls = PatternsCS.ask(master, new BrokerMessage.BrokerUrlRequest(), messageTimeout)
                    .thenApply(BrokerMessage.BrokerUrlResponse.class::cast);

                return completeOKWithFuture(urls, Jackson.marshaller());
            })
            , pathPrefix(METRICS_PATH, () -> {
                CompletionStage<BrokerMetricsMessage.BrokerResourceMetricsResponse> metrics = PatternsCS.ask(master, new BrokerMetricsMessage.BrokerResourceMetricsRequest(), messageTimeout)
                    .thenApply(BrokerMetricsMessage.BrokerResourceMetricsResponse.class::cast);

                return completeOKWithFuture(metrics, Jackson.marshaller());
            })
        );
    }

    // broker -> topic (get)
    private Route routeTopicsInquiry() {
        return path(topic -> {
            if (topic.equalsIgnoreCase("all")) {
                CompletionStage<TopicMessage.TopicsInquiryResponse> topics = PatternsCS.ask(master, new TopicMessage.TopicsInquiryRequest(), messageTimeout)
                    .thenApply(TopicMessage.TopicsInquiryResponse.class::cast);

                return completeOKWithFuture(topics, Jackson.marshaller());
            } else {
                CompletionStage<TopicMessage.TopicInquiryResponse> result = PatternsCS.ask(master, new TopicMessage.TopicInquiryRequest(topic), messageTimeout)
                    .thenApply(TopicMessage.TopicInquiryResponse.class::cast);

                return completeOKWithFuture(result, Jackson.marshaller());
            }
        });
    }

    // broker -> topic (register)
    private Route routeTopicRegistration() {
        return pathPrefix(REGISTER_PATH, () ->
            entity(Jackson.unmarshaller(TopicMessage.TopicRegistrationRequest.class), request -> {
                CompletionStage<TopicMessage.TopicRegistrationResponse> result = ask(master, request, messageTimeout)
                    .thenApply(TopicMessage.TopicRegistrationResponse.class::cast);

                return completeOKWithFuture(result, Jackson.marshaller());
            })
        );
    }

    // ingester -> connector (get)
    private Route routeConnectorInquiry() {
        return path(connectorName -> {
            if (connectorName.equalsIgnoreCase("all")) {
                CompletionStage<ConnectorMessage.ConnectorsInquiryResponse> connectors = PatternsCS.ask(master, new ConnectorMessage.ConnectorsInquiryRequest(), messageTimeout)
                    .thenApply(ConnectorMessage.ConnectorsInquiryResponse.class::cast);

                return completeOKWithFuture(connectors, Jackson.marshaller());
            } else {
                CompletionStage<ConnectorMessage.ConnectorInquiryResponse> connector = PatternsCS.ask(master, new ConnectorMessage.ConnectorInquiryRequest(connectorName), messageTimeout)
                    .thenApply(ConnectorMessage.ConnectorInquiryResponse.class::cast);

                return completeOKWithFuture(connector, Jackson.marshaller());
            }
        });
    }

    // ingester -> connector (register)
    private Route routeConnectorRegistration() {
        return pathPrefix(REGISTER_PATH, () ->
            entity(Jackson.unmarshaller(ConnectorMessage.ConnectorRegistrationRequest.class), request -> {
                CompletionStage<ConnectorMessage.ConnectorRegistrationResponse> result = ask(master, request, messageTimeout)
                    .thenApply(ConnectorMessage.ConnectorRegistrationResponse.class::cast);

                return completeOKWithFuture(result, Jackson.marshaller());
            })
        );
    }

    // ingester -> connector (update)
    private Route routeConnectorUpdate() {
        return pathPrefix(UPDATE_PATH, () ->
            entity(Jackson.unmarshaller(ConnectorMessage.ConnectorUpdateRequest.class), request -> {
                CompletionStage<ConnectorMessage.ConnectorUpdateResponse> result = ask(master, request, messageTimeout)
                    .thenApply(ConnectorMessage.ConnectorUpdateResponse.class::cast);

                return completeOKWithFuture(result, Jackson.marshaller());
            })
        );
    }

    // one-stop (register)
    private Route routeOneStopRegistration() {
        return pathPrefix(REGISTER_PATH, () ->
            entity(Jackson.unmarshaller(OneStopMessage.OneStopRegistrationRequest.class), request -> {
                CompletionStage<SchemaMessage.SchemaRegistrationResponse> schemaResult = PatternsCS.ask(master, request.toSchemaRegistrationRequest(), messageTimeout)
                    .thenApply(SchemaMessage.SchemaRegistrationResponse.class::cast);

                CompletionStage<TopicMessage.TopicRegistrationResponse> topicResult = PatternsCS.ask(master, request.toTopicRegistrationRequest(), messageTimeout)
                    .thenApply(TopicMessage.TopicRegistrationResponse.class::cast);

                CompletionStage<ConnectorMessage.ConnectorRegistrationResponse> connectorResult = PatternsCS.ask(master, request.toConnectorRegistrationRequest(), messageTimeout)
                    .thenApply(ConnectorMessage.ConnectorRegistrationResponse.class::cast);

                CompletableFuture<OneStopMessage.OneStopRegistrationResponse> oneStopResult = CompletableFuture.supplyAsync(() -> {
                    Message.Result[] results = new Message.Result[3];
                    String[] reasons = new String[3];

                    schemaResult.thenAccept(response -> {
                        results[0] = response.getResult();
                        reasons[0] = response.getReason();
                    });

                    topicResult.thenAccept(response -> {
                        results[1] = response.getResult();
                        reasons[1] = response.getReason();
                    });

                    connectorResult.thenAccept(response -> {
                        results[2] = response.getResult();
                        reasons[2] = response.getReason();
                    });

                    CompletableFuture.allOf(schemaResult.toCompletableFuture(), topicResult.toCompletableFuture(), connectorResult.toCompletableFuture()).join();

                    return new OneStopMessage.OneStopRegistrationResponse(results, reasons);
                });

                return completeOKWithFuture(oneStopResult, Jackson.marshaller());
            })
        );
    }

}
