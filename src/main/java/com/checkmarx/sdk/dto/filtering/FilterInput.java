package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.cxgo.OdScanResultItem;
import com.checkmarx.sdk.dto.cxgo.SASTScanResult;
import com.checkmarx.sdk.dto.cxgo.SCAScanResult;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private static final Map<String, String> CXSAST_STATE_ID_TO_NAME = ImmutableMap.of(
            "0", "TO VERIFY",
            "2", "CONFIRMED",
            "3", "URGENT",
            "4", "PROPOSED NOT EXPLOITABLE"
    );

    private static final Map<Integer, SASTScanResult.State> CXGO_STATE_ID_TO_NAME = Arrays.stream(SASTScanResult.State.values())
            .collect(Collectors.toMap(SASTScanResult.State::getValue, Function.identity()));

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

    /**
     * Creates FilterInput based on CxSAST result.
     */
    public static FilterInput getInstance(QueryType findingGroup, ResultType finding) {
        String stateName = CXSAST_STATE_ID_TO_NAME.get(finding.getState());

        return FilterInput.builder()
                .id(finding.getNodeId())
                .category(findingGroup.getName().toUpperCase(Locale.ROOT))
                .cwe(findingGroup.getCweId())
                .severity(findingGroup.getSeverity().toUpperCase(Locale.ROOT))
                .status(finding.getStatus().toUpperCase(Locale.ROOT))
                .state(stateName)
                .build();
    }

    public static FilterInput getInstance(Finding scaFinding) {
        return FilterInput.builder()
                .id(scaFinding.getId())
                .category(scaFinding.getCveName())
                .cwe(scaFinding.getCveName())
                .severity(scaFinding.getSeverity().toString())
                .score(scaFinding.getScore())
                .build();
    }

    public static FilterInput getInstance(SASTScanResult mainResultInfo, OdScanResultItem additionalResultInfo) {
        return FilterInput.builder()
                .id(mainResultInfo.getId().toString())
                .category(additionalResultInfo.getTitle().toUpperCase(Locale.ROOT))
                .cwe(mainResultInfo.getCwe())
                .severity(mainResultInfo.getSeverity().getSeverity())
                .status(mainResultInfo.getStatus().getStatus())
                .state(getCxGoSastStateName(mainResultInfo))
                .build();
    }

    public static FilterInput getInstance(SCAScanResult scaScanResult) {
        return FilterInput.builder()
                .id(scaScanResult.getId())
                .cwe(scaScanResult.getCwe())
                .severity(scaScanResult.getSeverity().getSeverity())
                .score(scaScanResult.getScore())
                .build();
    }

    private static String getCxGoSastStateName(SASTScanResult sastScanResult) {
        SASTScanResult.State state = CXGO_STATE_ID_TO_NAME.get(sastScanResult.getState());
        if (state == null) {
            log.warn("Unknown state ID for a CxGO-SAST result: {}. The state will be ignored during filtering.",
                    sastScanResult.getState());
        }

        return state != null ? state.toString() : null;
    }
}