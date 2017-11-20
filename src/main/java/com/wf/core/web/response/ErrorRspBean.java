package com.wf.core.web.response;

import java.io.Serializable;

/**
 * @author Huguanghui
 * @Date 2016-03-14
 */
public class ErrorRspBean extends BaseRspBean implements Serializable {
    private String message;

    public ErrorRspBean() {
        this("请求错误");
    }

    public ErrorRspBean(String message) {
        this(101, message);
    }

    public ErrorRspBean(int code, String message) {
        super(code);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
