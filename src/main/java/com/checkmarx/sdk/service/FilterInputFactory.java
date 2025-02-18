package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.cxgo.OdScanResultItem;
import com.checkmarx.sdk.dto.cxgo.SASTScanResult;
import com.checkmarx.sdk.dto.cxgo.SCAScanResult;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transforms findings from various vulnerability scanners into a standard FilterInput object.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FilterInputFactory {

    private final CxProperties cxProperties;

    private static final Map<Integer, SASTScanResult.State> CXGO_STATE_ID_TO_NAME =
            Arrays.stream(SASTScanResult.State.values())
            .collect(Collectors.toMap(SASTScanResult.State::getValue, Function.identity()));

    public FilterInput createFilterInputForCxSast(QueryType findingGroup, ResultType finding) {
        String stateName = cxProperties.getStateFullName(finding.getState());
        String severityName = cxProperties.getSeverityFullName(finding.getSeverityIndex());

        return FilterInput.builder()
                .id(finding.getNodeId())
                .category(findingGroup.getName().toUpperCase(Locale.ROOT))
                .cwe(findingGroup.getCweId())
                .severity(severityName)
                .status(finding.getStatus().toUpperCase(Locale.ROOT))
                .state(stateName)
                .build();
    }

    public FilterInput createFilterInputForSca(Finding scaFinding) {
        return FilterInput.builder()
                .id(scaFinding.getId())
                .category(scaFinding.getCveName())
                .cwe(scaFinding.getCveName())
                .severity(scaFinding.getSeverity().toString())
                .score(scaFinding.getScore())
                .policyViolation(BooleanUtils.toStringTrueFalse(scaFinding.isViolatingPolicy()))
                .build();
    }

    public FilterInput createFilterInputForCxGoSast(SASTScanResult mainResultInfo, OdScanResultItem additionalResultInfo) {
        return FilterInput.builder()
                .id(mainResultInfo.getId().toString())
                .category(additionalResultInfo.getTitle().toUpperCase(Locale.ROOT))
                .cwe(mainResultInfo.getCwe())
                .severity(mainResultInfo.getSeverity().getSeverity())
                .status(mainResultInfo.getStatus().getStatus())
                .state(getCxGoSastStateName(mainResultInfo))
                .build();
    }

    public FilterInput createFilterInputForCxGoSca(SCAScanResult scaScanResult) {
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
