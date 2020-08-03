package com.checkmarx.sdk.config;

import com.cx.restclient.dto.scansummary.Severity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class ScaConfig {

    private String appUrl;
    private String apiUrl;
    private String accessControlUrl;
    private String tenant;

    private Double filterScore;
    private List<String> filterSeverity;

    private Map<Severity, Integer> thresholdsSeverity;
    private Double thresholdsScore;

}