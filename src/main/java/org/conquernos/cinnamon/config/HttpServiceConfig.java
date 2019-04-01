package org.conquernos.cinnamon.config;


import com.typesafe.config.Config;


public class HttpServiceConfig extends CinnamonConfig {

    private static final String MESSAGE_TIMEOUT = "akka.message.timeout";

    private static final String API_SERVICE_HTTP_HOST_CONF = "api.service.http.host"; // api service ip
    private static final String API_SERVICE_HTTP_PORT_CONF = "api.service.http.port"; // api service port

    private final long messageTimeout;

    private final String httpHost;
    private final int httpPort;


    public HttpServiceConfig(Config config) {
        super(config);

        this.messageTimeout = getIntegerFromConfig(config, MESSAGE_TIMEOUT, 60000);
        this.httpHost = getStringFromConfig(config, API_SERVICE_HTTP_HOST_CONF, "localhost");
        this.httpPort = getIntegerFromConfig(config, API_SERVICE_HTTP_PORT_CONF, 8080);
    }

    public long getMessageTimeout() {
        return messageTimeout;
    }

    public String getHttpHost() {
        return httpHost;
    }

    public int getHttpPort() {
        return httpPort;
    }

}
