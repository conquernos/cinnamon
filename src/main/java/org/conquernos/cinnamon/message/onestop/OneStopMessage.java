package org.conquernos.cinnamon.message.onestop;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.conquernos.cinnamon.message.Message;
import org.conquernos.cinnamon.message.broker.TopicMessage.TopicRegistrationRequest;
import org.conquernos.cinnamon.message.ingester.ConnectorMessage.ConnectorRegistrationRequest;
import org.conquernos.cinnamon.message.schema.SchemaMessage.SchemaRegistrationRequest;

import java.util.List;

import static org.conquernos.cinnamon.message.Message.Result.FAIL;
import static org.conquernos.cinnamon.message.Message.Result.SUCCESS;

public class OneStopMessage extends Message {

    public static abstract class OneStopRequest extends OneStopMessage {

    }

    public static abstract class OneStopResponse extends OneStopMessage {

        private final Result result;
        private final String reason;

        public OneStopResponse(Result result, String reason) {
            this.result = result;
            this.reason = reason;
        }

        public Result getResult() {
            return result;
        }

        public String getReason() {
            return reason;
        }

    }

    public static class OneStopRegistrationRequest extends OneStopRequest {

        private final String topic;

        private final ArrayNode fields;

        private final int numberOfPartitions;
        private final int numberOfReplications;

        private final int numberOfTasks;
        private final int flushSize;
        private final String database;
        private final String directory;
        private final List<String> partitionFields;
        private final List<String> partitionDateFields;
        private final String partitionPath;


        @JsonCreator
        public OneStopRegistrationRequest(@JsonProperty("topic") String topic, @JsonProperty("fields") ArrayNode fields
            , @JsonProperty("numberOfPartitions") int numberOfPartitions, @JsonProperty("numberOfReplications") int numberOfReplications
            , @JsonProperty("numberOfTasks") int numberOfTasks, @JsonProperty("flushSize") int flushSize, @JsonProperty("database") String database
            , @JsonProperty("directory") String directory, @JsonProperty("partitionFields") List<String> partitionFields
            , @JsonProperty("partitionDateFields") List<String> partitionDateFields, @JsonProperty("partitionPath") String partitionPath) {
            this.topic = topic;
            this.fields = fields;
            this.numberOfPartitions = numberOfPartitions;
            this.numberOfReplications = numberOfReplications;
            this.numberOfTasks = numberOfTasks;
            this.flushSize = flushSize;
            this.database = database;
            this.directory = directory;
            this.partitionFields = partitionFields;
            this.partitionDateFields = partitionDateFields;
            this.partitionPath = partitionPath;
        }

        public String getTopic() {
            return topic;
        }

        public ArrayNode getFields() {
            return fields;
        }

        public int getNumberOfPartitions() {
            return numberOfPartitions;
        }

        public int getNumberOfReplications() {
            return numberOfReplications;
        }

        public int getNumberOfTasks() {
            return numberOfTasks;
        }

        public int getFlushSize() {
            return flushSize;
        }

        public String getDatabase() {
            return database;
        }

        public String getDirectory() {
            return directory;
        }

        public List<String> getPartitionFields() {
            return partitionFields;
        }

        public List<String> getPartitionDateFields() {
            return partitionDateFields;
        }

        public String getPartitionPath() {
            return partitionPath;
        }

        public SchemaRegistrationRequest toSchemaRegistrationRequest() {
            return new SchemaRegistrationRequest(getTopic(), getFields());
        }

        public TopicRegistrationRequest toTopicRegistrationRequest() {
            return new TopicRegistrationRequest(getTopic(), getNumberOfPartitions(), getNumberOfReplications());
        }

        public ConnectorRegistrationRequest toConnectorRegistrationRequest() {
            return new ConnectorRegistrationRequest(getTopic(), getNumberOfTasks(), getFlushSize()
                , getDatabase(), getDirectory(), getPartitionFields(), getPartitionDateFields(), getPartitionPath());
        }

    }

    public static class OneStopRegistrationResponse extends OneStopResponse {

        private final Result[] results;
        private final String[] reasons;


        public OneStopRegistrationResponse(Result[] results, String[] reasons) {
            super((results[0] != null && results[0].equals(SUCCESS)
                    && results[1] != null && results[1].equals(SUCCESS)
                    && results[2] != null && results[2].equals(SUCCESS)) ? SUCCESS : FAIL
                , reasons[0] != null ? reasons[0] : reasons[1] != null ? reasons[1] : reasons[2]);
            this.results = results;
            this.reasons = reasons;
        }

        @JsonGetter("schemaRegistrationResult")
        public Result getSchemaRegistractionResult() {
            return results[0];
        }

        @JsonGetter("topicRegistrationResult")
        public Result getTopicRegistractionResult() {
            return results[1];
        }

        @JsonGetter("connectorRegistrationResult")
        public Result getConnectorRegistractionResult() {
            return results[2];
        }

        @JsonGetter("schemaRegistrationReason")
        public String getSchemaRegistrationReason() {
            return reasons[0];
        }

        @JsonGetter("topicRegistrationReason")
        public String getTopicRegistrationReason() {
            return reasons[1];
        }

        @JsonGetter("connectorRegistrationReason")
        public String getConnectorRegistrationReason() {
            return reasons[2];
        }

    }

}
