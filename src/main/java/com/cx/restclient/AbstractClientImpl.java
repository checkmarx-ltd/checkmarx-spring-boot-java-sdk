package com.cx.restclient;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.checkmarx.sdk.service.AstClient;
import com.cx.restclient.ast.dto.common.SummaryResults;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
public abstract class AbstractClientImpl implements AstClient {
    
    private static final int SCA_SCAN_INTERVAL_IN_SECONDS = 5;
    protected static final String ERROR_PREFIX = "SCA scan cannot be initiated.";

    public ASTResultsWrapper scanRemoteRepo(ScanParams scanParams) throws IOException {
        validate(scanParams);

        CxScanConfig scanConfig = getScanConfig(scanParams);
        ScanResults scanResults = executeScan(scanConfig);

        ASTResultsWrapper scaResults = toResults(scanResults);
        applyScaResultsFilters(scaResults);

        return scaResults;
    }

    @Override
    public ASTResultsWrapper scanLocalSource(ScanParams scanParams) throws IOException {
        validate(scanParams);

        CxScanConfig scanConfig = getScanConfig(scanParams);
        scanConfig.setZipFile(new File(scanParams.getZipPath()));
        scanConfig.setOsaProgressInterval(SCA_SCAN_INTERVAL_IN_SECONDS);
        /*
        TODO
        ...
        LOGIC for Resolver functionality (package manager)
        ...
         */
        ScanResults scanResults = executeScan(scanConfig);

        ASTResultsWrapper scaResults = toResults(scanResults);
        applyScaResultsFilters(scaResults);

        return scaResults;
    }
    
    protected abstract void applyScaResultsFilters(ASTResultsWrapper scaResults);

    protected abstract ASTResultsWrapper toResults(ScanResults scanResults);

    protected ScanResults executeScan(CxScanConfig cxScanConfig) throws IOException {
        CxClientDelegator client = new CxClientDelegator(cxScanConfig, log);
        client.init();
        client.initiateScan();

        return client.waitForScanResults();
    }

    protected Map<Filter.Severity, Integer> getFindingCountMap(SummaryResults summary) {
        EnumMap<Filter.Severity, Integer> result = new EnumMap<>(Filter.Severity.class);
        result.put(Filter.Severity.HIGH, summary.getHighVulnerabilityCount());
        result.put(Filter.Severity.MEDIUM, summary.getMediumVulnerabilityCount());
        result.put(Filter.Severity.LOW, summary.getLowVulnerabilityCount());
        return result;
    }

    protected void validateNotEmpty(String parameter, String parameterDescr) {
        if (StringUtils.isEmpty(parameter)) {
            String message = String.format("%s %s wasn't provided", ERROR_PREFIX, parameterDescr);
            throw new ASTRuntimeException(message);
        }
    }

    protected abstract CxScanConfig getScanConfig(ScanParams scaParams);

    protected abstract void validate(ScanParams scaParams);
}
