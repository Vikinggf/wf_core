package com.wf.core.web.monitor.model;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class GcInfo {
    private String name;
    private String[] memoryPoolName;
    private Long collectionCount;
    private Double collectionTime;

    public Long getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(Long collectionCount) {
        this.collectionCount = collectionCount;
    }

    public Double getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(Double collectionTime) {
        this.collectionTime = collectionTime;
    }

    public String[] getMemoryPoolName() {
        return memoryPoolName;
    }

    public void setMemoryPoolName(String[] memoryPoolName) {
        this.memoryPoolName = memoryPoolName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
