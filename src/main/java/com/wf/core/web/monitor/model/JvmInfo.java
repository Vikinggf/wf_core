package com.wf.core.web.monitor.model;


import java.util.List;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class JvmInfo {
    private String JvmName;
    private String JvmVersion;
    private List<String> inputArguments;
    private Long totalMemory;
    private Long freeMemory;
    private Long maxMemory;
    private Double totalCompilationTime;
    private String compilationName;

    private JvmHeapMemoryInfo jvmHeapMemoryInfo;
    private JvmNonHeapMemoryInfo jvmNonHeapMemoryInfo;
    private List<JvmMemoryPoolInfo> jvmMemoryPoolInfoList;
    private ThreadInfo threadInfo;
    private OsInfo osInfo;
    private List<GcInfo> gcInfoList;

    public String getCompilationName() {
        return compilationName;
    }

    public void setCompilationName(String compilationName) {
        this.compilationName = compilationName;
    }

    public Long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public List<GcInfo> getGcInfoList() {
        return gcInfoList;
    }

    public void setGcInfoList(List<GcInfo> gcInfoList) {
        this.gcInfoList = gcInfoList;
    }

    public List<String> getInputArguments() {
        return inputArguments;
    }

    public void setInputArguments(List<String> inputArguments) {
        this.inputArguments = inputArguments;
    }

    public JvmHeapMemoryInfo getJvmHeapMemoryInfo() {
        return jvmHeapMemoryInfo;
    }

    public void setJvmHeapMemoryInfo(JvmHeapMemoryInfo jvmHeapMemoryInfo) {
        this.jvmHeapMemoryInfo = jvmHeapMemoryInfo;
    }

    public List<JvmMemoryPoolInfo> getJvmMemoryPoolInfoList() {
        return jvmMemoryPoolInfoList;
    }

    public void setJvmMemoryPoolInfoList(List<JvmMemoryPoolInfo> jvmMemoryPoolInfoList) {
        this.jvmMemoryPoolInfoList = jvmMemoryPoolInfoList;
    }

    public String getJvmName() {
        return JvmName;
    }

    public void setJvmName(String jvmName) {
        JvmName = jvmName;
    }

    public JvmNonHeapMemoryInfo getJvmNonHeapMemoryInfo() {
        return jvmNonHeapMemoryInfo;
    }

    public void setJvmNonHeapMemoryInfo(JvmNonHeapMemoryInfo jvmNonHeapMemoryInfo) {
        this.jvmNonHeapMemoryInfo = jvmNonHeapMemoryInfo;
    }

    public String getJvmVersion() {
        return JvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        JvmVersion = jvmVersion;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(Long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public OsInfo getOsInfo() {
        return osInfo;
    }

    public void setOsInfo(OsInfo osInfo) {
        this.osInfo = osInfo;
    }

    public ThreadInfo getThreadInfo() {
        return threadInfo;
    }

    public void setThreadInfo(ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
    }

    public Double getTotalCompilationTime() {
        return totalCompilationTime;
    }

    public void setTotalCompilationTime(Double totalCompilationTime) {
        this.totalCompilationTime = totalCompilationTime;
    }

    public Long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Long totalMemory) {
        this.totalMemory = totalMemory;
    }
}
