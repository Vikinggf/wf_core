package com.wf.core.sql.utils;

public class CdnException extends RuntimeException {
	private static final long serialVersionUID = 9190555528879302339L;

	public CdnException() {
        super();
    }
 
    public CdnException(String message) {
        super(message);
    }
    
    public CdnException(String message, Throwable e) {
    	super(message, e);
    }
}
