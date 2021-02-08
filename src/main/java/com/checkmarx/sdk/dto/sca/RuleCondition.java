package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class RuleCondition {

    private String operator;
    private List<String> parameterValue;
    private String parameter;
}