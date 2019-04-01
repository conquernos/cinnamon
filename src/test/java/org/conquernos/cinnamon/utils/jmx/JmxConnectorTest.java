package org.conquernos.cinnamon.utils.jmx;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.management.openmbean.CompositeDataSupport;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public class JmxConnectorTest {

    private JmxConnector jmxConnector;

    @BeforeClass
    public void setUp() throws IOException {
        jmxConnector = new JmxConnector("localhost", 8084);
    }

    @AfterClass
    public void tearDown() {
        jmxConnector.close();
    }

    @Test
    public void testQuery() throws Exception {

        Map<String, Object> result = jmxConnector.query("java.lang", "type", "Memory", "HeapMemoryUsage", "NonHeapMemoryUsage");
        System.out.println(Arrays.toString(((CompositeDataSupport)result.get("HeapMemoryUsage")).values().toArray()));
    }

}
