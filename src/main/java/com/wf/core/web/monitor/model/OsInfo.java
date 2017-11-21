package com.wf.core.web.monitor.model;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class OsInfo {
    private String architecture;
    private String name;
    private String version;
    private Integer osProcessors;

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getOsProcessors() {
        return osProcessors;
    }

    public void setOsProcessors(Integer osProcessors) {
        this.osProcessors = osProcessors;
    }
}
