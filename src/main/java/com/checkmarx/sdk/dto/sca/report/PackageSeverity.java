package com.checkmarx.sdk.dto.sca.report;

public enum PackageSeverity {
    /**
     * Package was scanned but no vulnerabilities were detected.
     */
    NONE,

    LOW,
    MEDIUM,
    HIGH
}
