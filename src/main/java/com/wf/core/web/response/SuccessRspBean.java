package com.wf.core.web.response;

public class SuccessRspBean<T> extends BaseRspBean {
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
