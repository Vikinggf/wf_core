package com.wf.core.utils.exception;

/**
 * Created by chris on 2017/6/23.
 */

public class HttpClientException extends RuntimeException {
    private static final long serialVersionUID = -8767489679043116606L;

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable e) {
        super(message, e);
    }
}
