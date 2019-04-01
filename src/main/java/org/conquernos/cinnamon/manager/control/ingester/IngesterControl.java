package org.conquernos.cinnamon.manager.control.ingester;


import akka.actor.ActorContext;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class IngesterControl {

    private static final Logger logger = LoggerFactory.getLogger(IngesterControl.class);

    private static final String CONNECTORS_PATH = "/connectors";

    private final String ingesterUrl;

    private final Http http;
    private final ExecutionContextExecutor dispatcher;
    private final Materializer materializer;

    private final HttpRequest connectorsGetRequest;
    private final HttpRequest connectorsPostRequest;

    private final Map<String, HttpRequest> connectorInquiryRequests = new HashMap<>();


    public IngesterControl(ActorContext context, String ingesterUrl) {
        http = Http.get(context.system());
        dispatcher = context.dispatcher();
        materializer = ActorMaterializer.create(context);

        this.ingesterUrl = ingesterUrl;
        connectorsGetRequest = HttpRequest.GET(ingesterUrl + CONNECTORS_PATH);
        connectorsPostRequest = HttpRequest.POST(ingesterUrl + CONNECTORS_PATH);
    }

    public Materializer getMaterializer() {
        return materializer;
    }

    public CompletionStage<HttpResponse> getConnectors() {
        return http.singleRequest(connectorsGetRequest, materializer);
    }

    public CompletionStage<HttpResponse> getConnector(String name) {
        HttpRequest request = connectorInquiryRequests.computeIfAbsent(name, k ->
            HttpRequest.GET(ingesterUrl + CONNECTORS_PATH + "/" + name));

        return http.singleRequest(request, materializer);
    }

    public CompletionStage<HttpResponse> registerConnector(ObjectNode config) {
        return http.singleRequest(connectorsPostRequest.withEntity(
            HttpEntities.create(ContentTypes.APPLICATION_JSON, config.toString())), materializer);
    }

    public CompletionStage<HttpResponse> updateConnector(String name, ObjectNode config) {
        HttpRequest request = HttpRequest.PUT(ingesterUrl + CONNECTORS_PATH + "/" + name + "/config")
            .withEntity(HttpEntities.create(ContentTypes.APPLICATION_JSON, config.toString()));

        return http.singleRequest(request, materializer);
    }

}
