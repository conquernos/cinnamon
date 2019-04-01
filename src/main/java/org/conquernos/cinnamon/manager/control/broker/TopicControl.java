package org.conquernos.cinnamon.manager.control.broker;


import org.conquernos.cinnamon.exception.kafka.CinnamonKafkaException;
import org.conquernos.cinnamon.manager.control.zookeeper.Zookeeper;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.api.LeaderAndIsr;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.JavaConversions;
import scala.collection.Seq;
import scala.math.Ordering;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class TopicControl {

    private static final Logger logger = LoggerFactory.getLogger(TopicControl.class);

    private final ZkUtils zkUtils;

    private final Map<String, ReentrantLock> topicDefinitionLock = new HashMap<>();


    public TopicControl(Zookeeper zookeeper) {
        zkUtils = zookeeper.getZkUtils();
    }

    public void close() {
    }

    public List<String> getTopicNames() {
        Seq<String> topicNames = zkUtils.getAllTopics().sorted(Ordering.String$.MODULE$);
        return new ArrayList<>(JavaConversions.asJavaCollection(topicNames));
    }

    public List<Topic> getTopics() {
        Seq<String> topicNames = zkUtils.getAllTopics().sorted(Ordering.String$.MODULE$);
        return getTopicsData(topicNames);
    }

    public Topic getTopic(String topicName) {
        List<Topic> topics = getTopicsData(JavaConversions.asScalaBuffer(Collections.singletonList(topicName)));
        return (topics.isEmpty() ? null : topics.get(0));
    }

    public List<Partition> getTopicPartitions(String topic) {
        return getTopicPartitions(topic, null);
    }

    public boolean partitionExists(String topicName, int partition) {
        Topic topic = getTopic(topicName);
        return (partition >= 0 && partition < topic.getPartitions().size());
    }

    public Partition getTopicPartition(String topic, int partition) {
        List<Partition> partitions = getTopicPartitions(topic, partition);
        if (partitions == null || partitions.isEmpty()) return null;

        return partitions.get(0);
    }

    private List<Partition> getTopicPartitions(String topic, Integer partitionsFilter) {
        scala.collection.Map<String, scala.collection.Map<Object, Seq<Object>>> topicPartitions
            = zkUtils.getPartitionAssignmentForTopics(JavaConversions.asScalaBuffer(Collections.singletonList(topic)));

        if (!topicPartitions.get(topic).isEmpty()) {
            scala.collection.Map<Object, Seq<Object>> parts = topicPartitions.get(topic).get();
            return extractPartitionsFromZKData(parts, topic, partitionsFilter);
        }

        return null;
    }

    public void createTopic(String topic, int numberOfPartitions, int numberOfReplications) throws CinnamonKafkaException {
        if (numberOfPartitions <= 0) throw new CinnamonKafkaException("number of partitions must be larger than 0");

        if (numberOfReplications <= 0) throw new CinnamonKafkaException("replication factor must be larger than 0");

        ReentrantLock lock = getLock(topic);

        if (!topicExists(topic)) {
            if (lock.tryLock()) {
                try {
                    AdminUtils.createTopic(zkUtils, topic, numberOfPartitions, numberOfReplications, new Properties(), RackAwareMode.Disabled$.MODULE$);
                    logger.info("Topic created : {}, partitions: {}, replications: {}", topic, numberOfPartitions, numberOfReplications);
                } catch (TopicExistsException e) {
                    logger.info("Topic already exists : {}", topic);
                    throw new CinnamonKafkaException("topic already exists : " + topic);
                } catch (Exception e) {
                    logger.info("{} : {}", e.getMessage(), topic);
                    throw new CinnamonKafkaException(e.getMessage() + " : " + topic, e);
                } finally {
                    lock.unlock();
                }
            }
        } else {
            logger.info("Topic already exists : {}", topic);
            throw new CinnamonKafkaException("topic already exists : " + topic);
        }
    }

    public void deleteTopic(String topic) throws CinnamonKafkaException {
        ReentrantLock lock = getLock(topic);

        if (lock.tryLock()) {
            try {
                if (topicExists(topic)) {
                    AdminUtils.deleteTopic(zkUtils, topic);
                } else {
                    logger.info("Topic not exists : {}", topic);
                    throw new CinnamonKafkaException("topic not exists : " + topic);
                }
            } catch (Exception e) {
                logger.info("Topic delete fail : {}", topic);
                throw new CinnamonKafkaException("topic delete fail : " + topic, e);
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean topicExists(String topic) {
        return AdminUtils.topicExists(zkUtils, topic);
    }


    private ReentrantLock getLock(String topicName) {
        synchronized (topicDefinitionLock) {
            if (!topicDefinitionLock.containsKey(topicName)) {
                topicDefinitionLock.put(topicName, new ReentrantLock(false));
            }
            return topicDefinitionLock.get(topicName);
        }
    }

    private List<Topic> getTopicsData(Seq<String> topicNames) {
        scala.collection.Map<String, scala.collection.Map<Object, Seq<Object>>> topicPartitions =
            zkUtils.getPartitionAssignmentForTopics(topicNames);
        List<Topic> topics = new Vector<>(topicNames.size());

        // Admin utils only supports getting either 1 or all topic configs. These per-topic overrides
        // shouldn't be common, so we just grab all of them to keep this simple
        scala.collection.Map<String, Properties> configs = AdminUtils.fetchAllTopicConfigs(zkUtils);
        for (String topicName : JavaConversions.asJavaCollection(topicNames)) {
            if (!topicPartitions.get(topicName).isEmpty()) {
                scala.collection.Map<Object, Seq<Object>> partitionMap = topicPartitions.get(topicName).get();
                List<Partition> partitions = extractPartitionsFromZKData(partitionMap, topicName, null);
                if (partitions.size() == 0) continue;

                Option<Properties> topicConfigOpt = configs.get(topicName);
                Properties topicConfigs = topicConfigOpt.isEmpty() ? new Properties() : topicConfigOpt.get();
                Topic topic = new Topic(topicName, topicConfigs, partitions);
                topics.add(topic);
            }
        }

        return topics;
    }

    private List<Partition> extractPartitionsFromZKData(scala.collection.Map<Object, Seq<Object>> parts, String topic, Integer partitionsFilter) {
        List<Partition> partitions = new Vector<>();

        for (java.util.Map.Entry<Object, Seq<Object>> part : JavaConversions.mapAsJavaMap(parts).entrySet()) {
            int partId = (Integer) part.getKey();
            if (partitionsFilter != null && partitionsFilter != partId) continue;

            Partition partition = new Partition();
            partition.setPartition(partId);
            Option<LeaderAndIsr> leaderAndIsrOpt = zkUtils.getLeaderAndIsrForPartition(topic, partId);

            if (!leaderAndIsrOpt.isEmpty()) {
                LeaderAndIsr leaderAndIsr = leaderAndIsrOpt.get();
                partition.setLeader(leaderAndIsr.leader());
                Seq<Object> isr = leaderAndIsr.isr().toSeq();
                List<PartitionReplica> partReplicas = new Vector<>();

                for (Object brokerObj : JavaConversions.asJavaCollection(part.getValue())) {
                    int broker = (Integer) brokerObj;
                    PartitionReplica r = new PartitionReplica(broker, (leaderAndIsr.leader() == broker), isr.contains(broker));
                    partReplicas.add(r);
                }

                partition.setReplicas(partReplicas);
                partitions.add(partition);
            }
        }

        return partitions;
    }

}
