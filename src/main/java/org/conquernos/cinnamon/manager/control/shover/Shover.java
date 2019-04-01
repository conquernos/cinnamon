package org.conquernos.cinnamon.manager.control.shover;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Shover {

    private String id;
    private String host;
    private String address;
    private List<String> topics;


    @JsonCreator
    public Shover(@JsonProperty("id") String id, @JsonProperty("host") String host, @JsonProperty("topics") List<String> topics) {
        this.id = id;
        this.host = host;
        this.address = getAddress();
        this.topics = topics;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "Shover{" +
            "id='" + id + '\'' +
            ", host='" + host + '\'' +
            ", address='" + address + '\'' +
            ", topics=" + topics +
            '}';
    }

}
