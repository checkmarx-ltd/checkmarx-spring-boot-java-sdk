package com.checkmarx.sdk.exception;

public class SCARuntimeException extends RuntimeException {

    public SCARuntimeException() {
    }

    public SCARuntimeException(String message) {
        super(message);
    }

    public SCARuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}