package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ConditionGroups {
    private List<Conditions> conditions;
}