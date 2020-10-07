package com.cx.restclient;

import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxProject;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;

public interface ScannerClient {
    
    Integer createScan(CxScanParams params, String comment) throws CheckmarxException;

    String getTeamId(String teamPath) throws CheckmarxException;

    abstract String createTeam(String parentID, String teamName) throws CheckmarxException;

    abstract ScanResults getReportContentByScanId(Integer scanId, FilterConfiguration filter) throws CheckmarxException;

    Integer getProjectId(String ownerId, String name);

    void waitForScanCompletion(Integer scanId) throws CheckmarxException;

    abstract CxProject getProject(Integer projectId);

    abstract Integer getLastScanId(Integer projectId);

    //
    /// I think things below here should be removed the public interface. They are specific
    /// Cx SAST.
    //
    abstract String getTeamId(String parentTeamId, String teamName) throws CheckmarxException;
}
