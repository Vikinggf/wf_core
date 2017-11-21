package com.wf.core.web.monitor.model;

/**
 * @author pdl
 * @date 2016/10/27
 * @Description
 **/

public class ThreadInfo {
    private int threadCount;
    private int peakThreadCount;
    private Double currentThreadCpuTime;
    private Double currentThreadUserTime;
    private int daemonThreadCount;

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public Double getCurrentThreadCpuTime() {
        return currentThreadCpuTime;
    }

    public void setCurrentThreadCpuTime(Double currentThreadCpuTime) {
        this.currentThreadCpuTime = currentThreadCpuTime;
    }

    public Double getCurrentThreadUserTime() {
        return currentThreadUserTime;
    }

    public void setCurrentThreadUserTime(Double currentThreadUserTime) {
        this.currentThreadUserTime = currentThreadUserTime;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }
}
