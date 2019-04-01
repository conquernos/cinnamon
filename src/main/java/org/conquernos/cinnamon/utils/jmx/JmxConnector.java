package org.conquernos.cinnamon.utils.jmx;


import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class JmxConnector {

    private final JMXConnector jmxConnector;
    private final MBeanServerConnection mbeanConnection;


    public JmxConnector(String host, int port) throws IOException, SecurityException {
        String url = String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);

        jmxConnector = JMXConnectorFactory.connect(new JMXServiceURL(url));
        mbeanConnection = jmxConnector.getMBeanServerConnection();
    }

    public Map<String, Object> query(String domain, Hashtable<String, String> keyValues, String... attributeNames) {
        Map<String, Object> attributes = new HashMap<>(attributeNames.length);
        try {
            ObjectName objectName = new ObjectName(domain, keyValues);
            AttributeList attrs = mbeanConnection.getAttributes(objectName, attributeNames);
            attrs.asList().forEach(attr -> attributes.put(attr.getName(), attr.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return attributes;
    }


    public Map<String, Object> query(String domain, String key, String value, String... attributeNames) {
        Map<String, Object> attributes = new HashMap<>(attributeNames.length);
        try {
            ObjectName objectName = new ObjectName(domain, key, value);
            AttributeList attrs = mbeanConnection.getAttributes(objectName, attributeNames);
            attrs.asList().forEach(attr -> attributes.put(attr.getName(), attr.getValue()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return attributes;
    }

    public Map<String, Object> queryType(String domain, String type, String... attributeNames) {
        return query(domain, "type", type);
    }

    public Map<String, Object> queryTypeAndName(String domain, String type, String name, String... attributeNames) {
        Hashtable<String, String> keyValues = new Hashtable<>(2);
        keyValues.put("type", type);
        keyValues.put("name", name);
        return query(domain, keyValues, attributeNames);
    }

    public Map<String, Object> queryTypeAndNameAndRequest(String domain, String type, String name, String request, String... attributeNames) {
        Hashtable<String, String> keyValues = new Hashtable<>(3);
        keyValues.put("type", type);
        keyValues.put("name", name);
        keyValues.put("request", request);
        return query(domain, keyValues, attributeNames);
    }

    public void close() {
        try {
            jmxConnector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
