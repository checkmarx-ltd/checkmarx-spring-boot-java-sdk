package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.CxScanSettings;

public interface ScanSettingsClient {
    int createScanSettings(int projectId, int presetId, int engineConfigId);

    String getScanSettings(int projectId);

    CxScanSettings getScanSettingsDto(int projectId);
}
