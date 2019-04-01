package org.conquernos.cinnamon.config;


import com.typesafe.config.Config;

import java.util.List;


public class MasterConfig extends CinnamonConfig {

    private static final String CLUSTER_NAME_CONF = "akka.cluster.name";
    private static final String MESSAGE_TIMEOUT = "akka.message.timeout";

    private static final String KAFKA_ZK_SERVERS_CONF = "kafka.zookeeper.servers"; // zookeeper server list ([ip:port,...])
    private static final String KAFKA_BROKER_SERVERS_CONF = "kafka.broker.servers"; // broker server list ([ip:port,...])
    private static final String KAFKA_SCHEMA_SERVER_CONF = "kafka.schema.server"; // schema-registry server list (ip:port)
    private static final String KAFKA_INGESTER_SERVERS_CONF = "kafka.ingester.servers"; // ingester server list ([ip:port,...])

    private static final String ZK_CONNECTION_TIMEOUT = "kafka.zookeeper.timeout";
    private static final String INGESTER_CONNECTION_TIMEOUT = "kafka.ingester.timeout";

    private static final String HDFS_CONNECTOR_CLASS = "kafka.ingester.connector.class";
    private static final String HDFS_CONNECTOR_HDFS_URL = "kafka.ingester.connector.hdfs.url";
    private static final String HDFS_CONNECTOR_HADOOP_CONF_DIR = "kafka.ingester.connector.hadoop.conf.dir";
    private static final String HDFS_CONNECTOR_LOG_DIR = "kafka.ingester.connector.logs.dir";
    private static final String HDFS_CONNECTOR_HIVE_INTEGRATION = "kafka.ingester.connector.hive.integration";
    private static final String HDFS_CONNECTOR_HIVE_METASTORE_URIS = "kafka.ingester.connector.hive.metastore.uris";
    private static final String HDFS_CONNECTOR_SCHEMA_COMPATIBILITY = "kafka.ingester.connector.schema.compatibility";
    private static final String HDFS_CONNECTOR_PARTITIONER_CLASS  = "kafka.ingester.connector.partitioner.class";
    private static final String HDFS_CONNECTOR_LOCALE = "kafka.ingester.connector.locale";
    private static final String HDFS_CONNECTOR_TIMEZONE = "kafka.ingester.connector.timezone";

    private static final String KAFKA_BROKER_JMX_PORT_CONF = "kafka.broker.jmx.port";


    // cinnamon - cluster
    private final String clusterSystemName;

    private final long messageTimeout;

    // shover

    // kafka - zookeeper
    private final List<String> kafkaZkServers;
    private final long kafkaZkTimeout;

    // kafka - brokers
    private final List<String> kafkaBrokerServers;
    private final int kafkaBrokerJmxPort;

    // kafka - schema-registry
    private final String kafkaSchemaServer;

    // ingester
    private final List<String> ingesterServers;
    private final long ingesterTimeout;

    // ingester - connector
    private final String hdfsConnectorClass;
    private final String hdfsConnectorHdfsUrl;
    private final String hdfsConnectorHadoopConfDir;
    private final String hdfsConnectorLogDir;
    private final String hdfsConnectorHiveIntegration;
    private final String hdfsConnectorHiveMetastoreUris;
    private final String hdfsConnectorSchemaCompatibility;
    private final String hdfsConnectorPartitionerClass;
    private final String hdfsConnectorLocale;
    private final String hdfsConnectorTimezone;


    public MasterConfig(Config config) {
        super(config);

        messageTimeout = getLongFromConfig(config, MESSAGE_TIMEOUT, 60000L);
        clusterSystemName = getStringFromConfig(config, CLUSTER_NAME_CONF, true);

        kafkaZkServers = getStringListFromConfig(config, KAFKA_ZK_SERVERS_CONF, true);
        kafkaZkTimeout = getLongFromConfig(config, ZK_CONNECTION_TIMEOUT, 10000L);

        kafkaBrokerServers = getStringListFromConfig(config, KAFKA_BROKER_SERVERS_CONF, true);
        kafkaBrokerJmxPort = getIntegerFromConfig(config, KAFKA_BROKER_JMX_PORT_CONF, true);

        kafkaSchemaServer = getStringFromConfig(config, KAFKA_SCHEMA_SERVER_CONF, true);

        ingesterServers = getStringListFromConfig(config, KAFKA_INGESTER_SERVERS_CONF, true);
        ingesterTimeout = getLongFromConfig(config, INGESTER_CONNECTION_TIMEOUT, 10000L);

        hdfsConnectorClass = getStringFromConfig(config, HDFS_CONNECTOR_CLASS, true);
        hdfsConnectorHdfsUrl = getStringFromConfig(config, HDFS_CONNECTOR_HDFS_URL, true);
        hdfsConnectorHadoopConfDir = getStringFromConfig(config, HDFS_CONNECTOR_HADOOP_CONF_DIR, true);
        hdfsConnectorLogDir = getStringFromConfig(config, HDFS_CONNECTOR_LOG_DIR, true);
        hdfsConnectorHiveIntegration = getStringFromConfig(config, HDFS_CONNECTOR_HIVE_INTEGRATION, true);
        hdfsConnectorHiveMetastoreUris = getStringFromConfig(config, HDFS_CONNECTOR_HIVE_METASTORE_URIS, true);
        hdfsConnectorSchemaCompatibility = getStringFromConfig(config, HDFS_CONNECTOR_SCHEMA_COMPATIBILITY, true);
        hdfsConnectorPartitionerClass = getStringFromConfig(config, HDFS_CONNECTOR_PARTITIONER_CLASS, true);
        hdfsConnectorLocale = getStringFromConfig(config, HDFS_CONNECTOR_LOCALE, true);
        hdfsConnectorTimezone = getStringFromConfig(config, HDFS_CONNECTOR_TIMEZONE, true);
    }

    public String getClusterSystemName() {
        return clusterSystemName;
    }

    public long getMessageTimeout() {
        return messageTimeout;
    }

    public List<String> getKafkaZkServers() {
        return kafkaZkServers;
    }

    public long getKafkaZkTimeout() {
        return kafkaZkTimeout;
    }

    public List<String> getKafkaBrokerServers() {
        return kafkaBrokerServers;
    }

    public int getKafkaBrokerJmxPort() {
        return kafkaBrokerJmxPort;
    }

    public String getKafkaSchemaServer() {
        return kafkaSchemaServer;
    }

    public List<String> getIngesterServers() {
        return ingesterServers;
    }

    public long getIngesterTimeout() {
        return ingesterTimeout;
    }

    public String getHdfsConnectorClass() {
        return hdfsConnectorClass;
    }

    public String getHdfsConnectorHdfsUrl() {
        return hdfsConnectorHdfsUrl;
    }

    public String getHdfsConnectorHadoopConfDir() {
        return hdfsConnectorHadoopConfDir;
    }

    public String getHdfsConnectorLogDir() {
        return hdfsConnectorLogDir;
    }

    public String getHdfsConnectorHiveIntegration() {
        return hdfsConnectorHiveIntegration;
    }

    public String getHdfsConnectorHiveMetastoreUris() {
        return hdfsConnectorHiveMetastoreUris;
    }

    public String getHdfsConnectorSchemaCompatibility() {
        return hdfsConnectorSchemaCompatibility;
    }

    public String getHdfsConnectorPartitionerClass() {
        return hdfsConnectorPartitionerClass;
    }

    public String getHdfsConnectorLocale() {
        return hdfsConnectorLocale;
    }

    public String getHdfsConnectorTimezone() {
        return hdfsConnectorTimezone;
    }

}
