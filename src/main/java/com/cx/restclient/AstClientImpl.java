package com.cx.restclient;

import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.sca.CombinedResults;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.dto.sca.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.configuration.CxScanConfig;

import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.sca.dto.*;
import com.cx.restclient.sca.dto.report.ASTSummaryResults;
import com.cx.restclient.sca.dto.report.SummaryResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class AstClientImpl extends AbstractClientImpl {
    private static final String ERROR_PREFIX = "SCA scan cannot be initiated.";

    private final AstProperties astProperties;


    protected void applyScaResultsFilters(CombinedResults combinedResults) {
      //currently do nothing
    }

  
    /**
     * Convert Common Client representation of SCA results into an object from this SDK.
     */

    @Override
    protected CombinedResults toResults(ScanResults astResults) {
        validateNotNull(astResults.getAstResults());

        ASTSummaryResults summary = astResults.getAstResults().getSummary();
        Map<Filter.Severity, Integer> findingCountsPerSeverity = getFindingCountMap(summary);

        ModelMapper mapper = new ModelMapper();
        SCAResults result = mapper.map(astResults, SCAResults.class);
        result.getSummary().setFindingCounts(findingCountsPerSeverity);
        
        return new CombinedResults(result);
    }

    private void validateNotNull(ASTResults scaResults) {
        if (scaResults == null) {
            throw new ASTRuntimeException("SCA results are missing.");
        }

        SummaryResults summary = scaResults.getSummary();
        if (summary == null) {
            throw new ASTRuntimeException("SCA results don't contain a summary.");
        }
    }
    /**
     * Convert scaParams to an object that is used by underlying logic in Common Client.
     */
    protected CxScanConfig getScanConfig(ScanParams scanParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.setScannerType(ScannerType.AST);
        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scanParams.getProjectName());
        cxScanConfig.setAstConfig(getAstConfig(scanParams));

        return cxScanConfig;
    }

    private ASTConfig getAstConfig(ScanParams astParams) {
        ASTConfig astConfig = new ASTConfig();
        astConfig.setApiUrl(astProperties.getApiUrl());
        astConfig.setToken(astProperties.getToken());
        astConfig.setPreset(astProperties.getPreset());
        astConfig.setIncremental(astProperties.getIncremental());

        return astConfig;
    }



    protected void validate(ScanParams scaParams) {
        validateNotNull(scaParams);
         validateNotEmpty(astProperties.getApiUrl(), "AST API URL");
        validateNotEmpty(astProperties.getToken(), "AST Access Token");
        validateNotEmpty(astProperties.getPreset(), "AST preset");
        validateNotEmpty(astProperties.getIncremental(), "Is Incremental flag");
    }

    private void validateNotNull(ScanParams scaParams) {
        if (scaParams == null) {
            throw new ASTRuntimeException(String.format("%s SCA parameters weren't provided.", ERROR_PREFIX));
        }

        if (scaParams.getRemoteRepoUrl() == null) {
            throw new ASTRuntimeException(String.format("%s Repository URL wasn't provided.", ERROR_PREFIX));
        }
    }




}