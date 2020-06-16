package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.Filter;
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
    private List<Filter> simpleFilters;
    private ScriptedFilter scriptedFilter;

    public static FilterConfiguration fromSimpleFilters(List<Filter> simpleFilters) {
        return FilterConfiguration.builder().simpleFilters(simpleFilters).build();
    }
}
