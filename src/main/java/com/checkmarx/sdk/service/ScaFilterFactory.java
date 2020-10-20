package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.ScaConfig;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.filtering.EngineFilterConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScaFilterFactory {
    private final NumberFormat neutralFormat = NumberFormat.getInstance(FilterValidator.NUMERIC_CONVERSION_LOCALE);

    private final ScaProperties scaProperties;

    /**
     * Creates filter configuration for SCA using SCA properties and a provided {@link ScaConfig}.
     *
     * @param filterOverrides contains optional filters. If present, these filters override filters
     *                        from {@link ScaProperties}.
     * @return filter configuration that may be passed to {@link FilterValidator}.
     */
    public EngineFilterConfiguration getFilterConfiguration(ScaConfig filterOverrides) {
        List<Filter> severityFilters = getEffectiveSeverityFilters(filterOverrides);
        List<Filter> allFilters = new ArrayList<>(severityFilters);

        Filter scoreFilter = getEffectiveScoreFilter(filterOverrides);
        allFilters.add(scoreFilter);

        return EngineFilterConfiguration.builder()
                .simpleFilters(allFilters)
                .build();
    }

    private Filter getEffectiveScoreFilter(ScaConfig filterOverride) {
        Double numericScore = Optional.ofNullable(filterOverride)
                .map(ScaConfig::getFilterScore)
                .orElse(scaProperties.getFilterScore());

        String score = Optional.ofNullable(numericScore)
                .map(neutralFormat::format)
                .orElse(null);

        return Filter.builder()
                .type(Filter.Type.SCORE)
                .value(score)
                .build();
    }

    private List<Filter> getEffectiveSeverityFilters(ScaConfig filterOverride) {
        List<String> severityOverride = Optional.ofNullable(filterOverride)
                .map(ScaConfig::getFilterSeverity)
                .orElse(null);

        List<String> filtersFromProperties = scaProperties.getFilterSeverity();

        List<String> filterValues = CollectionUtils.isNotEmpty(severityOverride) ? severityOverride : filtersFromProperties;
        List<String> nullSafeResult = Optional.ofNullable(filterValues).orElseGet(ArrayList::new);

        return nullSafeResult.stream()
                .map(severity ->
                        Filter.builder()
                                .type(Filter.Type.SEVERITY)
                                .value(severity)
                                .build())
                .collect(Collectors.toList());
    }
}
