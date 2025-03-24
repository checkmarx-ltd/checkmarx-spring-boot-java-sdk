package com.checkmarx.sdk.dto.sast;

import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class Filter {
    private Type type;
    private String value;

    @RequiredArgsConstructor
    @Getter
    public enum Type {
        SEVERITY("SEVERITY"),
        CWE("CWE"),
        OWASP("OWASP"),

        // Filter by vulnerability type aka category aka name.
        TYPE("TYPE"),

        STATUS("STATUS"),
        STATE("STATE"),
        SCORE("SCORE"),

        POLICYVIOLATION("POLICYVIOLATION"),
        EXCLUDETYPE("EXCLUDETYPE"),
        EXCLUDECWE("EXCLUDECWE"),
        EXCLUDESTATE("EXCLUDESTATE");

        private final String value;
    }

    @RequiredArgsConstructor
    @Getter
    @XmlType(name="filterSeverity")
    public enum Severity {
        CRITICAL("Critical"),
        HIGH("High"),
        MEDIUM("Medium"),
        LOW("Low"),
        NONE("None"),
        INFO("Informational");

        private final String value;
    }
}
