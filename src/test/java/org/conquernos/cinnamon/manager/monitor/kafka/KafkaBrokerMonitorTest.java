package org.conquernos.cinnamon.manager.monitor.kafka;


import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.broker.BrokerControl;
import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;

import org.conquernos.cinnamon.manager.monitor.KafkaBrokerMonitor;
import org.conquernos.cinnamon.manager.monitor.metrics.BrokerMetrics;
import org.conquernos.cinnamon.manager.monitor.metrics.ResourceMetrics;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import kafka.cluster.Broker;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class KafkaBrokerMonitorTest {

    private static BrokerControl control;
    private static KafkaBrokerMonitor monitor;

    @BeforeClass
    public static void setUp() {
        MasterConfig masterConfig = (MasterConfig) ConfigLoader.build(MasterConfig.class, ConfigLoader.load("conf/master.conf"));
        control = new BrokerControl(new Zookeeper("localhost:2181", 1000, false));
        monitor = new KafkaBrokerMonitor(masterConfig);
    }

    @AfterClass
    public static void tearDown() {
        control.close();
        monitor.close();
    }

    @Test
    public void testQueryResourceMetrics() throws Exception {
        List<Broker> brokers = control.getBrokers();
        Broker broker = brokers.get(0);

        ResourceMetrics metrics = monitor.queryResourceMetrics(broker);

        System.out.println(metrics);

        assertNotNull(metrics);
        assertTrue(metrics.getCpuPercent() >= 0.f);
        assertTrue(metrics.getHeapMemoryMax() > 0.f);
        assertTrue(metrics.getHeapMemoryUsed() > 0.f);
    }

    @Test
    public void testQueryMessageMetrics() throws Exception {
        List<Broker> brokers = control.getBrokers();
        Broker broker = brokers.get(0);

        BrokerMetrics metrics = monitor.queryMessageMetrics(broker);

        System.out.println(metrics);

        assertNotNull(metrics);
        assertTrue(metrics.getBytesInPerSec() >= 0.f);
        assertTrue(metrics.getBytesOutPerSec() >= 0.f);
        assertTrue(metrics.getConsumerRequestsPerSec() >= 0.f);
        assertTrue(metrics.getMessagesInPerSec() >= 0.f);
        assertTrue(metrics.getProduceRequestsPerSec() >= 0.f);
    }

}