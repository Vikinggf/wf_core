package com.wf.core.interceptor;

import com.wf.core.utils.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class TimeCostInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(TimeCostInterceptor.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("log-startTime", startTime);
        request.setAttribute("traceId", UUID.randomUUID().toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long startTime = (Long) request.getAttribute("log-startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        StringBuilder sb = new StringBuilder(1000);
        if (handler instanceof HandlerMethod) {
            sb.append("请求时间:").append(sdf.format(new Date())).append("\n");
            sb.append("请求耗时:").append(executeTime).append("\n");
            HandlerMethod h = (HandlerMethod) handler;
            sb.append("请求类:").append(h.getBean().getClass().getName()).append("\n");
            sb.append("请求方法:").append(h.getMethod().getName()).append("\n");
            sb.append("参数:").append(getParamString(request.getParameterMap())).append("\n");
            sb.append("请求地址:").append(request.getRequestURI()).append("\n");
            sb.append("traceId:").append(request.getAttribute("traceId")).append("\n");
            sb.append("用户IP :").append(IPUtils.getRemoteAddress(request)).append("\n");
            if (executeTime > 100) {
                logger.warn(sb.toString());
            } else {
                logger.info(sb.toString());
            }
        }
    }

    private String getParamString(Map<String, String[]> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> e : map.entrySet()) {
            sb.append(e.getKey()).append("=");
            String[] value = e.getValue();
            if (value != null && value.length == 1) {
                sb.append(value[0]).append("\t");
            } else {
                sb.append(Arrays.toString(value)).append("\t");
            }
        }
        return sb.toString();
    }

}
