package org.conquernos.cinnamon.manager.control.zookeeper;


import org.conquernos.cinnamon.utils.config.Configuration;
import kafka.utils.ZkUtils;

import java.util.Collections;
import java.util.List;


public class Zookeeper {

//    private final CuratorFramework curatorFramework;
//    private final ZkClient zkClient;
    private final ZkUtils zkUtils;

    public Zookeeper(String zkHost, long connectionTimeout, boolean zkSecurity) {
        this(Collections.singletonList(zkHost), connectionTimeout, zkSecurity);
    }

    public Zookeeper(List<String> zkHosts, long connectionTimeout, boolean zkSecurity) {
        String zkHostsString = Configuration.toListString(zkHosts);

//        curatorFramework = CuratorClient.getCuratorFramework(zkHostsString.toString());

//        zkClient = new ZkClient(new CuratorZKClientBridge(curatorFramework));
//        zkClient.waitUntilConnected(connectionTimeout, TimeUnit.MILLISECONDS);

//        zkUtils = ZkUtils.apply(zkClient,zkSecurity);

        zkUtils = ZkUtils.apply(zkHostsString, (int) connectionTimeout, (int) connectionTimeout, zkSecurity);
    }

//    public CuratorFramework getCuratorFramework() {
//        return curatorFramework;
//    }

//    public ZkClient getZkClient() {
//        return zkClient;
//    }

    public ZkUtils getZkUtils() {
        return zkUtils;
    }

    public void close() {
        zkUtils.close();
//        zkClient.close();
//        curatorFramework.close();
    }

}
