package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.ScanConfig;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ScaStartScanRequest {
    
    /**
     * 
     * What to scan.
     */
    private ScaProjectToScan project;

    /**
     * How to scan.
     */
    private List<ScanConfig> config;
}
