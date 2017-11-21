package com.wf.core.web.monitor.model;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class JvmNonHeapMemoryInfo {

    private Long initNonHeapMemory;
    private Long maxNonHeapMemory;
    private Long usedNonHeapMemory;
    private Long committedHeapMemory;

    public Long getInitNonHeapMemory() {
        return initNonHeapMemory;
    }

    public void setInitNonHeapMemory(Long initNonHeapMemory) {
        this.initNonHeapMemory = initNonHeapMemory;
    }

    public Long getMaxNonHeapMemory() {
        return maxNonHeapMemory;
    }

    public void setMaxNonHeapMemory(Long maxNonHeapMemory) {
        this.maxNonHeapMemory = maxNonHeapMemory;
    }

    public Long getUsedNonHeapMemory() {
        return usedNonHeapMemory;
    }

    public void setUsedNonHeapMemory(Long usedNonHeapMemory) {
        this.usedNonHeapMemory = usedNonHeapMemory;
    }

    public Long getCommittedHeapMemory() {
        return committedHeapMemory;
    }

    public void setCommittedHeapMemory(Long committedHeapMemory) {
        this.committedHeapMemory = committedHeapMemory;
    }
}
