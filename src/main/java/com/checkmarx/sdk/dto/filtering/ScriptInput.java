package com.checkmarx.sdk.dto.filtering;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Passed as input parameter to a filtering script.
 * Only contains properties that are needed for filtering result evaluation.
 * May be extended to include properties for other types of filters.
 */
@Builder
@Getter
@Setter
public class ScriptInput {
    private final String severity;
    private final String status;
}
