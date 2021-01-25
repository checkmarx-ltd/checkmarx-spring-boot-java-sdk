package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.exception.CheckmarxException;
//import com.checkmarx.sdk.scanner.restclient.osa.dto.OSAResults;

import java.util.List;

/**
 * Class used to orchestrate submitting scans and retrieving results for OSA / SCA scans
 */
public interface CxOsaClient {

    public ScanResults createScanAndReport(Integer projectId, String sourceDir, ScanResults results, List<Filter> filter) throws CheckmarxException;

    public String createScan(Integer projectId, String sourceDir) throws CheckmarxException;

    public ScanResults waitForOsaScan(String scanId, Integer projectId, ScanResults results, List<Filter> filter) throws CheckmarxException;

    public ScanResults getLatestOsaResults(Integer projectId, ScanResults results, List<Filter> filter) throws CheckmarxException;

    //public ScanResults mapOsaResults(OSAResults osaResults, ScanResults results, List<Filter> filters) throws CheckmarxException;

}
