package org.conquernos.cinnamon.message.broker;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.conquernos.cinnamon.message.Message;

import java.util.List;

public class BrokerMessage extends Message {

    public static abstract class BrokerRequest extends BrokerMessage {

    }

    public static abstract class BrokerResponse extends BrokerMessage {

        private final Result result;
        private final String reason;

        public BrokerResponse(Result result, String reason) {
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

    public static final class BrokerUrlRequest extends BrokerRequest {

        @JsonCreator
        public BrokerUrlRequest() {
        }
    }

    public static final class BrokerUrlResponse extends BrokerResponse {

        private final List<String> urls;

        public BrokerUrlResponse(@JsonProperty("result") Result result, @JsonProperty List<String> urls) {
            this(result, null, urls);
        }

        public BrokerUrlResponse(@JsonProperty("result") Result result, @JsonProperty("reason") String reason, @JsonProperty List<String> urls) {
            super(result, reason);
            this.urls = urls;
        }

        public List<String> getUrls() {
            return urls;
        }

    }

}
