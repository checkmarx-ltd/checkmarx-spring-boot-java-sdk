package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.sast.Filter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class FilterConfiguration {
    /**
     * Used for filtering all static code analysis results, independent of platform.
     */
    private EngineFilterConfiguration sastFilters;


    /**
     * Used for the filtering all dependency scan results, independent of platform.
     */
    private EngineFilterConfiguration scaFilters;

    /**
     * Shortcut method for populating SAST simple filters.
     */
    public static FilterConfiguration fromSimpleFilters(List<Filter> simpleFilters) {
        return FilterConfiguration.builder()
                .sastFilters(EngineFilterConfiguration.builder()
                        .simpleFilters(simpleFilters)
                        .build())
                .build();
    }
}