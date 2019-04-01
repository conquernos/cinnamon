package org.conquernos.cinnamon.manager.control.consumer;

import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.manager.control.broker.KafkaAdminClient;
import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import kafka.admin.AdminClient;
import org.junit.*;

import java.util.List;


public class ConsumerControlTest {

    private static ConsumerControl consumerControl;

    private static final MasterConfig config = (MasterConfig) ConfigLoader.build(MasterConfig.class, ConfigLoader.load("master-test.conf"));

    private static final String TEST_GROUP_ID = "connect-hive-shover_test-sink";
    private static final String TEST_TOPIC = "shover_test";
    private static final int TEST_PARTITION = 0;


    @BeforeClass
    public static void setUp() throws Exception {
        Zookeeper zookeeper = new Zookeeper(config.getKafkaZkServers(), config.getKafkaZkTimeout(), false);
        AdminClient kafka = KafkaAdminClient.create(config);
        consumerControl = new ConsumerControl(zookeeper, kafka);
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    @Test
    public void getGroupIds() throws Exception {
        List<String> ids = consumerControl.getGroupIds();
        System.out.println(ids);
    }

    @Test
    public void getTopics() throws Exception {
//        List<String> topics = consumerControl.getTopics(TEST_TOPIC);
//        System.out.println(topics);
    }

    @Test
    public void getOffset() throws Exception {
        long offset = consumerControl.getOffset(TEST_GROUP_ID, TEST_TOPIC, TEST_PARTITION);
        System.out.println("offset : " + offset);
    }

}