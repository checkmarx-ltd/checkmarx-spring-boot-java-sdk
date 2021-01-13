package com.cx.restclient;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.checkmarx.sdk.service.AstClient;
import com.cx.restclient.ast.dto.common.*;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.SourceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
public abstract class AbstractAstClient implements AstClient {
    
    private static final int SCA_SCAN_INTERVAL_IN_SECONDS = 5;
    protected static final String ERROR_PREFIX = "Scan cannot be initiated.";

    @Override
    public ASTResultsWrapper scan(ScanParams scanParams) {
        validateScanParams(scanParams);

        CxScanConfig scanConfig = getScanConfig(scanParams);
        scanConfig.setOsaProgressInterval(SCA_SCAN_INTERVAL_IN_SECONDS);
        CommonScanResults scanResults = executeScan(scanConfig);
        
        ASTResultsWrapper scaResults = toResults(scanResults);
        applyFilterToResults(scaResults, scanParams);

        return scaResults;
    }

    protected abstract void applyFilterToResults(ASTResultsWrapper scaResults, ScanParams scanParams);

    protected abstract ASTResultsWrapper toResults(CommonScanResults scanResults);

    protected CommonScanResults executeScan(CxScanConfig cxScanConfig) {
        CxClientDelegator client;
        try {
            client = new CxClientDelegator(cxScanConfig, log);
        } catch (MalformedURLException e) {
            String message = String.format("Error creating %s instance.", CxClientDelegator.class.getSimpleName());
            throw new ASTRuntimeException(message, e);
        }
        CommonScanResults initResults = client.init();
        validateResults(initResults);
        CommonScanResults intermediateResults = client.initiateScan();
        validateResults(intermediateResults);
        CommonScanResults results = client.waitForScanResults();
        validateResults(results);
        return results;
    }
    

    protected Map<Filter.Severity, Integer> getFindingCountMap(AstScaSummaryResults summary) {
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

    protected static void setSourceLocation(ScanParams scanParams, CxScanConfig scanConfig, ASTConfig astConfig) {
        if (localSourcesAreSpecified(scanParams)) {
            astConfig.setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

            // If both zip file and source directory are specified, zip file has priority.
            // This is to conform to Common Client behavior.
            if (StringUtils.isNotEmpty(scanParams.getZipPath())) {
                log.debug("Using a local zip file for scanning.");
                scanConfig.setZipFile(new File(scanParams.getZipPath()));
            } else {
                log.debug("Using a local directory for scanning.");
                scanConfig.setSourceDir(scanParams.getSourceDir());
            }
        } else {
            astConfig.setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
            RemoteRepositoryInfo remoteRepoInfo = new RemoteRepositoryInfo();
            remoteRepoInfo.setUrl(scanParams.getRemoteRepoUrl());
            astConfig.setRemoteRepositoryInfo(remoteRepoInfo);
        }
    }

    protected static boolean localSourcesAreSpecified(ScanParams scanParams) {
        return !StringUtils.isAllEmpty(scanParams.getZipPath(), scanParams.getSourceDir());
    }

    protected abstract void validateResults(CommonScanResults results);

    protected abstract CxScanConfig getScanConfig(ScanParams scaParams);

    protected abstract void validateScanParams(ScanParams scaParams);
}
