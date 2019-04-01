package org.conquernos.cinnamon.message.schema;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.conquernos.cinnamon.message.Message;

import java.util.Collection;
import java.util.List;

public class SchemaMessage extends Message {

    public static abstract class SchemaRequest extends SchemaMessage {

    }

    public static abstract class SchemaResponse extends SchemaMessage {

        private final Result result;
        private final String reason;


        public SchemaResponse(Result result, String reason) {
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

    public static final class SchemaRegistryUrlRequest extends SchemaRequest {

        @JsonCreator
        public SchemaRegistryUrlRequest() {
        }

    }

    public static final class SchemaRegistryUrlResponse extends SchemaResponse {

        private final List<String> urls;


        public SchemaRegistryUrlResponse(@JsonProperty("result") Result result, @JsonProperty List<String> urls) {
            this(result, null, urls);
        }

        public SchemaRegistryUrlResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty List<String> urls) {
            super(result, reason);
            this.urls = urls;
        }

        public List<String> getUrls() {
            return urls;
        }

    }

    public static final class SubjectsInquiryRequest extends SchemaRequest {

        @JsonCreator
        public SubjectsInquiryRequest() {
        }

    }

    public static final class SubjectsInquiryResponse extends SchemaResponse {

        private final Collection<String> subjects;


        public SubjectsInquiryResponse(@JsonProperty("result") Result result, @JsonProperty Collection<String> subjects) {
            this(result, null, subjects);
        }

        public SubjectsInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty Collection<String> subjects) {
            super(result, reason);
            this.subjects = subjects;
        }

        public Collection<String> getSubjects() {
            return subjects;
        }

    }

    public static final class SchemaInquiryRequest extends SchemaRequest {

        private final String subject;
        private final int version;


        @JsonCreator
        public SchemaInquiryRequest(@JsonProperty("subject") String subject, @JsonProperty("version") int version) {
            this.subject = subject;
            this.version = version;
        }

        public String getSubject() {
            return subject;
        }

        public int getVersion() {
            return version;
        }

        public boolean isAllVersion() {
            return version == 0;
        }

    }

    public static final class SchemaInquiryResponse extends SchemaResponse {

        private final JsonNode schema;


        public SchemaInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("schema") JsonNode schema) {
            this(result, null, schema);
        }

        public SchemaInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty("schema") JsonNode schema) {
            super(result, reason);
            this.schema = schema;
        }

        public JsonNode getSchema() {
            return schema;
        }

    }

    public static final class SchemaAllVersionInquiryResponse extends SchemaResponse {

        private final List<JsonNode> schemas;


        public SchemaAllVersionInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("schemas") List<JsonNode> schemas) {
            this(result, null, schemas);
        }

        public SchemaAllVersionInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty("schemas") List<JsonNode> schemas) {
            super(result, reason);
            this.schemas = schemas;
        }

        public List<JsonNode> getSchemas() {
            return schemas;
        }

    }

    public static final class SchemaRegistrationRequest extends SchemaRequest {

        private final String subject;
        private final ArrayNode fields;


        @JsonCreator
        public SchemaRegistrationRequest(@JsonProperty("subject") String subject, @JsonProperty("fields") ArrayNode fields) {
            this.subject = subject;
            this.fields = fields;
        }

        public String getSubject() {
            return subject;
        }

        public ArrayNode getFields() {
            return fields;
        }

    }

    public static final class SchemaRegistrationResponse extends SchemaResponse {

        private final int id;


        public SchemaRegistrationResponse(@JsonProperty("result") Result result, @JsonProperty("id") int id) {
            this(result, null, id);
        }

        public SchemaRegistrationResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty("id") int id) {
            super(result, reason);
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }

    public static final class SchemaUpdateRequest extends SchemaRequest {

        private final String subject;
        private final ArrayNode fields;


        @JsonCreator
        public SchemaUpdateRequest(@JsonProperty("subject") String subject, @JsonProperty("fields") ArrayNode fields) {
            this.subject = subject;
            this.fields = fields;
        }

        public String getSubject() {
            return subject;
        }

        public ArrayNode getFields() {
            return fields;
        }

    }

    public static final class SchemaUpdateResponse extends SchemaResponse {

        private final int id;
        private final int version;


        public SchemaUpdateResponse(@JsonProperty("result") Result result, @JsonProperty("id") int id, @JsonProperty("version") int version) {
            this(result, null, id, version);
        }

        public SchemaUpdateResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty("id") int id, @JsonProperty("version") int version) {
            super(result, reason);
            this.id = id;
            this.version = version;
        }

        public int getId() {
            return id;
        }

        public int getVersion() {
            return version;
        }

    }

}
