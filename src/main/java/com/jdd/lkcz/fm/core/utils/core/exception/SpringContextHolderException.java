package com.jdd.lkcz.fm.core.utils.core.exception;

public class SpringContextHolderException extends RuntimeException {

    private static final long serialVersionUID = -2882495860358955626L;

    public SpringContextHolderException() {
    }

    public SpringContextHolderException(String meesage) {
        super(meesage);
    }

    public SpringContextHolderException(String message, Throwable cause) {
        super(message, cause);
    }
}
