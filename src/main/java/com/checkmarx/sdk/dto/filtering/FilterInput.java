package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Map;

/**
 * Standardized input to {@link com.checkmarx.sdk.service.FilterValidator}, independent of specific scanner type.
 *
 * Some of the fields may not be initialized for a specific scanner type.
 */
@Builder
@Getter
@Setter
@Slf4j
public class FilterInput {
    /**
     * Maps finding state ID (as returned in CxSAST report) to state name (as specified in filter configuration).
     */
    private static final Map<String, String> STATE_ID_TO_NAME = ImmutableMap.of(
            "0", "TO VERIFY",
            "2", "CONFIRMED",
            "3", "URGENT",
            "4", "PROPOSED NOT EXPLOITABLE"
    );

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

    public static FilterInput getInstance(QueryType findingGroup, ResultType finding) {
        String stateName = STATE_ID_TO_NAME.get(finding.getState());

        return FilterInput.builder()
                .id(finding.getNodeId())
                .category(findingGroup.getName().toUpperCase(Locale.ROOT))
                .cwe(findingGroup.getCweId())
                .severity(findingGroup.getSeverity().toUpperCase(Locale.ROOT))
                .status(finding.getStatus().toUpperCase(Locale.ROOT))
                .state(stateName)
                .build();
    }
}