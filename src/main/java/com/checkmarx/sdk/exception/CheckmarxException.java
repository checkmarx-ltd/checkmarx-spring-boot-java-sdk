package com.checkmarx.sdk.exception;

import java.util.Map;

public class CheckmarxException extends Exception {

    private Map<String, String> metadata;

    public CheckmarxException(){ super(); }

    public CheckmarxException(String message) {
        super(message);
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
