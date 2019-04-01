package org.conquernos.cinnamon.message.broker;


import org.conquernos.cinnamon.manager.monitor.metrics.ResourceMetrics;

import java.util.List;


public class BrokerMetricsMessage extends BrokerMessage {

    public static abstract class BrokerMetricsRequest extends BrokerRequest {

    }

    public static abstract class BrokerMetricsResponse extends BrokerResponse {

        public BrokerMetricsResponse(Result result, String reason) {
            super(result, reason);
        }

    }

    public static final class BrokerResourceMetricsRequest extends BrokerRequest {

    }

    public static final class BrokerResourceMetricsResponse extends BrokerResponse {

        private final List<ResourceMetrics> resourceMetricses;


        public BrokerResourceMetricsResponse(Result result, String reason) {
            super(result, reason);
            this.resourceMetricses = null;
        }

        public BrokerResourceMetricsResponse(Result result, List<ResourceMetrics> resourceMetricses) {
            super(result, null);
            this.resourceMetricses = resourceMetricses;
        }

        public List<ResourceMetrics> getResourceMetricses() {
            return resourceMetricses;
        }

    }

}
