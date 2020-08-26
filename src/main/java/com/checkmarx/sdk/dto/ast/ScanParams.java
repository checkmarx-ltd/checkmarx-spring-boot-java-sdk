package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.config.ScaConfig;
import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class ScanParams {
    private String projectName;
    private String sourceDir;
    private URL remoteRepoUrl;
    private String zipPath;
    private ScaConfig scaConfig;
}