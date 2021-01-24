package com.cx.restclient;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.common.*;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.checkmarx.sdk.utils.common.State;
import com.cx.restclient.configuration.RestClientConfig;
import com.cx.restclient.dto.IResults;
import com.cx.restclient.dto.SourceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
public abstract class AbstractScanner  {
    
    private static final int SCA_SCAN_INTERVAL_IN_SECONDS = 5;
    protected static final String ERROR_PREFIX = "Scan cannot be initiated.";
    private IRestClient client;
    
    public ASTResultsWrapper scan(ScanParams scanParams) {
        validateScanParams(scanParams);

        RestClientConfig scanConfig = getScanConfig(scanParams);
        scanConfig.setOsaProgressInterval(SCA_SCAN_INTERVAL_IN_SECONDS);
        IResults scanResults = executeScan(scanConfig);
        
        ASTResultsWrapper results = toResults(scanResults);
        applyFilterToResults(results, scanParams);

        return results;
    }

    protected abstract void applyFilterToResults(ASTResultsWrapper scaResults, ScanParams scanParams);

    protected abstract ASTResultsWrapper toResults(IResults scanResults);

    protected IResults executeScan(RestClientConfig restClientConfig) {

        IResults finalResults;
                
        try {
            this.client = allocateClient(restClientConfig);
            IResults initResults = client.init();
            validateResults(initResults);
            IResults intermediateResults = client.initiateScan();
            validateResults(intermediateResults);
            finalResults = client.waitForScanResults();
            validateResults(finalResults);
        }finally {
            client.close();
        }
        return finalResults;
    }

    protected abstract IRestClient allocateClient(RestClientConfig restClientConfig);


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

    protected static void setSourceLocation(ScanParams scanParams, RestClientConfig scanConfig, ASTConfig astConfig) {
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


    protected void validateResults(IResults results) {
        if (results!= null && results!= null && results.getException() != null){
            throw new ASTRuntimeException(results.getException().getMessage() );
        }else if(client.getState() != State.SUCCESS) {
            throw new ASTRuntimeException("Scanner State Failure");
        }
        
    }

    protected abstract RestClientConfig getScanConfig(ScanParams scaParams);

    protected abstract void validateScanParams(ScanParams scaParams);

    public abstract ASTResultsWrapper getLatestScanResults(ScanParams scanParams);
}
