package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.sast.Filter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Filter configuration for a specific vulnerability scanner.
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EngineFilterConfiguration {
    private List<Filter> simpleFilters;
    private ScriptedFilter scriptedFilter;
}
