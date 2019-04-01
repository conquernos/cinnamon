package org.conquernos.cinnamon.message.ingester;


import org.conquernos.cinnamon.message.Message;

public class IngesterMessage extends Message {

    public static abstract class IngesterRequest extends IngesterMessage {

    }

    public static abstract class IngesterResponse extends IngesterMessage {

        private final Result result;
        private final String reason;

        public IngesterResponse(Result result, String reason) {
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

}
