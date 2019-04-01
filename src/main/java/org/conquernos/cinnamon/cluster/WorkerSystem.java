package org.conquernos.cinnamon.cluster;

import akka.actor.ActorSystem;
import org.conquernos.cinnamon.config.MasterConfig;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import com.typesafe.config.Config;


public class WorkerSystem {

    public static final String CINNAMON_ROLE = "cinnamon";
    public static final String CINNAMON_MASTER_NAME = "cinnamonMaster";
    public static final String CINNAMON_MASTER_PATH = "/user/cinnamonMaster";


    public static void start(Config config) {
        MasterConfig masterConfig = (MasterConfig) ConfigLoader.build(MasterConfig.class, config);

        ActorSystem system = ActorSystem.create(masterConfig.getClusterSystemName(), config);
    }

}
