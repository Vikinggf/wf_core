package com.wf.core.sentinel;


import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.util.AppNameUtil;

/**
 * 类名称：MonitorSystemProperties
 * 类描述：
 * 开发人：朱水平【Tank】
 * 创建时间：2018/12/27.13:47
 * 修改备注：
 *
 * @version 1.0.0
 */

public class MonitorSystemProperties {

    private String server;

    private String port;

    private String projectName;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public MonitorSystemProperties(String server, String port, String projectName) {
        this.server = server;
        this.port = port;
        this.projectName = projectName;
        System.setProperty("project.name", projectName);
        AppNameUtil.resolveAppName();
        SentinelConfig.setConfig("csp.sentinel.dashboard.server", server);
        SentinelConfig.setConfig("csp.sentinel.api.port", port);
    }
}
