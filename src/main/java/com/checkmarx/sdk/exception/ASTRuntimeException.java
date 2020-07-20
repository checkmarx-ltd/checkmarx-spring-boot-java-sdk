package com.checkmarx.sdk.exception;

public class ASTRuntimeException extends RuntimeException {

    public ASTRuntimeException() {
    }

    public ASTRuntimeException(String message) {
        super(message);
    }

    public ASTRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}