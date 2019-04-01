package org.conquernos.cinnamon.manager.monitor.metrics;


import com.fasterxml.jackson.annotation.JsonGetter;


public class BrokerMetrics {

    private final long messagesInPerSec;
    private final long bytesInPerSec;
    private final long bytesOutPerSec;
    private final long produceRequestsPerSec;
    private final long consumerRequestsPerSec;


    public BrokerMetrics(long messagesInPerSec, long bytesInPerSec, long bytesOutPerSec, long produceRequestsPerSec, long consumerRequestsPerSec) {
        this.messagesInPerSec = messagesInPerSec;
        this.bytesInPerSec = bytesInPerSec;
        this.bytesOutPerSec = bytesOutPerSec;
        this.produceRequestsPerSec = produceRequestsPerSec;
        this.consumerRequestsPerSec = consumerRequestsPerSec;
    }

    @JsonGetter("messagesInPerSec")
    public long getMessagesInPerSec() {
        return messagesInPerSec;
    }

    @JsonGetter("bytesInPerSec")
    public long getBytesInPerSec() {
        return bytesInPerSec;
    }

    @JsonGetter("bytesOutPerSec")
    public long getBytesOutPerSec() {
        return bytesOutPerSec;
    }

    @JsonGetter("produceRequestsPerSec")
    public long getProduceRequestsPerSec() {
        return produceRequestsPerSec;
    }

    @JsonGetter("consumerRequestsPerSec")
    public long getConsumerRequestsPerSec() {
        return consumerRequestsPerSec;
    }

    @Override
    public String toString() {
        return "BrokerMetrics{" +
            "messagesInPerSec=" + messagesInPerSec +
            ", bytesInPerSec=" + bytesInPerSec +
            ", bytesOutPerSec=" + bytesOutPerSec +
            ", produceRequestsPerSec=" + produceRequestsPerSec +
            ", consumerRequestsPerSec=" + consumerRequestsPerSec +
            '}';
    }

}
