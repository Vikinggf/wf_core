/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wf.core.sentinel;


import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;

import java.util.Set;

/**
 * 类名称：ClusterClientModifyRequest
 * 类描述：
 * 开发人：朱水平【Tank】
 * 创建时间：2018/12/27.20:41
 * 修改备注：
 *
 * @version 1.0.0
 */
public class ClusterClientModifyRequest {

    private String app;
    private String ip;
    private Integer port;

    private Integer mode;
    private ServerFlowConfig flowConfig;
    private ServerTransportConfig transportConfig;
    private ClusterClientConfig clientConfig;
    private Set<String> namespaceSet;

    public ServerTransportConfig getTransportConfig() {
        return transportConfig;
    }

    public void setTransportConfig(ServerTransportConfig transportConfig) {
        this.transportConfig = transportConfig;
    }

    public Set<String> getNamespaceSet() {
        return namespaceSet;
    }

    public void setNamespaceSet(Set<String> namespaceSet) {
        this.namespaceSet = namespaceSet;
    }

    public String getApp() {
        return app;
    }

    public ClusterClientModifyRequest setApp(String app) {
        this.app = app;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public ClusterClientModifyRequest setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public ClusterClientModifyRequest setPort(Integer port) {
        this.port = port;
        return this;
    }

    public Integer getMode() {
        return mode;
    }

    public ClusterClientModifyRequest setMode(Integer mode) {
        this.mode = mode;
        return this;
    }

    public ServerFlowConfig getFlowConfig() {
        return flowConfig;
    }

    public void setFlowConfig(ServerFlowConfig flowConfig) {
        this.flowConfig = flowConfig;
    }

    public ClusterClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(ClusterClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
