package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.sca.report.PolicyAction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Policy {

    private String name;
    private String description;
    private List<PolicyRule> rules;
    private List<String> projectIds;
    private boolean isDisabled;
    private boolean isDefault;
    private PolicyAction actions;
}