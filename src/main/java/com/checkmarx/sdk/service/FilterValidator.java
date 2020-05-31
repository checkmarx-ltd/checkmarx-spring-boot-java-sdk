package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;

import javax.validation.constraints.NotNull;

/**
 * Checks if SAST results pass provided filters.
 */
public interface FilterValidator {
    /**
     * Check if a finding and its group meet the filter criteria
     *
     * @param findingGroup        the parent of this finding. Container for findings with the same vulnerability type.
     * @param finding             a finding to check against the filter
     * @param filterConfiguration filters to check against
     * @return a value indicating whether the finding meets the filter criteria
     */
    boolean passesFilter(@NotNull QueryType findingGroup, @NotNull ResultType finding,
                         FilterConfiguration filterConfiguration);
}
