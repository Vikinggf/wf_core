package com.wf.core.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 获取TraceId的工具类
 * @author zwf
 */
public class TraceIdUtils {

	private static final String TRACE_ID = "traceId";

	private static final ThreadLocal<String> TRACE_ID_THREAD_LOCAL = new ThreadLocal<>();

	/**
	 * 获取HTTPServletRequest对象
	 * @return
	 */
	private static HttpServletRequest getRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			return null;
		}
		return ((ServletRequestAttributes) attributes).getRequest();
	}

	/**
	 * 获取当前traceId，优先从当前HTTP请求中获取，如请求中不存在，则自动生成一个并保存至当前HTTP请求作用域中，<br/>
     * 如当前线程不是HTTP请求线程，则从当前线程中获取，如未获取，则自动生成一个，并保存至当前线程中
	 * @return
	 */
	public static String getTraceId() {
		HttpServletRequest request = getRequest();
        if (request != null) {
            return getTraceIdFromRequest(request);
        }

        String traceId = TRACE_ID_THREAD_LOCAL.get();
        if (traceId != null) {
            return traceId;
        }

        traceId = UUID.randomUUID().toString().replaceAll("-", "");
        TRACE_ID_THREAD_LOCAL.set(traceId);
        return traceId;
    }

    private static String getTraceIdFromRequest(HttpServletRequest request) {
        Object obj = request.getAttribute(TRACE_ID);
        if (obj == null) {
            String traceId = UUID.randomUUID().toString().replaceAll("-", "");
            request.setAttribute(TRACE_ID, traceId);
            return traceId;
        }
        return obj.toString();
    }
}
