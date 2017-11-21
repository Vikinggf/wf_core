package com.wf.core.web.monitor.model;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class JvmMemoryPoolInfo {
    private String name;
    private String type;
    private String usage;
    private String peakUsage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getPeakUsage() {
        return peakUsage;
    }

    public void setPeakUsage(String peakUsage) {
        this.peakUsage = peakUsage;
    }
}
