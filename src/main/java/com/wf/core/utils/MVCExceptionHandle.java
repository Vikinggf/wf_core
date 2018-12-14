package com.wf.core.utils;

import com.wf.core.utils.exception.BusinessCommonException;
import com.wf.core.web.base.BaseController;
import com.wf.core.web.response.ErrorRspBean;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * mvc异常工具
 *
 * @author Fe 2016年5月16日
 */
public class MVCExceptionHandle {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 捕捉异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(HttpServletResponse response, Exception e) {
        String traceId = TraceIdUtils.getTraceId();
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException me = (MethodArgumentNotValidException) e;
            StringBuilder sb = new StringBuilder();
            List<ObjectError> errors = me.getBindingResult().getAllErrors();
            if (errors.size() > 1) {
                sb.append("共" + errors.size() + "个错误；");
            }
            for (ObjectError error : errors) {
                sb.append(error.getDefaultMessage()).append("，");
            }
            sb.delete(sb.length() - 1, sb.length());
            logger.warn("参数非法 param={} ex={} traceId={}", sb.toString(), ExceptionUtils.getStackTrace(e), traceId);
            return new ErrorRspBean(400, "参数错误");
        } else if (e instanceof BaseController.LbmOAuthException) {
            logger.warn("用户没有登录: ex={}, traceId={}", ExceptionUtils.getStackTrace(e), traceId);
            return new ErrorRspBean(401, "登录信息已失效，请重新登录");
        } else if (e instanceof BaseController.ChannelErrorException) {
            logger.error("渠道不存在或被禁用 ex={}, traceId={}", ExceptionUtils.getStackTrace(e), traceId);
            return new ErrorRspBean(402, "渠道不存在或被禁用");
        } else if (e instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException le = (HttpMessageNotReadableException) e;
            logger.error("请传入body ex={} traceId={}", ExceptionUtils.getStackTrace(le), traceId);
            return new ErrorRspBean(400, "请传入body");
        } else if (e instanceof BusinessCommonException) {
            BusinessCommonException le = (BusinessCommonException) e;
            logger.error("业务异常 ex={} traceId={}", ExceptionUtils.getStackTrace(le), traceId);
            return new ErrorRspBean(le.getCode(), le.getMsg());
        }  else if (e instanceof ConstraintViolationException) {
            List<String> list = BeanValidators.extractMessage((ConstraintViolationException) e);
            logger.error("ex={}, traceId={}", list.toString() + ExceptionUtils.getStackTrace(e), traceId);
            return new ErrorRspBean(400, list.toString());
        } else {
            logger.error("ex={}, traceId={}", ExceptionUtils.getStackTrace(e), traceId);
            return new ErrorRspBean(500, "系统异常");
        }
    }
}
