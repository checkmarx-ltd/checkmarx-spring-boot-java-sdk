package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.ScanConfig;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ScaUploadUrlRequest {

    private List<ScanConfig> config;
}
