package com.checkmarx.sdk.dto.filtering;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Standardized input to {@link com.checkmarx.sdk.service.FilterValidator}, independent of specific scanner type.
 *
 * Some of the fields may not be initialized for a specific scanner type.
 */
@Builder
@Getter
@Setter
public class FilterInput {
    private final String id;

    /**
     * This field is also known as 'title' or 'query name', depending on a vulnerability scanner.
     * Sample value: "SQL_Injection".
     */
    private final String category;

    private final String cwe;
    private final String severity;
    private final String status;
    private final String state;
    private final Double score;
}