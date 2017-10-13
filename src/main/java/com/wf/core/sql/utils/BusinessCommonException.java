package com.wf.core.sql.utils;

public class BusinessCommonException extends RuntimeException{
	private static final long serialVersionUID = 524249241986303780L;
	private int code = 400; 
    private String msg; 
     
    public BusinessCommonException() {
        super();
    }
 
    public BusinessCommonException(String message) {
        super(message);
        msg = message;
    }
 
    public BusinessCommonException(int retCd, String msgDes) {
        this.code = retCd;
        this.msg = msgDes;
    }
    
    public BusinessCommonException(String msgDes, Throwable e) {
    	super(msgDes,e);
        this.msg = msgDes;
    }

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
