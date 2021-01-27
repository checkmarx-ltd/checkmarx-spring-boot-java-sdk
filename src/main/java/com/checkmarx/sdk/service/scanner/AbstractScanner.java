package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.AstScaResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.ScanConfigBase;
import com.checkmarx.sdk.dto.RemoteRepositoryInfo;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.dto.sca.report.ScaSummaryBaseFormat;
import com.checkmarx.sdk.utils.scanner.client.IScanClientHelper;
import com.checkmarx.sdk.utils.State;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.ResultsBase;
import com.checkmarx.sdk.dto.SourceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
public abstract class AbstractScanner  {
    
    private static final int SCA_SCAN_INTERVAL_IN_SECONDS = 5;
    protected static final String ERROR_PREFIX = "Scan cannot be initiated.";
    private IScanClientHelper client;
    
    public AstScaResults scan(ScanParams scanParams) {
        validateScanParams(scanParams);

        RestClientConfig scanConfig = getScanConfig(scanParams);
        scanConfig.setProgressInterval(SCA_SCAN_INTERVAL_IN_SECONDS);
        ResultsBase scanResults = executeScan(scanConfig);
        
        AstScaResults results = toResults(scanResults);
        applyFilterToResults(results, scanParams);

        return results;
    }

    protected abstract void applyFilterToResults(AstScaResults scaResults, ScanParams scanParams);

    protected abstract AstScaResults toResults(ResultsBase scanResults);

    protected ResultsBase executeScan(RestClientConfig restClientConfig) {

        ResultsBase finalResults;
                
        try {
            this.client = allocateClient(restClientConfig);
            ResultsBase initResults = client.init();
            validateResults(initResults);
            ResultsBase intermediateResults = client.initiateScan();
            validateResults(intermediateResults);
            finalResults = client.waitForScanResults();
            validateResults(finalResults);
        }finally {
            client.close();
        }
        return finalResults;
    }

    protected abstract IScanClientHelper allocateClient(RestClientConfig restClientConfig);


    protected Map<Filter.Severity, Integer> getFindingCountMap(ScaSummaryBaseFormat summary) {
        EnumMap<Filter.Severity, Integer> result = new EnumMap<>(Filter.Severity.class);
        result.put(Filter.Severity.HIGH, summary.getHighVulnerabilityCount());
        result.put(Filter.Severity.MEDIUM, summary.getMediumVulnerabilityCount());
        result.put(Filter.Severity.LOW, summary.getLowVulnerabilityCount());
        return result;
    }

    protected void validateNotEmpty(String parameter, String parameterDescr) {
        if (StringUtils.isEmpty(parameter)) {
            String message = String.format("%s %s wasn't provided", ERROR_PREFIX, parameterDescr);
            throw new ScannerRuntimeException(message);
        }
    }

    protected static void setSourceLocation(ScanParams scanParams, RestClientConfig scanConfig, ScanConfigBase scaConfig) {
        if (localSourcesAreSpecified(scanParams)) {
            scaConfig.setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

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
            scaConfig.setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
            RemoteRepositoryInfo remoteRepoInfo = new RemoteRepositoryInfo();
            remoteRepoInfo.setUrl(scanParams.getRemoteRepoUrl());
            scaConfig.setRemoteRepositoryInfo(remoteRepoInfo);
        }
    }

    protected static boolean localSourcesAreSpecified(ScanParams scanParams) {
        return !StringUtils.isAllEmpty(scanParams.getZipPath(), scanParams.getSourceDir());
    }


    protected void validateResults(ResultsBase results) {
        if (results!= null && results.getException() != null){
            throw new ScannerRuntimeException(results.getException().getMessage() );
        }else if(client.getState() != State.SUCCESS) {
            throw new ScannerRuntimeException("Scanner State Failure");
        }
        
    }

    protected abstract RestClientConfig getScanConfig(ScanParams scaParams);

    protected abstract void validateScanParams(ScanParams scaParams);

    public abstract AstScaResults getLatestScanResults(ScanParams scanParams);
}
