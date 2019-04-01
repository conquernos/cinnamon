package org.conquernos.cinnamon.manager.control.broker;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PartitionReplica {

    private int broker;
    private boolean leader;
    private boolean inSync;

    public PartitionReplica() {
    }

    public PartitionReplica(int broker, boolean leader, boolean inSync) {
        this.broker = broker;
        this.leader = leader;
        this.inSync = inSync;
    }

    @JsonProperty
    public int getBroker() {
        return broker;
    }

    @JsonProperty
    public void setBroker(int broker) {
        this.broker = broker;
    }

    @JsonProperty
    public boolean isLeader() {
        return leader;
    }

    @JsonProperty
    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    @JsonProperty("in_sync")
    public boolean isInSync() {
        return inSync;
    }

    @JsonProperty("in_sync")
    public void setInSync(boolean inSync) {
        this.inSync = inSync;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartitionReplica)) {
            return false;
        }

        PartitionReplica that = (PartitionReplica) o;

        if (broker != that.broker) {
            return false;
        }
        if (inSync != that.inSync) {
            return false;
        }
        if (leader != that.leader) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = broker;
        result = 31 * result + (leader ? 1 : 0);
        result = 31 * result + (inSync ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PartitionReplica{" +
            "broker=" + broker +
            ", leader=" + leader +
            ", inSync=" + inSync +
            '}';
    }
}
