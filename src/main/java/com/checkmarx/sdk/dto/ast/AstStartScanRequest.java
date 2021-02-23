package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.sca.ScaProjectToScan;
import com.checkmarx.sdk.dto.ScanConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class AstStartScanRequest {

    private String branch;
    private String commitId;
    private String commitTag;
    private String uploadUrl;
    /**
     * What to scan.
     */
    private AstProjectToScan project;

    /**
     * How to scan.
     */
    private List<ScanConfig> config;
}
