package com.wf.core.utils.http;

import org.slf4j.Logger;

/**
 * 类名称：RequestConfig
 * 类描述：
 * 开发人：朱水平【Tank】
 * 创建时间：2018/12/5.10:33
 * 修改备注：
 *
 * @version 1.0.0
 */
public class RequestConfig {

    /**
     * 请求地址
     */
    private String url;
    /**
     * 记录日志
     */
    private Logger logger;

    /**
     * 请求方式
     */
    private HttpMethod httpMethod;

    /**
     * 连接超时
     */
    private int connectionTimeout;

    /**
     * 读超时
     */
    private int readTimeout;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public enum HttpMethod {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH,
        HEAD,
        OPTIONS;

        private HttpMethod() {
        }
    }
}
