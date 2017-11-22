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

	/**
	 * 获取HTTPServletRequest对象
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			throw new NullPointerException("org.springframework.web.context.request.RequestContextListener未在web.xml中配置");
		}
		return ((ServletRequestAttributes) attributes).getRequest();
	}

	/**
	 * 获取当前请求中的traceId，如请求中没有则自动生成一个
	 * @return
	 */
	public static String getTraceId() {
		Object obj = getRequest().getAttribute(TRACE_ID);
		if (obj == null) {
			String traceId = UUID.randomUUID().toString();
			getRequest().setAttribute(TRACE_ID, traceId);
			return traceId;
		}
		return obj.toString();
	}
}
