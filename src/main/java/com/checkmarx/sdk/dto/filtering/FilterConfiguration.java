package com.checkmarx.sdk.dto.filtering;

import com.checkmarx.sdk.dto.Filter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FilterConfiguration {
    private List<Filter> simpleFilters;
    private ScriptedFilter scriptedFilter;
}
