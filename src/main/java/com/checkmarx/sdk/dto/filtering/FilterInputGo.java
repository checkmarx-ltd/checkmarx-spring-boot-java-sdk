package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.cxgo.OdScanResultItem;
import com.checkmarx.sdk.dto.cxgo.SASTScanResult;
import com.checkmarx.sdk.dto.cxgo.SCAScanResult;
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
public class FilterInputGo {
    private static final Map<Integer, SASTScanResult.State> STATE_ID_TO_NAME = Arrays.stream(SASTScanResult.State.values())
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

    public static FilterInputGo getInstance(SASTScanResult mainResultInfo, OdScanResultItem additionalResultInfo) {
        return FilterInputGo.builder()
                .id(mainResultInfo.getId().toString())
                .category(additionalResultInfo.getTitle().toUpperCase(Locale.ROOT))
                .cwe(mainResultInfo.getCwe())
                .severity(mainResultInfo.getSeverity().getSeverity())
                .status(mainResultInfo.getStatus().getStatus())
                .state(getStateName(mainResultInfo))
                .build();
    }

    public static FilterInputGo getInstance(SCAScanResult scaScanResult) {
        return FilterInputGo.builder()
                .id(scaScanResult.getId())
                .cwe(scaScanResult.getCwe())
                .severity(scaScanResult.getSeverity().getSeverity())
                .score(scaScanResult.getScore())
                .build();
    }

    private static String getStateName(SASTScanResult sastScanResult) {
        SASTScanResult.State state = STATE_ID_TO_NAME.get(sastScanResult.getState());
        if (state == null) {
            log.warn("Unknown state ID for a CxGO-SAST result: {}. The state will be ignored during filtering.",
                    sastScanResult.getState());
        }

        return state != null ? state.toString() : null;
    }
}