package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.config.ScaConfig;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

/**
 * Scan parameters for AST-SCA and AST-SAST engines. Passed from the outside of this SDK.
 * Mapped to a corresponding Common Client object.
 */
@Data
@Builder
public class ScanParams {
    // Common params for both AST and SCA engines.
    private String projectName;
    private String sourceDir;
    private URL remoteRepoUrl;
    private String zipPath;
    private FilterConfiguration filterConfiguration;
    private String branch;
    // SCA-specific parameters.
    private ScaConfig scaConfig;
}