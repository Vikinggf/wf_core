package com.wf.core.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取HttpRequest对象的工具类
 * @author zwf
 */
public class RequestUtils {
	/**
	 * 获取HTTPServletRequest对象。 必须在web.xml中配置org.springframework.web.context.request.RequestContextListener,
	 * 否则调用本方法将抛出NullPointerExcetion异常
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			throw new NullPointerException("org.springframework.web.context.request.RequestContextListener未在web.xml中配置");
		}
		return ((ServletRequestAttributes) attributes).getRequest();
	}
}
