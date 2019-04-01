package org.conquernos.cinnamon.manager.control.consumer;


import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import kafka.admin.AdminClient;
import kafka.api.OffsetFetchResponse;
import kafka.client.ClientUtils;
import kafka.common.ErrorMapping;
import kafka.common.OffsetMetadataAndError;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetFetchRequest;
import kafka.network.BlockingChannel;
import kafka.utils.ZkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import java.util.ArrayList;
import java.util.List;


public class ConsumerControl {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerControl.class);

    public static final int OFFSET_NOT_SET = -1;

    private final ZkUtils zkUtils;
    private final AdminClient admin;

    public ConsumerControl(Zookeeper zookeeper, AdminClient admin) {
        this.zkUtils = zookeeper.getZkUtils();
        this.admin = admin;
    }

    public List<String> getGroupIds() {
        List<String> ids = new ArrayList<>();
        JavaConversions.seqAsJavaList(admin.listAllConsumerGroupsFlattened()).forEach(group -> ids.add(group.groupId()));

        return ids;
    }

    public long getOffset(String groupId, String topic, int partition) {
        List<TopicAndPartition> topicAndPartitions = new ArrayList<>();
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        topicAndPartitions.add(topicAndPartition);

        OffsetFetchRequest fetchRequest = new OffsetFetchRequest(groupId, topicAndPartitions, (short) 1, 1, "");

        long retrievedOffset = OFFSET_NOT_SET;

        BlockingChannel channel = null;
        try {
            channel = ClientUtils.channelToOffsetManager(groupId, zkUtils, 1000, 500);
            channel.send(fetchRequest.underlying());
            OffsetFetchResponse fetchResponse = OffsetFetchResponse.readFrom(channel.receive().payload(), 1);

            OffsetMetadataAndError result = fetchResponse.requestInfo().get(topicAndPartition).get();
            short error = result.error();
            if (error == ErrorMapping.NoError()) {
                retrievedOffset = result.offsetMetadata().offset();
            }
        } finally {
            if (channel != null && channel.isConnected()) channel.disconnect();
        }

        return retrievedOffset;

//        System.out.println(admin.listGroupOffsets(groupId));
//        Option<Object> offset = admin.listGroupOffsets(groupId).get(new TopicPartition(topic, partition));
//        if (offset.nonEmpty()) {
//            return (Long) offset.get();
//        }
//        return OFFSET_NOT_SET;
    }

}
