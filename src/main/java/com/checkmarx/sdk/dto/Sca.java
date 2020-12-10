package com.checkmarx.sdk.dto;

import com.cx.restclient.dto.scansummary.Severity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SCA properties specified in a config-as-code file.
 */
@Getter
@Setter
@NoArgsConstructor
public class Sca implements Serializable {

    @JsonProperty
    protected String appUrl;
    @JsonProperty
    protected String apiUrl;
    @JsonProperty
    protected String accessControlUrl;
    @JsonProperty
    protected String tenant;
    @JsonProperty
    protected Map<Severity, Integer> thresholdsSeverity;
    @JsonProperty
    protected Double thresholdsScore;
    @JsonProperty
    protected List<String> filterSeverity;
    @JsonProperty
    protected Double filterScore;

}