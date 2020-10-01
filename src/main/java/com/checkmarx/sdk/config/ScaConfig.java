package com.checkmarx.sdk.config;

import com.cx.restclient.dto.scansummary.Severity;
import com.typesafe.config.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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
    private Double filterScore;
    @Optional
    private List<String> filterSeverity;
    @Optional
    private Map<Severity, Integer> thresholdsSeverity;
    @Optional
    private Double thresholdsScore;

    public void setThresholdsSeverity(Map<String, Object> thresholdsSeverity) {
        EnumMap<Severity, Integer> map = new EnumMap<>(Severity.class);
        thresholdsSeverity.forEach( (key, value) -> map.put(Severity.valueOf(key), (Integer) value));
        this.thresholdsSeverity = map;
    }

    public void initThresholdsSeverity(Map<Severity, Integer> thresholdsSeverity) {
        this.thresholdsSeverity = thresholdsSeverity;
    }

}