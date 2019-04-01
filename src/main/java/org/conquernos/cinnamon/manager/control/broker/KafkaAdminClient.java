package org.conquernos.cinnamon.manager.control.broker;


import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.utils.config.Configuration;
import kafka.admin.AdminClient;


public class KafkaAdminClient {

    public static AdminClient create(MasterConfig config) {
//        Properties kafkaAdminProperties = new Properties();
//        kafkaAdminProperties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, Configuration.toListString(config.getKafkaBrokerServers()));
        return AdminClient.createSimplePlaintext(Configuration.toListString(config.getKafkaBrokerServers()));
    }

}
