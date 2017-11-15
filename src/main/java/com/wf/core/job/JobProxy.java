package com.wf.core.job;

import com.wf.core.utils.core.SpringContextHolder;
import com.wf.core.utils.type.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * job代理类，用于从Spring中获取真正的job实例，并执行其中的方法。
 * @author zhuweifeng
 */
public class JobProxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobProxy.class);

    /**
     * 此方法被任务统一调度中心调用
     * @param jobName 包含执行任务的类和方法,以点分割。例如com.wf.GameInitJob.execute表示执行gameInitJob类的execute方法
     * @throws Exception
     */
    public void execute(String jobName) throws Exception {
        if (StringUtils.isBlank(jobName)) {
            LOGGER.error("jobName is required.");
            return;
        }

        jobName = jobName.trim();
        String className = getClass(jobName);
        String methodName = getMethod(jobName);

        Class cls = Class.forName(className);
        Object obj = SpringContextHolder.getBean(cls);
        if (obj == null) {
            throw new NullPointerException("从Spring中获取实例" + cls.getSimpleName() + "失败");
        }
        Method method = cls.getMethod(methodName);
        method.invoke(obj);
    }

    private String getMethod(String str) {
        return str.substring(str.lastIndexOf(".") + 1);
    }

    private String getClass(String str) {
        return str.substring(0, str.lastIndexOf("."));
    }
}