package com.wf.core.web.monitor.model;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class JvmHeapMemoryInfo {

    private Long initHeapMemory;
    private Long maxHeapMemory;
    private Long usedHeapMemory;
    private Long committedHeapMemory;
    private Double usedMaxPercent;

    public Long getInitHeapMemory() {
        return initHeapMemory;
    }

    public void setInitHeapMemory(Long initHeapMemory) {
        this.initHeapMemory = initHeapMemory;
    }

    public Long getMaxHeapMemory() {
        return maxHeapMemory;
    }

    public void setMaxHeapMemory(Long maxHeapMemory) {
        this.maxHeapMemory = maxHeapMemory;
    }

    public Long getUsedHeapMemory() {
        return usedHeapMemory;
    }

    public void setUsedHeapMemory(Long usedHeapMemory) {
        this.usedHeapMemory = usedHeapMemory;
    }

    public Long getCommittedHeapMemory() {
        return committedHeapMemory;
    }

    public void setCommittedHeapMemory(Long committedHeapMemory) {
        this.committedHeapMemory = committedHeapMemory;
    }

    public Double getUsedMaxPercent() {
        return usedMaxPercent;
    }

    public void setUsedMaxPercent(Double usedMaxPercent) {
        this.usedMaxPercent = usedMaxPercent;
    }
}
