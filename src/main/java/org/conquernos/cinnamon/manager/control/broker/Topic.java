package org.conquernos.cinnamon.manager.control.broker;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;


public class Topic implements Serializable {

    private String name;

    private Properties configs;

    private List<Partition> partitions;

    public Topic(@JsonProperty("name") String name,
                 @JsonProperty("configs") Properties configs,
                 @JsonProperty("partitions") List<Partition> partitions) {
        this.name = name;
        this.configs = configs;
        this.partitions = partitions;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public Properties getConfigs() {
        return configs;
    }

    @JsonProperty
    public void setConfigs(Properties configs) {
        this.configs = configs;
    }

    @JsonProperty
    public List<Partition> getPartitions() {
        return partitions;
    }

    @JsonProperty
    public void setPartitions(List<Partition> partitions) {
        this.partitions = partitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Topic topic = (Topic) o;

        if (configs != null ? !configs.equals(topic.configs) : topic.configs != null) {
            return false;
        }
        if (name != null ? !name.equals(topic.name) : topic.name != null) {
            return false;
        }
        if (partitions != null ? !partitions.equals(topic.partitions) : topic.partitions != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (partitions != null ? partitions.hashCode() : 0);
        result = 31 * result + (configs != null ? configs.hashCode() : 0);
        return result;
    }
}
