package org.conquernos.cinnamon.message.broker;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.conquernos.cinnamon.manager.control.broker.Topic;

import java.util.List;


public class TopicMessage extends BrokerMessage {

    public static abstract class TopicRequest extends BrokerRequest {

    }

    public static abstract class TopicResponse extends BrokerResponse {

        public TopicResponse(Result result, String reason) {
            super(result, reason);
        }

    }

    public static final class TopicsInquiryRequest extends TopicRequest {

        @JsonCreator
        public TopicsInquiryRequest() {
        }

    }

    public static final class TopicsInquiryResponse extends TopicResponse {

        private final List<String> topics;

        public TopicsInquiryResponse(@JsonProperty("result") Result result, @JsonProperty List<String> topics) {
            this(result, null, topics);
        }

        public TopicsInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty("topics") List<String> topics) {
            super(result, reason);
            this.topics = topics;
        }

        public List<String> getTopics() {
            return topics;
        }

    }

    public static final class TopicInquiryRequest extends TopicRequest {

        private final String topic;

        @JsonCreator
        public TopicInquiryRequest(@JsonProperty("topic") String topic) {
            this.topic = topic;
        }

        public String getTopic() {
            return topic;
        }

    }

    public static final class TopicInquiryResponse extends TopicResponse {

        private final Topic topic;

        public TopicInquiryResponse(@JsonProperty("result") Result result, @JsonProperty Topic topic) {
            this(result, null, topic);
        }

        public TopicInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty("topic") Topic topic) {
            super(result, reason);
            this.topic = topic;
        }

        public Topic getTopic() {
            return topic;
        }

    }

    public static final class TopicRegistrationRequest extends TopicRequest {

        private final String topic;
        private final int numberOfPartitions;
        private final int numberOfReplications;

        public TopicRegistrationRequest(@JsonProperty("topic") String topic, @JsonProperty("numberOfPartitions") int numberOfPartitions, @JsonProperty("numberOfReplications") int numberOfReplications) {
            this.topic = topic;
            this.numberOfPartitions = numberOfPartitions;
            this.numberOfReplications = numberOfReplications;
        }

        public String getTopic() {
            return topic;
        }

        public int getNumberOfPartitions() {
            return numberOfPartitions;
        }

        public int getNumberOfReplications() {
            return numberOfReplications;
        }

    }

    public static final class TopicRegistrationResponse extends TopicResponse {

        public TopicRegistrationResponse(@JsonProperty("result") Result result) {
            super(result, null);
        }

        public TopicRegistrationResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);
        }

    }

}
