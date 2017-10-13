package com.wf.core.idg;

/**
 * idg异常
 * @author Fe
 * @version 2015年12月22日 <strong>1.0</strong>
 */
public class IDGeneratorException extends RuntimeException {
	private static final long serialVersionUID = -5127295729556115866L;

	public IDGeneratorException(){
		super();
	}
	
	public IDGeneratorException(String message){
		super(message);
	}
	
	public IDGeneratorException(Throwable e){
		super(e);
	}
	
	public IDGeneratorException(String message, Throwable e){
		super(message, e);
	}

}
