package org.conquernos.cinnamon.manager.monitor;


import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.monitor.metrics.BrokerMetrics;
import org.conquernos.cinnamon.manager.monitor.metrics.ResourceMetrics;
import org.conquernos.cinnamon.utils.jmx.JmxConnector;
import kafka.cluster.Broker;
import kafka.cluster.EndPoint;

import javax.management.openmbean.CompositeDataSupport;
import java.util.HashMap;
import java.util.Map;


public class KafkaBrokerMonitor {

    private static final String MEMORY = "Memory";
    private static final String HEAP_MEMORY_USAGE = "HeapMemoryUsage";
    private static final String NON_HEAP_MEMORY_USAGE = "NonHeapMemoryUsage";

    private static final String OS = "OperatingSystem";
    private static final String CPU_LOAD = "ProcessCpuLoad";

    private final Map<Broker, JmxConnector> jmxConnectors = new HashMap<>();
    private final int jmxPort;


    public KafkaBrokerMonitor(MasterConfig config) {
        this.jmxPort = config.getKafkaBrokerJmxPort();
    }

    public ResourceMetrics queryResourceMetrics(Broker broker) throws Exception {
        if (!jmxConnectors.containsKey(broker)) {
            EndPoint endPoint = broker.endPoints().head();
            jmxConnectors.put(broker, new JmxConnector(endPoint.host(), jmxPort));
        }

        JmxConnector jmx = jmxConnectors.get(broker);

        // memory
        Map<String, Object> attributes = jmx.query("java.lang", "type", MEMORY, HEAP_MEMORY_USAGE, NON_HEAP_MEMORY_USAGE);
        CompositeDataSupport attribute = (CompositeDataSupport) attributes.get(HEAP_MEMORY_USAGE);
        long memUsed = (long) attribute.get("used");
        long memMax = (long) attribute.get("max");

        // cpu
        attributes = jmx.query("java.lang", "type", OS, CPU_LOAD);
        float cpuLoad = ((Double) attributes.get(CPU_LOAD)).floatValue();

        return new ResourceMetrics(broker.id(), memUsed, memMax, cpuLoad);
    }

    public BrokerMetrics queryMessageMetrics(Broker broker) throws Exception {
        if (!jmxConnectors.containsKey(broker)) {
            EndPoint endPoint = broker.endPoints().head();
            jmxConnectors.put(broker, new JmxConnector(endPoint.host(), jmxPort));
        }

        JmxConnector jmx = jmxConnectors.get(broker);

        // message in rate
        // - kafka.server:type=BrokerTopicMetrics,name=MessagesInPerSec
        Map<String, Object> attributes = jmx.queryTypeAndName("kafka.server", "BrokerTopicMetrics", "MessagesInPerSec", "Count");
        long messagesInPerSec = (long) attributes.get("Count");

        // byte in rate
        // - kafka.server:type=BrokerTopicMetrics,name=BytesInPerSec
        attributes = jmx.queryTypeAndName("kafka.server", "BrokerTopicMetrics", "BytesInPerSec", "Count");
        long bytesInPerSec = (long) attributes.get("Count");

        // byte out rate
        // - kafka.server:type=BrokerTopicMetrics,name=BytesOutPerSec
        attributes = jmx.queryTypeAndName("kafka.server", "BrokerTopicMetrics", "BytesOutPerSec", "Count");
        long bytesOutPerSec = (long) attributes.get("Count");

        // request rate
        // - kafka.network:type=RequestMetrics,name=RequestsPerSec,request={Produce|FetchConsumer|FetchFollower}
        attributes = jmx.queryTypeAndNameAndRequest("kafka.network", "RequestMetrics", "RequestsPerSec", "Produce", "Count");
        long produceRequestsPerSec = (long) attributes.get("Count");

        attributes = jmx.queryTypeAndNameAndRequest("kafka.network", "RequestMetrics", "RequestsPerSec", "FetchConsumer", "Count");
        long consumerRequestsPerSec = (long) attributes.get("Count");

        // Log flush rate and time
        // - kafka.log:type=LogFlushStats,name=LogFlushRateAndTimeMs

        // # of under replicated partitions (|ISR| < |all replicas|)
        // - kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions
        // - 0

        // Is controller active on broker
        // - kafka.controller:type=KafkaController,name=ActiveControllerCount
        // - only one broker in the cluster should have 1

        // Leader election rate
        // - kafka.controller:type=ControllerStats,name=LeaderElectionRateAndTimeMs
        // - non-zero when there are broker failures

        // Unclean leader election rate
        // - kafka.controller:type=ControllerStats,name=UncleanLeaderElectionsPerSec
        // - 0

        // Partition counts
        // - kafka.server:type=ReplicaManager,name=PartitionCount
        // - mostly even across brokers

        // Leader replica counts
        // - kafka.server:type=ReplicaManager,name=LeaderCount
        // - mostly even across brokers

        // ISR(interrupt service routine = interrupt handler) shrink rate
        // - kafka.server:type=ReplicaManager,name=IsrShrinksPerSec
        // - If a broker goes down, ISR for some of the partitions will shrink. When that broker is up again, ISR will be expanded once the replicas are fully caught up. Other than that, the expected value for both ISR shrink rate and expansion rate is 0.

        // ISR expansion rate
        // - kafka.server:type=ReplicaManager,name=IsrExpandsPerSec	See above

        // Max lag in messages btw follower and leader replicas
        // - kafka.server:type=ReplicaFetcherManager,name=MaxLag,clientId=Replica
        // - lag should be proportional to the maximum batch size of a produce request.

        // Lag in messages per follower replica
        // - kafka.server:type=FetcherLagMetrics,name=ConsumerLag,clientId=([-.\w]+),topic=([-.\w]+),partition=([0-9]+)
        // - lag should be proportional to the maximum batch size of a produce request.

        // Requests waiting in the producer purgatory
        // - kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Produce
        // - non-zero if ack=-1 is used

        // Requests waiting in the fetch purgatory
        // - kafka.server:type=DelayedOperationPurgatory,name=PurgatorySize,delayedOperation=Fetch
        // - size depends on fetch.wait.max.ms in the consumer

        // Request total time
        // - kafka.network:type=RequestMetrics,name=TotalTimeMs,request={Produce|FetchConsumer|FetchFollower}
        // - broken into queue, local, remote and response send time

        // Time the request waits in the request queue
        // - kafka.network:type=RequestMetrics,name=RequestQueueTimeMs,request={Produce|FetchConsumer|FetchFollower}

        // Time the request is processed at the leader
        // - kafka.network:type=RequestMetrics,name=LocalTimeMs,request={Produce|FetchConsumer|FetchFollower}

        // Time the request waits for the follower
        // - kafka.network:type=RequestMetrics,name=RemoteTimeMs,request={Produce|FetchConsumer|FetchFollower}
        // - non-zero for produce requests when ack=-1

        // Time the request waits in the response queue
        // - kafka.network:type=RequestMetrics,name=ResponseQueueTimeMs,request={Produce|FetchConsumer|FetchFollower}

        // Time to send the response
        // - kafka.network:type=RequestMetrics,name=ResponseSendTimeMs,request={Produce|FetchConsumer|FetchFollower}

        // Number of messages the consumer lags behind the producer by. Published by the consumer, not broker.
        // - Old consumer: kafka.consumer:type=ConsumerFetcherManager,name=MaxLag,clientId=([-.\w]+)
        // - New consumer: kafka.consumer:type=consumer-fetch-manager-metrics,client-id={client-id} Attribute: records-lag-max

        // The average fraction of time the network processors are idle
        // - kafka.network:type=SocketServer,name=NetworkProcessorAvgIdlePercent
        // - between 0 and 1, ideally > 0.3

        // The average fraction of time the request handler threads are idle
        // - kafka.server:type=KafkaRequestHandlerPool,name=RequestHandlerAvgIdlePercent
        // - between 0 and 1, ideally > 0.3

        // Quota metrics per (user, client-id), user or client-id
        // - kafka.server:type={Produce|Fetch},user=([-.\w]+),client-id=([-.\w]+)
        // - Two attributes. throttle-time indicates the amount of time in ms the client was throttled. Ideally = 0. byte-rate indicates the data produce/consume rate of the client in bytes/sec. For (user, client-id) quotas, both user and client-id are specified. If per-client-id quota is applied to the client, user is not specified. If per-user quota is applied, client-id is not specified.

        return new BrokerMetrics(messagesInPerSec, bytesInPerSec, bytesOutPerSec, produceRequestsPerSec, consumerRequestsPerSec);
    }

    public void close() {
        jmxConnectors.forEach((broker, connector) -> connector.close());
        jmxConnectors.clear();
    }

}
