package org.conquernos.cinnamon.utils.process;


import java.io.*;
import java.lang.management.ManagementFactory;

public class Process {

    public static int getProcessId() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int idx;
        String pid = null;
        if ((idx = jvmName.indexOf("@")) > 0) {
            pid = jvmName.substring(0, idx);
        }
        if (pid != null) {
            try {
                return Integer.parseInt(pid);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static boolean saveProcessId(String fileName) {
        PrintWriter pidWriter = null;
        try {
            pidWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
            pidWriter.print(getProcessId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (pidWriter != null) {
                pidWriter.close();
            }
        }
    }

}
