package com.wf.core.web.response;

import java.io.Serializable;

public class BaseRspBean implements Serializable {
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
