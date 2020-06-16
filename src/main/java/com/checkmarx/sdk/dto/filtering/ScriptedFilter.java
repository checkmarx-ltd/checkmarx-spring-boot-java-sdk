package com.checkmarx.sdk.dto.filtering;

import groovy.lang.Script;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ScriptedFilter {
    private Script script;
}
