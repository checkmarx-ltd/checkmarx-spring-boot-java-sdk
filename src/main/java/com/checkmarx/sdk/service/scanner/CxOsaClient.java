package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.exception.CheckmarxException;
//import com.checkmarx.sdk.scanner.restclient.osa.dto.OSAResults;

import java.util.List;

/**
 * Class used to orchestrate submitting scans and retrieving results for OSA / SCA scans
 */
public interface CxOsaClient {

    ScanResults createScanAndReport(Integer projectId, String sourceDir, ScanResults results, List<Filter> filter) throws CheckmarxException;

    String createScan(Integer projectId, String sourceDir) throws CheckmarxException;

    ScanResults waitForOsaScan(String scanId, Integer projectId, ScanResults results, List<Filter> filter) throws CheckmarxException;

    ScanResults getLatestOsaResults(Integer projectId, ScanResults results, List<Filter> filter) throws CheckmarxException;
    
}
