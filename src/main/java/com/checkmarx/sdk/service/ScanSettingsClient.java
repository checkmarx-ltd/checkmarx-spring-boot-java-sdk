package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.checkmarx.sdk.exception.CheckmarxException;

public interface ScanSettingsClient {
    int createScanSettings(int projectId, int presetId, int engineConfigId);

    String getScanSettings(int projectId);

    CxScanSettings getScanSettingsDto(int projectId);

    Integer getPresetId(String preset) throws CheckmarxException;

    String getPresetName(int presetId);

    Integer getProjectPresetId(int projectId);
}
