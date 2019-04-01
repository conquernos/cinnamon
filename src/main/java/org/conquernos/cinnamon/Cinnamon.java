package org.conquernos.cinnamon;


import org.conquernos.cinnamon.cluster.MasterSystem;
import org.conquernos.cinnamon.cluster.WorkerSystem;
import org.conquernos.cinnamon.utils.config.ConfigLoader;
import org.conquernos.cinnamon.utils.process.Process;

public class Cinnamon {

    public static void main(String[] args) {
        if (args.length < 3) System.exit(0);

        String configFilePath = args[0];
        String pidFileName = args[1];
        String masterOrWorker = args[2];

        Process.saveProcessId(pidFileName);

        if (masterOrWorker.equalsIgnoreCase("master")) {
            MasterSystem.start(ConfigLoader.load(configFilePath));
        } else if (masterOrWorker.equalsIgnoreCase("worker")) {
            WorkerSystem.start(ConfigLoader.load(configFilePath));
        }
    }

}
