package com.checkmarx.sdk.exception;

/**
 * Created by Galn on 05/02/2018.
 */
public class CxHTTPClientException extends RuntimeException {
    private int statusCode = 0;
    private String responseBody;

    public CxHTTPClientException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public CxHTTPClientException() {
        super();
    }

    public CxHTTPClientException(String message) {
        super(message);
    }

    public CxHTTPClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CxHTTPClientException(Throwable cause) {
        super(cause);
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
