package org.conquernos.cinnamon.manager.monitor.metrics;


import com.fasterxml.jackson.annotation.JsonGetter;


public class ResourceMetrics {

    private final int brokerId;
    private final float heapMemoryUsed;
    private final float heapMemoryMax;
    private final float cpuPercent;


    public ResourceMetrics(int brokerId, float heapMemoryUsed, float heapMemoryMax, float cpuPercent) {
        this.brokerId = brokerId;
        this.heapMemoryUsed = heapMemoryUsed;
        this.heapMemoryMax = heapMemoryMax;
        this.cpuPercent = cpuPercent;
    }

    @JsonGetter("brokerId")
    public int getBroker() {
        return brokerId;
    }

    @JsonGetter("heapMemoryUsed")
    public float getHeapMemoryUsed() {
        return heapMemoryUsed;
    }

    @JsonGetter("heapMemoryMax")
    public float getHeapMemoryMax() {
        return heapMemoryMax;
    }

    @JsonGetter("cpuPercent")
    public float getCpuPercent() {
        return cpuPercent;
    }

    @Override
    public String toString() {
        return "ResourceMetrics{" +
            "heapMemoryUsed=" + heapMemoryUsed +
            ", heapMemoryMax=" + heapMemoryMax +
            ", cpuPercent=" + cpuPercent +
            '}';
    }

}
