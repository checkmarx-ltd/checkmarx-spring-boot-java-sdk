package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.ProjectToScan;
import com.checkmarx.sdk.dto.ScanConfig;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AstStartScanRequest {

    private String branch;
    private String commitId;
    private String commitTag;
    private String uploadUrl;
    /**
     * What to scan.
     */
    private ProjectToScan project;

    /**
     * How to scan.
     */
    private List<ScanConfig> config;
}
