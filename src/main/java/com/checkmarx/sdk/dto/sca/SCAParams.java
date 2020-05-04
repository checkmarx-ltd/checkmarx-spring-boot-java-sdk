package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SCAParams {

    private String projectName;
    private String remoteRepoUrl;
}