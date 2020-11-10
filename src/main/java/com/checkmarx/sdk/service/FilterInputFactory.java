package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.cxgo.OdScanResultItem;
import com.checkmarx.sdk.dto.cxgo.SASTScanResult;
import com.checkmarx.sdk.dto.cxgo.SCAScanResult;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class FilterInputFactory {
    /**
     * Maps finding state ID (as returned in CxSAST report) to state name (as specified in filter configuration).
     */
    private static final Map<String, String> CXSAST_STATE_ID_TO_NAME = ImmutableMap.of(
            "0", "TO VERIFY",
            "2", "CONFIRMED",
            "3", "URGENT",
            "4", "PROPOSED NOT EXPLOITABLE"
    );

    private static final Map<Integer, SASTScanResult.State> CXGO_STATE_ID_TO_NAME =
            Arrays.stream(SASTScanResult.State.values())
            .collect(Collectors.toMap(SASTScanResult.State::getValue, Function.identity()));

    public FilterInput createFilterInputForCxSast(QueryType findingGroup, ResultType finding) {
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

    public FilterInput createFilterInputForSca(Finding scaFinding) {
        return FilterInput.builder()
                .id(scaFinding.getId())
                .category(scaFinding.getCveName())
                .cwe(scaFinding.getCveName())
                .severity(scaFinding.getSeverity().toString())
                .score(scaFinding.getScore())
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
