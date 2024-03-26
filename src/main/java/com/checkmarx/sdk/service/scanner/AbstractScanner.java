package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.config.PDFPropertiesSCA;
import com.checkmarx.sdk.dto.*;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.utils.scanner.client.IScanClientHelper;
import com.checkmarx.sdk.utils.State;
import com.checkmarx.sdk.config.RestClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public abstract class AbstractScanner  {
    
    private static final int SCA_SCAN_INTERVAL_IN_SECONDS = 5;
    protected static final String ERROR_PREFIX = "Scan cannot be initiated.";

    private IScanClientHelper client;

    public void scanWithNoWaitingToResults(ScanParams scanParams) {
        validateScanParams(scanParams);
        RestClientConfig scanConfig = getScanConfig(scanParams);

        try {
            this.client = allocateClient(scanConfig);
            client.init();
            client.initiateScan();
        } finally {
            client.close();
        }
    }

    public AstScaResults scan(ScanParams scanParams) {
        validateScanParams(scanParams);

        RestClientConfig scanConfig = getScanConfig(scanParams);
        scanConfig.setProgressInterval(SCA_SCAN_INTERVAL_IN_SECONDS);
        ResultsBase scanResults = executeScan(scanConfig);
        
        AstScaResults results = toResults(scanResults);
        applyFilterToResults(results, scanParams);

        return results;
    }

    public AstScaResults scanForPDF(ScanParams scanParams,PDFPropertiesSCA pdfSCAprop) {
        validateScanParams(scanParams);

        RestClientConfig scanConfig = getScanConfig(scanParams);
        scanConfig.setProgressInterval(SCA_SCAN_INTERVAL_IN_SECONDS);
        executeScanForPDF(scanConfig,pdfSCAprop);

//        AstScaResults results = toResults(scanResults);
//        applyFilterToResults(results, scanParams);

        return null;
    }

    protected abstract void applyFilterToResults(AstScaResults scaResults, ScanParams scanParams);

    protected abstract AstScaResults toResults(ResultsBase scanResults);

    protected synchronized  ResultsBase executeScan(RestClientConfig restClientConfig) {

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

    protected ResultsBase executeScanForPDF(RestClientConfig restClientConfig, PDFPropertiesSCA pdfSCAprop) {

        ResultsBase finalResults;

        try {
            this.client = allocateClient(restClientConfig);
            ResultsBase initResults = client.init();
            validateResults(initResults);
            ResultsBase intermediateResults = client.initiateScanPDF();
            validateResults(intermediateResults);
            finalResults = client.waitForScanResultsForPDF(pdfSCAprop);
            validateResults(finalResults);
        }finally {
            client.close();
        }
        return finalResults;
    }

    protected abstract IScanClientHelper allocateClient(RestClientConfig restClientConfig);



    protected void validateNotEmpty(String parameter, String parameterDescr) {
        if (StringUtils.isEmpty(parameter)) {
            String message = String.format("%s %s wasn't provided", ERROR_PREFIX, parameterDescr);
            throw new ScannerRuntimeException(message);
        }
    }

    protected void setSourceLocation(ScanParams scanParams, RestClientConfig scanConfig, ScanConfigBase configBase) {
        RemoteRepositoryInfo remoteRepoInfo = new RemoteRepositoryInfo();
        setRemoteBranch(scanParams, remoteRepoInfo);

        if (localSourcesAreSpecified(scanParams)) {
            configBase.setSourceLocationType(SourceLocationType.CLONED_REMOTE_REPOSITORY);
            // If both zip file and source directory are specified, zip file has priority.
            // This is to conform to Common Client behavior.
            if (StringUtils.isNotEmpty(scanParams.getZipPath())) {
                log.debug("Using a local zip file for scanning.");
                scanConfig.setZipFile(new File(scanParams.getZipPath()));
            } else if (scanParams.getRemoteRepoUrl() != null) {
                log.debug("Using a cloned local directory for scanning.");
                scanConfig.setSourceDir(scanParams.getSourceDir());
            }else {
                log.debug("Using a local directory for scanning.");
                configBase.setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);
                scanConfig.setSourceDir(scanParams.getSourceDir());
            }
        } else {
            configBase.setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
            remoteRepoInfo.setUrl(scanParams.getRemoteRepoUrl());
        }
        configBase.setRemoteRepositoryInfo(remoteRepoInfo);
    }

    protected abstract void setRemoteBranch(ScanParams scanParams, RemoteRepositoryInfo remoteRepoInfo) ;


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

    public String initiateSbom(String scanId,ScanParams scanParams,String fileFormat,boolean hideDev,boolean showLicenses)
    {
        try {
            validateScanParams(scanParams);
            RestClientConfig scanConfig = getScanConfig(scanParams);
            this.client = allocateClient(scanConfig);
            client.init();
            return client.initiateSbom(scanId, fileFormat,hideDev,showLicenses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            client.close();
        }
    }

    protected abstract RestClientConfig getScanConfig(ScanParams scaParams);

    protected abstract void validateScanParams(ScanParams scaParams);

    public abstract AstScaResults getLatestScanResults(ScanParams scanParams);
}
