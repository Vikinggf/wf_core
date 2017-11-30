package com.wf.core.web.base;

import com.wf.core.utils.MVCExceptionHandle;
import com.wf.core.web.response.BaseRspBean;
import com.wf.core.web.response.ErrorRspBean;
import com.wf.core.web.response.SuccessRspBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * 控制器支持类
 *
 * @author Ares
 * @version 2016-01-23
 */
public abstract class BaseController extends MVCExceptionHandle {
    public static final BaseRspBean SUCCESS = new SuccessRspBean<>(), ERROR = new ErrorRspBean();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * data
     *
     * @param data
     * @return
     */
    public SuccessRspBean<?> data(Object data) {
        return new SuccessRspBean<>(data);
    }

    /**
     * 错误
     *
     * @param message
     * @return
     */
    public ErrorRspBean error(String message) {
        return new ErrorRspBean(message);
    }

    /**
     * 错误
     *
     * @param message
     * @return
     */
    public ErrorRspBean error(int code, String message) {
        return new ErrorRspBean(code, message);
    }

    /**
     * 获取token
     *
     * @return
     */
    protected String getToken() {
        return getToken(getRequest());
    }

    /**
     * 获得Ip地址
     *
     * @return
     */
    protected String getIp() {
        return getRequest().getLocalAddr();
    }

    /**
     * 获取token
     *
     * @param request
     * @return
     */
    protected String getToken(ServletRequest request) {
        String token = getRequest().getHeader(OAuth.HeaderType.AUTHORIZATION);
        if (StringUtils.isBlank(token)) {
            throw new LbmOAuthException();
        } else {
            return token;
        }
    }


    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */


    /**
     * 获取token，不抛出未登录异常
     *
     * @return
     */
    protected String getTokenNoError() {
        return getRequest().getHeader(OAuth.HeaderType.AUTHORIZATION);
    }

    /**
     * 获取App-Version
     *
     * @param request
     * @return
     */
    protected String getAppVersion(ServletRequest request) {
        return ((HttpServletRequest) request).getHeader("App-Version");
    }

    /**
     * 获取App-Version
     *
     * @return
     */
    protected String getAppVersion() {
        return getAppVersion(getRequest());
    }


    protected int getVersion() {
        String version = getAppVersion();
        return getVersion(version);
    }

    protected int getVersion(String version) {
        if (StringUtils.isBlank(version)) {
            return -1;
        }
        String[] vs = version.split("\\.");
        int value = 0;
        try {
            for (String v : vs) {
                value = value * 100 + Integer.parseInt(v);
            }
        } catch (Exception e) {
            return -2;
        }
        return value;
    }

    /**
     * 获取request
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取渠道
     *
     * @param request
     * @return
     */
    protected String getAppChannel(ServletRequest request) {
        return getRequest().getHeader("App-Channel");
    }

    /**
     * 获取渠道
     *
     * @return
     */
    protected String getAppChannel() {
        return getAppChannel(getRequest());
    }


    public static class LbmOAuthException extends RuntimeException {
        private static final long serialVersionUID = 5067141585734438228L;
    }

    public static class ChannelErrorException extends RuntimeException {
        private static final long serialVersionUID = 7308727782750338596L;

        public ChannelErrorException() {
            super();
        }

        public ChannelErrorException(String message) {
            super(message);
        }
    }

    protected String appendUrlParam(String url, String param) {
        return url + (url.contains("?") ? "&" : "?") + param;
    }
}
