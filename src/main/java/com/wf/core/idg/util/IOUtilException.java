package com.wf.core.idg.util;

/**
 * IO工具产生的异常
 * @author Fe
 * @version 2013-7-14 <strong>1.0</strong>
 */
public class IOUtilException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IOUtilException(){
		super();
	}
	
	public IOUtilException(String message){
		super(message);
	}
	
	public IOUtilException(Throwable e){
		super(e);
	}
	
	public IOUtilException(String message, Throwable e){
		super(message, e);
	}
}
