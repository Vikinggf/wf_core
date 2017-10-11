package com.jdd.lkcz.fm.core.utils;

import com.jdd.lkcz.game.api.response.ErrorRspBean;
import com.jdd.lkcz.game.api.web.BaseController.ChannelErrorException;
import com.jdd.lkcz.game.api.web.BaseController.LbmOAuthException;
import com.jdd.lkcz.game.common.beanvalidator.BeanValidators;
import com.jdd.lkcz.game.common.exception.BusinessCommonException;
import com.jdd.lkcz.game.common.exception.HttpClientException;
import org.apache.shiro.authz.UnauthorizedException;
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
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException me = (MethodArgumentNotValidException) e;
            StringBuilder sb = new StringBuilder();
            List<ObjectError> errors = me.getBindingResult().getAllErrors();
            if (errors.size() > 1)
                sb.append("共" + errors.size() + "个错误；");
            for (ObjectError error : errors)
                sb.append(error.getDefaultMessage()).append("，");
            sb.delete(sb.length() - 1, sb.length());
            String s = sb.toString();
            logger.info("参数非法：" + s);
            return new ErrorRspBean(400, s);
        } else if (e instanceof LbmOAuthException) {
            //        	response.setStatus(401);
            return new ErrorRspBean(401, "用户没有登录");
        } else if (e instanceof ChannelErrorException) {
            return new ErrorRspBean(402, "渠道不存在或被禁用");
        } else if (e instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException le = (HttpMessageNotReadableException) e;
            logger.info("请传入body:" + le.getMessage());
            return new ErrorRspBean(400, "请传入body");
        } else if (e instanceof BusinessCommonException) {
            BusinessCommonException le = (BusinessCommonException) e;
            logger.info("业务异常:" + le.getMsg());
            return new ErrorRspBean(le.getCode(), le.getMsg());
        } else if (e instanceof UnauthorizedException) {
            logger.warn(e.getMessage());
            Throwable t = e.getCause();
            if (t != null)
                logger.info(t.getMessage());
            return new ErrorRspBean(403, "禁止访问");
        } else if (e instanceof ConstraintViolationException) {
            List<String> list = BeanValidators.extractMessage((ConstraintViolationException) e);
            return new ErrorRspBean(400, list.toString());
        } else if (e instanceof HttpClientException) {
            return new ErrorRspBean(400, e.getMessage());
        } else {
            logger.error("系统异常", e);
            return new ErrorRspBean(500, "系统异常");
        }
    }
}
