package com.checkmarx.sdk.dto.filtering;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ScriptInput {
    private final String severity;
    private final String status;
}
