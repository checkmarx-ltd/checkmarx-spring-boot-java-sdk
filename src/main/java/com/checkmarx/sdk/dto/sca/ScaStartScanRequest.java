package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.ProjectToScan;
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
    private ProjectToScan project;

    /**
     * How to scan.
     */
    private List<ScanConfig> config;
}
