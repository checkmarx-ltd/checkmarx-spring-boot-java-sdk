package com.checkmarx.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class FilterGo {
    private Type type;
    private String value;

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        SEVERITY("SEVERITY"),
        CWE("CWE"),
        OWASP("OWASP"),
        TYPE("TYPE"),
        STATUS("STATUS"),
        STATE("STATE"),
        SCORE("SCORE");

        private final String value;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Severity {
        CRITICAL("Critical"),
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low"),
        INFO("Informational");

        private final String value;
    }
}
