package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.checkmarx.sdk.exception.CheckmarxException;

public interface ScanSettingsClient {
    int createScanSettings(int projectId, int presetId, int engineConfigId);

    String getScanSettings(int projectId);

    CxScanSettings getScanSettingsDto(int projectId);

    int getPresetId(String preset) throws CheckmarxException;

    String getPresetName(int presetId);

    int getProjectPresetId(int projectId);

    int getScanConfigurationId(String configuration) throws CheckmarxException;

    String getScanConfigurationName(int configurationId);
}
