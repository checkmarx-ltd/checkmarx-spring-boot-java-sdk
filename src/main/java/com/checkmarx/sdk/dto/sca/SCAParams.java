package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Data
@Builder
public class SCAParams {
    private String projectName;
    private URL remoteRepoUrl;
}