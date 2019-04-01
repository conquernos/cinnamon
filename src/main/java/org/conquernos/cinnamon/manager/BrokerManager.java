package org.conquernos.cinnamon.manager;


import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.broker.BrokerControl;
import org.conquernos.cinnamon.manager.control.broker.Topic;
import org.conquernos.cinnamon.manager.control.broker.TopicControl;
import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import org.conquernos.cinnamon.exception.kafka.CinnamonKafkaException;
import org.conquernos.cinnamon.exception.schema.CinnamonSchemaException;
import org.conquernos.cinnamon.manager.monitor.KafkaBrokerMonitor;
import org.conquernos.cinnamon.manager.monitor.metrics.ResourceMetrics;
import kafka.cluster.Broker;
import kafka.cluster.EndPoint;
import org.conquernos.cinnamon.message.Message;
import org.conquernos.cinnamon.message.broker.BrokerMessage;
import org.conquernos.cinnamon.message.broker.BrokerMetricsMessage;
import org.conquernos.cinnamon.message.broker.TopicMessage;

import java.util.ArrayList;
import java.util.List;


public class BrokerManager extends Manager {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final ActorRef master;

    private final BrokerControl brokerControl;
    private final TopicControl topicControl;
    private final KafkaBrokerMonitor brokerMonitor;


    public BrokerManager(ActorRef master, MasterConfig config, Zookeeper zookeeper) throws CinnamonSchemaException {
        this.master = master;
        brokerControl = new BrokerControl(zookeeper);
        topicControl = new TopicControl(zookeeper);
        brokerMonitor = new KafkaBrokerMonitor(config);

        logger.debug("BrokerManager({})", getSelf().path());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        logger.debug("BrokerManager({}) onReceive - {}", getSelf(), message);

        if (message instanceof BrokerMessage.BrokerUrlRequest) {
            inquireBrokers((BrokerMessage.BrokerUrlRequest) message, getSender());

        } else if (message instanceof TopicMessage.TopicsInquiryRequest) {
            inquireAllTopics((TopicMessage.TopicsInquiryRequest) message, getSender());

        } else if (message instanceof TopicMessage.TopicInquiryRequest) {
            inquireTopic((TopicMessage.TopicInquiryRequest) message, getSender());

        } else if (message instanceof TopicMessage.TopicRegistrationRequest) {
            registerTopic((TopicMessage.TopicRegistrationRequest) message, getSender());

        } else if (message instanceof BrokerMetricsMessage.BrokerResourceMetricsRequest) {
            inquireResourceMetrics((BrokerMetricsMessage.BrokerResourceMetricsRequest) message, getSender());
        }
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        topicControl.close();
    }

    private void inquireBrokers(BrokerMessage.BrokerUrlRequest request, ActorRef requester) {
        List<Broker> brokers = brokerControl.getBrokers();
        List<String> urls = new ArrayList<>(brokers.size());

        brokers.forEach(broker -> {
            EndPoint endPoint = broker.endPoints().head();
            urls.add(endPoint.host() + ':' + endPoint.port());
        });

        BrokerMessage.BrokerUrlResponse response = (urls.size() > 0) ?
            new BrokerMessage.BrokerUrlResponse(Message.Result.SUCCESS, urls)
            : new BrokerMessage.BrokerUrlResponse(Message.Result.FAIL, null);

        requester.tell(response, master);
    }

    private void inquireAllTopics(TopicMessage.TopicsInquiryRequest request, ActorRef requester) {
        List<String> topics = topicControl.getTopicNames();

        TopicMessage.TopicsInquiryResponse response = topics != null ?
            new TopicMessage.TopicsInquiryResponse(Message.Result.SUCCESS, topics)
            : new TopicMessage.TopicsInquiryResponse(Message.Result.FAIL, "no topics", null);

        requester.tell(response, master);
    }

    private void inquireTopic(TopicMessage.TopicInquiryRequest request, ActorRef requester) {
        Topic topic = topicControl.getTopic(request.getTopic());

        TopicMessage.TopicInquiryResponse response = topic != null ?
            new TopicMessage.TopicInquiryResponse(Message.Result.SUCCESS, topic)
            : new TopicMessage.TopicInquiryResponse(Message.Result.FAIL, "no topic : " + request.getTopic(), null);

        requester.tell(response, master);
    }

    private void registerTopic(TopicMessage.TopicRegistrationRequest request, ActorRef requester) {
        TopicMessage.TopicRegistrationResponse response;

        try {
            topicControl.createTopic(request.getTopic(), request.getNumberOfPartitions(), request.getNumberOfReplications());
            response = new TopicMessage.TopicRegistrationResponse(Message.Result.SUCCESS);
        } catch (CinnamonKafkaException e) {
            response = new TopicMessage.TopicRegistrationResponse(Message.Result.FAIL, e.getMessage());
        }

        requester.tell(response, master);
    }

    private void inquireResourceMetrics(BrokerMetricsMessage.BrokerResourceMetricsRequest request, ActorRef requester) {
        try {
            List<Broker> brokers = brokerControl.getBrokers();
            List<ResourceMetrics> metricses = new ArrayList<>(brokers.size());
            for (Broker broker : brokers) {
                metricses.add(brokerMonitor.queryResourceMetrics(broker));
            }

            requester.tell(new BrokerMetricsMessage.BrokerResourceMetricsResponse(Message.Result.SUCCESS, metricses), master);
        } catch (Exception e) {
            requester.tell(new BrokerMetricsMessage.BrokerResourceMetricsResponse(Message.Result.FAIL, e.getMessage()), master);
        }
    }

}
