package com.wf.core.web.response;

public class BaseRspBean {
    private int code;

    public BaseRspBean(int code) {
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
