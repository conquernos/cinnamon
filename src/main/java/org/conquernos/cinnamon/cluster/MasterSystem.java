package org.conquernos.cinnamon.cluster;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.cluster.singleton.ClusterSingletonProxy;
import akka.cluster.singleton.ClusterSingletonProxySettings;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import com.typesafe.config.Config;


public class MasterSystem {

    public static final String CINNAMON_ROLE = "cinnamon";
    public static final String CINNAMON_MASTER_NAME = "cinnamonMaster";
    public static final String CINNAMON_MASTER_PATH = "/user/cinnamonMaster";


    public static void start(Config config) {
        MasterConfig masterConfig = (MasterConfig) ConfigLoader.build(MasterConfig.class, config);

        ActorSystem system = ActorSystem.create(masterConfig.getClusterSystemName(), config);

        // singleton manager
        ClusterSingletonManagerSettings settings = ClusterSingletonManagerSettings.create(system).withRole(CINNAMON_ROLE);
        system.actorOf(ClusterSingletonManager.props(Props.create(Master.class, masterConfig)
            , PoisonPill.getInstance(), settings), CINNAMON_MASTER_NAME);

        // singleton proxy
        ClusterSingletonProxySettings proxySettings = ClusterSingletonProxySettings.create(system).withRole(CINNAMON_ROLE);
        system.actorOf(ClusterSingletonProxy.props(CINNAMON_MASTER_PATH, proxySettings), CINNAMON_MASTER_NAME + "Proxy");
    }

}
