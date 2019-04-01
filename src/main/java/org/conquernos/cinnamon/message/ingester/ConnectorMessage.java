package org.conquernos.cinnamon.message.ingester;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;


public class ConnectorMessage extends IngesterMessage {

    public static abstract class ConnectorRequest extends IngesterRequest {

    }

    public static abstract class ConnectorResponse extends IngesterResponse {

        public ConnectorResponse(Result result, String reason) {
            super(result, reason);
        }

    }

    public static final class ConnectorsInquiryRequest extends ConnectorRequest {

        @JsonCreator
        public ConnectorsInquiryRequest() {
        }

    }

    public static final class ConnectorsInquiryResponse extends ConnectorResponse {

        private final JsonNode connectors;


        public ConnectorsInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("connectors") JsonNode connectors) {
            super(result, null);
            this.connectors = connectors;
        }

        public ConnectorsInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);
            this.connectors = null;
        }

        public JsonNode getConnectors() {
            return connectors;
        }

    }

    public static final class ConnectorInquiryRequest extends ConnectorRequest {

        private final String topic;


        @JsonCreator
        public ConnectorInquiryRequest(@JsonProperty("topic") String topic) {
            this.topic = topic;
        }

        public String getTopic() {
            return topic;
        }

    }

    public static final class ConnectorInquiryResponse extends ConnectorResponse {

        private final JsonNode connector;


        @JsonCreator
        public ConnectorInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("connector") JsonNode connector) {
            super(result, null);
            this.connector = connector;
        }

        @JsonCreator
        public ConnectorInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);
            this.connector = null;
        }

        public JsonNode getConnector() {
            return connector;
        }

    }

    public static final class ConnectorRegistrationRequest extends ConnectorRequest {

        private final String topic;
        private final int numberOfTasks;
        private final int flushSize;
        private final String database;
        private final String directory;
        private final List<String> partitionFields;
        private final List<String> partitionDateFields;
        private final String partitionPath;


        @JsonCreator
        public ConnectorRegistrationRequest(@JsonProperty("topic") String topic, @JsonProperty("numberOfTasks") int numberOfTasks
            , @JsonProperty("flushSize") int flushSize, @JsonProperty("database") String database, @JsonProperty("directory") String directory
            , @JsonProperty("partitionFields") List<String> partitionFields, @JsonProperty("partitionDateFields") List<String> partitionDateFields
            , @JsonProperty("partitionPath") String partitionPath) {
            this.topic = topic;
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

    }

    public static final class ConnectorRegistrationResponse extends ConnectorResponse {

        public ConnectorRegistrationResponse(@JsonProperty("result") Result result) {
            super(result, null);
        }

        public ConnectorRegistrationResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);
        }

    }

    public static final class ConnectorUpdateRequest extends ConnectorRequest {

        private final String topic;
        private final int numberOfTasks;
        private final int flushSize;
        private final String database;
        private final String directory;
        private final List<String> partitionFields;
        private final List<String> partitionDateFields;
        private final String partitionPath;


        @JsonCreator
        public ConnectorUpdateRequest(@JsonProperty("topic") String topic, @JsonProperty("numberOfTasks") int numberOfTasks
            , @JsonProperty("flushSize") int flushSize, @JsonProperty("database") String database, @JsonProperty("directory") String directory
            , @JsonProperty("partitionFields") List<String> partitionFields, @JsonProperty("partitionDateFields") List<String> partitionDateFields
            , @JsonProperty("partitionPath") String partitionPath) {
            this.topic = topic;
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

    }

    public static final class ConnectorUpdateResponse extends ConnectorResponse {

        public ConnectorUpdateResponse(@JsonProperty("result") Result result) {
            super(result, null);
        }

        public ConnectorUpdateResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);
        }

    }

}
