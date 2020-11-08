package com.checkmarx.sdk.config;

import com.cx.restclient.dto.scansummary.Severity;
import com.typesafe.config.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * SCA-specific configuration in {@link com.checkmarx.sdk.dto.ast.ScanParams }.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScaConfig {

    private String appUrl;
    private String apiUrl;
    private String accessControlUrl;
    private String tenant;
    private String username;
    private String password;

    @Optional
    private Double filterScore;
    @Optional
    private List<String> filterSeverity;
    @Optional
    private Map<Severity, Integer> thresholdsSeverity;
    @Optional
    private Double thresholdsScore;
}