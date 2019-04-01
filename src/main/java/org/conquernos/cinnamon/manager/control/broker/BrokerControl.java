package org.conquernos.cinnamon.manager.control.broker;


import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import kafka.cluster.Broker;
import kafka.utils.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.ArrayList;
import java.util.List;

public class BrokerControl {

    private static final Logger logger = LoggerFactory.getLogger(TopicControl.class);

    private final ZkUtils zkUtils;


    public BrokerControl(Zookeeper zookeeper) {
        this.zkUtils = zookeeper.getZkUtils();
    }

    public List<Broker> getBrokers() {
        return new ArrayList<>(JavaConversions.asJavaCollection(zkUtils.getAllBrokersInCluster()));
    }

    public void close() {
        zkUtils.close();
    }

}
