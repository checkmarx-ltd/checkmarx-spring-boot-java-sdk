package com.checkmarx.sdk.exception;

public class ScannerRuntimeException extends RuntimeException {

    public ScannerRuntimeException() {
    }

    public ScannerRuntimeException(String message) {
        super(message);
    }

    public ScannerRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScannerRuntimeException(Throwable cause) {
            super(cause);
    }
}