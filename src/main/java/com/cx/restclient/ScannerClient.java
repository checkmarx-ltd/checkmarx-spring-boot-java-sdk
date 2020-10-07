package com.cx.restclient;

import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxProject;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;

public interface ScannerClient {

    Integer getScanIdOfExistingScanIfExists(Integer projectId);

    void cancelScan(Integer scanId) throws CheckmarxException;

    Integer createScan(CxScanParams params, String comment) throws CheckmarxException;

    String getTeamId(String teamPath) throws CheckmarxException;

    ScanResults getReportContentByScanId(Integer scanId, FilterConfiguration filter) throws CheckmarxException;

    Integer getProjectId(String ownerId, String name);

    void waitForScanCompletion(Integer scanId) throws CheckmarxException;

    CxProject getProject(Integer projectId);

    Integer getLastScanId(Integer projectId);

  }
