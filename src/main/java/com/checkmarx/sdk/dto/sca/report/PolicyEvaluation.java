package com.checkmarx.sdk.dto.sca.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyEvaluation {

    private String id;
    private String name;

    @JsonProperty("isViolated")
    private boolean isViolated;
    private PolicyAction actions;
}