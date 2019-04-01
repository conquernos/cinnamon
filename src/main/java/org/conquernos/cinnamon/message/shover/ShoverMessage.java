package org.conquernos.cinnamon.message.shover;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.conquernos.cinnamon.manager.control.shover.Shover;
import org.conquernos.cinnamon.message.Message;

import java.util.Collection;


public class ShoverMessage extends Message {

    public static abstract class ShoverRequest extends ShoverMessage {

    }

    public static abstract class ShoverResponse extends ShoverMessage {

        private final Result result;
        private final String reason;

        public ShoverResponse(Result result, String reason) {
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

    public static class ShoverRegistrationRequest extends ShoverRequest {

        private final Shover shover;

        @JsonCreator
        public ShoverRegistrationRequest(@JsonProperty("shover") Shover shover) {
            this.shover = shover;
        }

        public Shover getShover() {
            return shover;
        }

    }

    public static class ShoverRegistrationResponse extends ShoverResponse {

        private final Collection<String> brokerUrls;
        private final Collection<String> schemaRegistryUrls;


        public ShoverRegistrationResponse(@JsonProperty("result") Result result
            , @JsonProperty("brokerUrls") Collection<String> brokerUrls, @JsonProperty("schemaRegistryUrls") Collection<String> schemaRegistryUrls) {
            super(result, null);

            this.brokerUrls = brokerUrls;
            this.schemaRegistryUrls = schemaRegistryUrls;
        }

        public ShoverRegistrationResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);

            this.brokerUrls = null;
            this.schemaRegistryUrls = null;
        }

        public Collection<String> getBrokerUrls() {
            return brokerUrls;
        }

        public Collection<String> getSchemaRegistryUrls() {
            return schemaRegistryUrls;
        }

    }

    public static class ShoverInquiryRequest extends ShoverRequest {

        private final String id;


        @JsonCreator
        public ShoverInquiryRequest(@JsonProperty("id") String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

    }

    public static class ShoversInquiryRequest extends ShoverRequest {

        @JsonCreator
        public ShoversInquiryRequest() {
        }

    }

    public static class ShoverInquiryResponse extends ShoverResponse {

        private final Shover shover;


        public ShoverInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("shovers") Shover shover) {
            super(result, null);

            this.shover = shover;
        }

        public ShoverInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);

            this.shover = null;
        }

        public Shover getShover() {
            return shover;
        }

    }

    public static class ShoversInquiryResponse extends ShoverResponse {

        private final Collection<Shover> shovers;


        public ShoversInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("shovers") Collection<Shover> shovers) {
            super(result, null);

            this.shovers = shovers;
        }

        public ShoversInquiryResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason) {
            super(result, reason);

            this.shovers = null;
        }

        public Collection<Shover> getShovers() {
            return shovers;
        }

    }

}
