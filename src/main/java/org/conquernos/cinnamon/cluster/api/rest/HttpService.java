package org.conquernos.cinnamon.cluster.api.rest;


import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.conquernos.cinnamon.cluster.api.rest.router.message.MessageServiceRouter;
import org.conquernos.cinnamon.config.HttpServiceConfig;
import org.conquernos.cinnamon.utils.config.ConfigLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;


public class HttpService extends AllDirectives {

    private final LoggingAdapter log;

    private final ActorSystem system;
    private final ActorRef master;
    private final HttpServiceConfig config;
    private final Http http;
    private final ActorMaterializer materializer;

    private CompletionStage<ServerBinding> binding = null;


    public HttpService(ActorSystem system, ActorRef master, HttpServiceConfig config) {
        this.log = Logging.getLogger(system, this);
        this.system = system;
        this.master = master;
        this.config = config;
        this.http = Http.get(system);
        this.materializer = ActorMaterializer.create(system);
    }

    public void open() {
        Route route = new MessageServiceRouter(system, master, config.getMessageTimeout()).createRoute();
        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.flow(system, materializer);

        binding = http.bindAndHandle(routeFlow
            , ConnectHttp.toHost(config.getHttpHost(), config.getHttpPort())
            , materializer);

        binding.exceptionally(failure -> {
            log.error("HTTP service binding failure : {}", failure.getMessage());
            system.terminate();
            return null;
        });
    }

    public void close() {
        if (binding != null) {
            binding.thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
        }
    }

    public static String extractIP(HttpRequest request) {
        List<Optional<HttpHeader>> headers = new ArrayList<>(8);
        headers.add(request.getHeader("REMOTE_ADDR"));
        headers.add(request.getHeader("HTTP_X_FORWARDED_FOR"));
        headers.add(request.getHeader("HTTP_CLIENT_IP"));
        headers.add(request.getHeader("HTTP_X_FORWARDED"));
        headers.add(request.getHeader("HTTP_X_CLUSTER_CLIENT_IP"));
        headers.add(request.getHeader("HTTP_FORWARDED_FOR"));
        headers.add(request.getHeader("HTTP_FORWARDED"));
        headers.add(request.getHeader("HTTP_VIA"));

        for (Optional<HttpHeader> header : headers) {
            if (header.isPresent()) {
                String ip = header.get().value().trim();
                if (ip.length() > 0) return ip;
            }
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        HttpServiceConfig httpServiceConfig = (HttpServiceConfig) ConfigLoader.build(HttpServiceConfig.class, "/Users/haimjoon/IdeaProjects/cinnamon/conf/master.conf");
        HttpService service = new HttpService(ActorSystem.create(), ActorRef.noSender(), httpServiceConfig);
        service.open();

//		System.out.println("Type RETURN to exit");
//		System.in.read();
    }

}
