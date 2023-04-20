package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.CxEmailNotifications;
import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.checkmarx.sdk.dto.cx.preandpostaction.CustomTaskByName;
import com.checkmarx.sdk.dto.cx.preandpostaction.ListCustomeObj;
import com.checkmarx.sdk.exception.CheckmarxException;

/**
 * Works with scan settings. The settings contain<br>
 * - engine configuration<br>
 * - scan presets
 */
public interface ScanSettingsClient {
    int createScanSettings(int projectId, int presetId, int engineConfigId, int postScanId);

    int createScanSettings(int projectId, int presetId, int engineConfigId, int postScanId,
                           CxEmailNotifications emailNotifications);

    String getScanSettings(int projectId);
    CustomTaskByName getPreActionID(String customTaskName);

    CxScanSettings getScanSettingsDto(int projectId);

    int getPresetId(String preset) throws CheckmarxException;

    String getPresetName(int presetId);

    int getProjectPresetId(int projectId);

    int getEngineConfigurationId(String configurationName) throws CheckmarxException;

    String getEngineConfigurationName(int configurationId);
}
