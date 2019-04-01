package org.conquernos.cinnamon.utils.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;


public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final org.codehaus.jackson.map.ObjectMapper codehausMapper = new org.codehaus.jackson.map.ObjectMapper();

    public static JsonNode toJsonNode(String node) {
        if (node == null) return null;

        try {
            return mapper.readTree(node);
        } catch (IOException e) {
            e.printStackTrace();
            return new TextNode(node);
        }
    }

    public static ObjectNode toObjectNode(String node) {
        if (node == null) return null;

        try {
            return (ObjectNode) mapper.readTree(node);
        } catch (IOException e) {
            ObjectNode objectNode = createObjectNode();
            objectNode.set("none", new TextNode(node));
            return objectNode;
        }
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return mapper.createArrayNode();
    }

    public static org.codehaus.jackson.JsonNode toCodeHaus(JsonNode jsonNode) {
        if (jsonNode == null) return null;

        try {
            return codehausMapper.readTree(jsonNode.toString());
        } catch (IOException e) {
            return new org.codehaus.jackson.node.TextNode(jsonNode.toString());
        }
    }

}
