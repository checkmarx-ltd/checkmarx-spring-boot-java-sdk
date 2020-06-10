package com.checkmarx.sdk.dto.filtering;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Passed as input parameter to a filtering script.
 * Only contains properties that are needed for filtering result evaluation.
 */
@Builder
@Getter
@Setter
public class ScriptInput {
    private final String category;
    private final String cwe;
    private final String severity;
    private final String status;
    private final String state;
}
