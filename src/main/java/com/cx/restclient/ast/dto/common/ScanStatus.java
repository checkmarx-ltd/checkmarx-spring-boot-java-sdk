package com.cx.restclient.ast.dto.common;

public enum ScanStatus {
    // Some of the statuses are not used in code, but they help to prevent the "unknown status" warnings.
    CANCELED,
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED
}
