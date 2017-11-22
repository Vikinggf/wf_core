package com.wf.core.utils;

import org.apache.shiro.UnavailableSecurityManagerException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * IP工具
 *
 * @author Fe 2016年9月27日
 */
public class IPUtils {
    /**
     * 获取request
     * @returnr
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取客户端IP
     *
     * @return
     */
    public static String getRemoteAddress() {
        try {
            return getRemoteAddress(getRequest());
        } catch (UnavailableSecurityManagerException e) {
            return "127.0.0.1";
        }
    }

    /**
     * 获取客户端IP
     *
     * @param request
     * @return
     */
    public static String getRemoteAddress(HttpServletRequest request) {
        String ip = request.getHeader("Cdn-Src-Ip");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        String[] ips = ip.split(",");
        String trueIp = "";
        for (int i = 0; i < ips.length; i++) {
            if (!("unknown".equalsIgnoreCase(ips[i]))) {
                trueIp = ips[i];
                break;
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(trueIp)) {
            trueIp = "127.0.0.1";
        }
        return trueIp;
    }
}
