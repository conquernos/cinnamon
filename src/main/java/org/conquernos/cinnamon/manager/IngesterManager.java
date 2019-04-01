package org.conquernos.cinnamon.manager;


import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.model.HttpResponse;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.ingester.IngesterControl;
import org.conquernos.cinnamon.utils.json.JsonUtils;
import org.conquernos.cinnamon.message.Message;
import org.conquernos.cinnamon.message.ingester.ConnectorMessage;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static org.conquernos.cinnamon.utils.json.JsonUtils.*;


public class IngesterManager extends Manager {

    private static final String CONNECTOR_NAME_HEAD = "hive-";
    private static final String CONNECTOR_NAME_TAIL = "-sink";

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final ActorRef master;

    private final long timeout;

    private final IngesterControl ingesterControl;

    private final MasterConfig config;


    public IngesterManager(ActorRef master, MasterConfig config) {
        this.master = master;
        this.config = config;
        this.timeout = config.getIngesterTimeout();

        this.ingesterControl = new IngesterControl(getContext(), config.getIngesterServers().get(0));

        logger.debug("IngesterManager({})", getSelf().path());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        logger.debug("IngesterManager({}) onReceive - {}", getSelf(), message);

        if (message instanceof ConnectorMessage.ConnectorsInquiryRequest) {
            inquireAllConnectors((ConnectorMessage.ConnectorsInquiryRequest) message, getSender());

        } else if (message instanceof ConnectorMessage.ConnectorInquiryRequest) {
            inquireConnector((ConnectorMessage.ConnectorInquiryRequest) message, getSender());

        } else if (message instanceof ConnectorMessage.ConnectorRegistrationRequest) {
            registerConnector((ConnectorMessage.ConnectorRegistrationRequest) message, getSender());

        } else if (message instanceof ConnectorMessage.ConnectorUpdateRequest) {
            updateConnector((ConnectorMessage.ConnectorUpdateRequest) message, getSender());

        }

    }

    private void inquireAllConnectors(ConnectorMessage.ConnectorsInquiryRequest request, ActorRef requester) {
        CompletionStage<HttpResponse> cs = ingesterControl.getConnectors();

        cs.thenAccept(httpResponse -> getBody(httpResponse)
            .thenApply(JsonUtils::toJsonNode)
            .thenAccept(body -> {
            if (body.findValue("error_code") != null) {
                requester.tell(new ConnectorMessage.ConnectorsInquiryResponse(Message.Result.FAIL, body.get("message").asText()), master);
            } else {
                requester.tell(new ConnectorMessage.ConnectorsInquiryResponse(Message.Result.SUCCESS, body), master);
            }
        }));
    }

    private void inquireConnector(ConnectorMessage.ConnectorInquiryRequest request, ActorRef requester) {
        CompletionStage<HttpResponse> cs = ingesterControl.getConnector(request.getTopic());

        cs.thenAccept(httpResponse -> getBody(httpResponse)
            .thenApply(JsonUtils::toJsonNode)
            .thenAccept(body -> {
                if (body.findValue("error_code") != null) {
                    requester.tell(new ConnectorMessage.ConnectorInquiryResponse(Message.Result.FAIL, body.get("message").asText()), master);
                } else {
                    requester.tell(new ConnectorMessage.ConnectorInquiryResponse(Message.Result.SUCCESS, body), master);
                }
            }));
    }

    private void registerConnector(ConnectorMessage.ConnectorRegistrationRequest request, ActorRef requester) {
        CompletionStage<HttpResponse> cs = ingesterControl.registerConnector(makeConnectorConfig(request));

        cs.thenAccept(httpResponse -> getBody(httpResponse)
            .thenApply(JsonUtils::toJsonNode)
            .thenAccept(body -> {
                if (body.findValue("error_code") != null) {
                    requester.tell(new ConnectorMessage.ConnectorRegistrationResponse(Message.Result.FAIL, body.get("message").asText()), master);
                } else {
                    requester.tell(new ConnectorMessage.ConnectorRegistrationResponse(Message.Result.SUCCESS), master);
                }
            }));
    }

    private void updateConnector(ConnectorMessage.ConnectorUpdateRequest request, ActorRef requester) {
        CompletionStage<HttpResponse> cs = ingesterControl.updateConnector(toConnectorName(request.getTopic()), makeConnectorConfig(request));

        cs.thenAccept(httpResponse -> getBody(httpResponse)
            .thenApply(JsonUtils::toJsonNode)
            .thenAccept(body -> {
                if (body.findValue("error_code") != null) {
                    requester.tell(new ConnectorMessage.ConnectorUpdateResponse(Message.Result.FAIL, body.get("message").asText()), master);
                } else {
                    requester.tell(new ConnectorMessage.ConnectorUpdateResponse(Message.Result.SUCCESS), master);
                }
            }));
    }

    private CompletionStage<String> getBody(HttpResponse httpResponse) {
        return httpResponse.entity()
            .toStrict(timeout, ingesterControl.getMaterializer())
            .thenCompose(strict ->
                strict.getDataBytes()
                    .runFold(ByteString.empty(), ByteString::concat, ingesterControl.getMaterializer())
                    .thenApply(ByteString::utf8String));
    }

    private ObjectNode makeConnectorConfig(ConnectorMessage.ConnectorRequest request) {
        ObjectNode configNode = createObjectNode();
        configNode.put("connector.class", config.getHdfsConnectorClass());
        configNode.put("hdfs.url", config.getHdfsConnectorHdfsUrl());
        configNode.put("hive.integration", config.getHdfsConnectorHiveIntegration());
        configNode.put("hadoop.conf.dir", config.getHdfsConnectorHadoopConfDir());
        configNode.put("logs.dir", config.getHdfsConnectorLogDir());
        configNode.put("hive.metastore.uris", config.getHdfsConnectorHiveMetastoreUris());
        configNode.put("schema.compatibility", config.getHdfsConnectorSchemaCompatibility());
        configNode.put("partitioner.class", config.getHdfsConnectorPartitionerClass());
        configNode.put("locale", config.getHdfsConnectorLocale());
        configNode.put("timezone", config.getHdfsConnectorTimezone());

        ObjectNode node = createObjectNode();
        if (request instanceof ConnectorMessage.ConnectorRegistrationRequest) {
            node.put("name", toConnectorName(((ConnectorMessage.ConnectorRegistrationRequest) request).getTopic()));

            configNode.put("tasks.max", ((ConnectorMessage.ConnectorRegistrationRequest) request).getNumberOfTasks());
            configNode.put("flush.size", ((ConnectorMessage.ConnectorRegistrationRequest) request).getFlushSize());
            configNode.put("hive.database", ((ConnectorMessage.ConnectorRegistrationRequest) request).getDatabase());
            configNode.put("topics.dir", trimSlash(((ConnectorMessage.ConnectorRegistrationRequest) request).getDirectory()));
            configNode.put("topics", ((ConnectorMessage.ConnectorRegistrationRequest) request).getTopic());
            configNode.put("partition.date.field", toListString(((ConnectorMessage.ConnectorRegistrationRequest) request).getPartitionDateFields()));
            configNode.put("partition.field.name", toListString(((ConnectorMessage.ConnectorRegistrationRequest) request).getPartitionFields()));
            configNode.put("partition.duration.ms", extractDuration(((ConnectorMessage.ConnectorRegistrationRequest) request).getPartitionDateFields()));
            configNode.put("path.format", trimSlash(((ConnectorMessage.ConnectorRegistrationRequest) request).getPartitionPath()));
        } else if (request instanceof ConnectorMessage.ConnectorUpdateRequest) {
            node.put("name", toConnectorName(((ConnectorMessage.ConnectorUpdateRequest) request).getTopic()));

            configNode.put("tasks.max", ((ConnectorMessage.ConnectorUpdateRequest) request).getNumberOfTasks());
            configNode.put("flush.size", ((ConnectorMessage.ConnectorUpdateRequest) request).getFlushSize());
            configNode.put("hive.database", ((ConnectorMessage.ConnectorUpdateRequest) request).getDatabase());
            configNode.put("topics.dir", trimSlash(((ConnectorMessage.ConnectorUpdateRequest) request).getDirectory()));
            configNode.put("topics", ((ConnectorMessage.ConnectorUpdateRequest) request).getTopic());
            configNode.put("partition.date.field", toListString(((ConnectorMessage.ConnectorUpdateRequest) request).getPartitionDateFields()));
            configNode.put("partition.field.name", toListString(((ConnectorMessage.ConnectorUpdateRequest) request).getPartitionFields()));
            configNode.put("partition.duration.ms", extractDuration(((ConnectorMessage.ConnectorUpdateRequest) request).getPartitionDateFields()));
            configNode.put("path.format", trimSlash(((ConnectorMessage.ConnectorUpdateRequest) request).getPartitionPath()));
        }
        node.set("config", configNode);

        return node;
    }

    private static String toConnectorName(String topic) {
        return CONNECTOR_NAME_HEAD + topic + CONNECTOR_NAME_TAIL;
    }

    private static String toListString(List<String> list) {
        StringBuilder listString = new StringBuilder();

        for (String str : list) {
            if (listString.length() > 0) listString.append(',');
            listString.append(str);
        }

        return listString.toString();
    }

    private static long extractDuration(List<String> dates) {
        long duration = 0;
        for (String date : dates) {
            String expression = date.substring(date.indexOf(':') + 1);
            if (expression.indexOf('S') != -1) duration = 1;
            else if (expression.indexOf('s') != -1) duration = TimeUnit.SECONDS.toMillis(1);
            else if (expression.indexOf('m') != -1) duration = TimeUnit.MINUTES.toMillis(1);
            else if (expression.indexOf('H') != -1) duration = TimeUnit.HOURS.toMillis(1);
            else if (expression.indexOf('d') != -1) duration = TimeUnit.DAYS.toMillis(1);
        }

        return duration;
    }

    private static String trimSlash(String path) {
        return path.replace("^\\*", "").replace("\\*$", "");
    }

}
