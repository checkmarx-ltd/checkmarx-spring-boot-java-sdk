package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PolicyRule {

    private String name;
    private List<RuleCondition> conditions;
}