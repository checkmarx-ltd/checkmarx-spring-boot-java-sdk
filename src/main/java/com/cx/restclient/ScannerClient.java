package com.cx.restclient;

import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;

public interface ScannerClient {

    Integer getScanIdOfExistingScanIfExists(Integer projectId);

    void cancelScan(Integer scanId) throws CheckmarxException;

    Integer createScan(CxScanParams params, String comment) throws CheckmarxException;
    
    ScanResults getReportContentByScanId(Integer scanId, FilterConfiguration filter) throws CheckmarxException;

    void waitForScanCompletion(Integer scanId) throws CheckmarxException;

    Integer getLastScanId(Integer projectId);

  }
