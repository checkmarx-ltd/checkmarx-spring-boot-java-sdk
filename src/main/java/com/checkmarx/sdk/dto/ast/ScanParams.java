package com.checkmarx.sdk.dto.ast;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class ScanParams {
    private String projectName;
    private URL remoteRepoUrl;
}