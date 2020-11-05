package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.config.ScaConfig;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class ScanParams {
    // Common params for both AST and SCA engines.
    private String projectName;
    private String sourceDir;
    private URL remoteRepoUrl;
    private String zipPath;

    // SCA-specific parameters.
    private ScaConfig scaConfig;
}