package com.checkmarx.sdk.exception;

/**
 * Created by Galn on 19/03/2018.
 */
public class CxTokenExpiredException extends RuntimeException {

    public CxTokenExpiredException() {
        super();
    }

    public CxTokenExpiredException(String message) {
        super(message);
    }

    public CxTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public CxTokenExpiredException(Throwable cause) {
        super(cause);
    }
}
