package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.ScriptedFilter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Checks if SAST results pass provided filters.
 */
public interface FilterValidator {
    /**
     * Check if a finding group meets the filter criteria
     *
     * @param findingGroup container for findings with the same vulnerability type
     * @param filters filters to check against
     * @return a value indicating whether the finding group meets the filter criteria
     */
    boolean passesFilter(QueryType findingGroup, List<Filter> filters);

    /**
     * Check if a finding meets the filter criteria
     *
     * @param finding a finding to check against the filter
     * @param filters filters to check against
     * @return a value indicating whether the finding meets the filter criteria
     */
    boolean passesFilter(ResultType finding, List<Filter> filters);

    boolean passesScriptedFilter(@NotNull QueryType findingGroup,
                                 @NotNull ResultType finding,
                                 @NotNull ScriptedFilter filter);
}
