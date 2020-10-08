package com.cx.restclient;

import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxProject;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;

public interface ScannerClient {

    Integer getScanIdOfExistingScanIfExists(Integer projectId);

    void cancelScan(Integer scanId) throws CheckmarxException;

    Integer createScan(CxScanParams params, String comment) throws CheckmarxException;

    String getTeamId(String teamPath) throws CheckmarxException;

    /**
     * Create team under given parentId - Will use REST API to create team for version 9.0+
     *
     * @param parentTeamId
     * @param teamName
     * @return new TeamId
     * @throws CheckmarxException
     */
    String createTeam(String parentTeamId, String teamName) throws CheckmarxException;

    /**
     * Get Preset Id of an existing project
     *
     * @param projectId
     * @return preset associated with the current scan
     */
    public Integer getProjectPresetId(Integer projectId);

    /**
     * Get Preset Name based on Id
     *
     * @param presetId
     * @return preset associated with the current scan
     */
    public String getPresetName(Integer presetId);
    
    /**
     * Retrieve the report by scanId, mapped to ScanResults DTO, applying filtering as requested
     *
     * @param scanId
     * @param filter
     * @return Contents of the current report
     * @throws CheckmarxException
     */
    ScanResults getReportContentByScanId(Integer scanId, FilterConfiguration filter) throws CheckmarxException;
    
    Integer getProjectId(String ownerId, String name);

    void waitForScanCompletion(Integer scanId) throws CheckmarxException;

    CxProject getProject(Integer projectId);

    Integer getLastScanId(Integer projectId);

    String getScanConfigurationName(int configurationId);

    CxScanSettings getScanSettingsDto(int projectId);
}
