package com.wf.core.web.response;

import java.io.Serializable;

public class SuccessRspBean<T> extends BaseRspBean implements Serializable {
    private T data;

    public SuccessRspBean() {
        super(200);
    }

    public SuccessRspBean(T data) {
        this();
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
