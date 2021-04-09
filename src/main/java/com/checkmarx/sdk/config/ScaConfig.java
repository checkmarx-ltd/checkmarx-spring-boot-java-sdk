package com.checkmarx.sdk.config;

import com.checkmarx.sdk.dto.scansummary.Severity;
import com.typesafe.config.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.EnumMap;
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
    @Optional
    private boolean includeSources;
    @Optional
    private List<String> excludeFiles;

    @Optional
    private Map<Severity, Integer> thresholdsSeverity;
    @Optional
    private Double thresholdsScore;
    @Optional
    private String team;

    /**
     * This setter allows to avoid ConfigProvider error: Map&lt;Severity,Integer&gt; is not supported.
     */
    public void setThresholdsSeverity(Map<String, Object> thresholdsSeverity) {
        EnumMap<Severity, Integer> map = new EnumMap<>(Severity.class);
        java.util.Optional.ofNullable(thresholdsSeverity)
                .orElseGet(Collections::emptyMap)
                .forEach((key, value) -> map.put(Severity.valueOf(key), (Integer) value));

        this.thresholdsSeverity = map;
    }

    public void setThresholdsSeverityDirectly(Map<Severity, Integer> thresholdsSeverity) {
        this.thresholdsSeverity = thresholdsSeverity;
    }
}