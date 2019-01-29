package com.wf.core.sentinel;

import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wf.core.log.LogExceptionStackTrace;
import com.wf.core.utils.IpGetter;
import com.wf.core.utils.type.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * 类名称：MonitorConfig
 * 类描述：限流工具类。主要是用来做限流
 * 开发人：朱水平【Tank】
 * 创建时间：2018/12/20.19:33
 * 修改备注：
 *
 * @version 1.0.0
 */

public class MonitorConfig {
    public static final String GROUP_ID = "/sentinel";
    public static final String FLOW_RULES = "/rules/flow-rules";
    public static final String DEGRADE_RULES="/rules/degrade-rules";

    /**
     * ZK地址
     */
    private String serverAddr;

    /**
     * 项目名称
     */
    private String projectName;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MonitorConfig(String serverAddr, String projectName) {
        this.serverAddr = serverAddr;
        this.projectName = projectName;
        init(serverAddr,projectName);
    }


    public void init(String serverAddr, String projectName) {
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds =
                    new ZookeeperDataSource<>(serverAddr, getPath(projectName,FLOW_RULES),
                            source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                            }));
            return ds.getProperty();
        });

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource =
                new ZookeeperDataSource<>(serverAddr, getPath(projectName,FLOW_RULES),
                        source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                        }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());

        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource =
                new ZookeeperDataSource<>(serverAddr, getPath(projectName,DEGRADE_RULES),
                        source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                        }));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
        ZookeeperDataSource<ClusterClientModifyRequest> clusterServerModifyRequest =
                new ZookeeperDataSource<>(serverAddr, getPath(projectName, IpGetter.getIp()),
                        source -> JSON.parseObject(source, new TypeReference<ClusterClientModifyRequest>() {
                        }));
        try {
            String requestString = clusterServerModifyRequest.readSource();
            if (StringUtils.isNotBlank(requestString)) {
                ClusterClientModifyRequest clusterClientModifyRequest = JSON.parseObject(requestString, ClusterClientModifyRequest.class);
                if (clusterClientModifyRequest.getClientConfig() != null) {
                    ClusterClientConfigManager.applyNewConfig(clusterClientModifyRequest.getClientConfig());
                } else {
                    SentinelProperty<Set<String>> property3 = new DynamicSentinelProperty<>(clusterClientModifyRequest.getNamespaceSet());
                    ClusterServerConfigManager.registerNamespaceSetProperty(property3);
                    ClusterServerConfigManager.loadGlobalTransportConfig(clusterClientModifyRequest.getTransportConfig());
                    ClusterServerConfigManager.loadGlobalFlowConfig(clusterClientModifyRequest.getFlowConfig());
                }
                ClusterStateManager.applyState(clusterClientModifyRequest.getMode());
            }
        } catch (Exception e) {
            logger.error("monitor 集群初始失败", LogExceptionStackTrace.erroStackTrace(e));
        }
    }

    private static String getPath(String groupId, String dataId) {
        String path = GROUP_ID;
        if (groupId.startsWith("/")) {
            path += groupId;
        } else {
            path += "/" + groupId;
        }
        if (StringUtil.isNotBlank(dataId)){
            if (dataId.startsWith("/")) {
                path += dataId;
            } else {
                path += "/" + dataId;
            }
        }
        return path;
    }

}
