package org.conquernos.cinnamon.manager.control.broker;

import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TopicControlTest {

    private static TopicControl topicControl;

    private static final MasterConfig config = (MasterConfig) ConfigLoader.build(MasterConfig.class, ConfigLoader.load("master-test.conf"));


    @BeforeClass
    public static void setUp() throws Exception {
        Zookeeper zookeeper = new Zookeeper(config.getKafkaZkServers(), config.getKafkaZkTimeout(), false);
        topicControl = new TopicControl(zookeeper);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    @Test
    public void test() {
    }

}