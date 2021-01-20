package com.cx.restclient.dto;

public enum ScannerType {
    // Legacy scanners.
    SAST("CxSAST"),
    OSA("CxOSA"),

    // Scan engines of the new CxAST platform.
    AST_SCA("CxAST-SCA"),
    AST_SAST("CxAST-SAST");

    private final String displayName;

    ScannerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
