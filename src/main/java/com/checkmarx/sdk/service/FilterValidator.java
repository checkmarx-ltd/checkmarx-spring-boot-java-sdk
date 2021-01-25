package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.filtering.EngineFilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Checks if a specific scan result item (finding) passes provided filters.
 */
@Service
@Slf4j
public class FilterValidator {
    public static final Locale NUMERIC_CONVERSION_LOCALE = Locale.ROOT;

    /**
     * An object variable with this name will be passed to the filtering script.
     */
    private static final String INPUT_VARIABLE_NAME = "finding";

    private final NumberFormat numberFormat = NumberFormat.getInstance(NUMERIC_CONVERSION_LOCALE);

    /**
     * @param filterConfiguration filters to check against
     * @return a value indicating whether the finding meets the filter criteria
     */
    public boolean passesFilter(@NotNull FilterInput finding, EngineFilterConfiguration filterConfiguration) {
        boolean result;

        boolean hasSimpleFilters = hasSimpleFilters(filterConfiguration);
        boolean hasScriptedFilter = hasScriptedFilter(filterConfiguration);

        if (hasScriptedFilter && hasSimpleFilters) {
            throw new CheckmarxRuntimeException("Simple filters and scripted filter cannot be used together. " +
                    "Please either specify one of them or don't use filters.");
        } else if (!hasSimpleFilters && !hasScriptedFilter) {
            // No filters => everything passes.
            result = true;
        } else if (hasScriptedFilter) {
            result = passesScriptedFilter(finding, filterConfiguration);
        } else {
            result = passesSimpleFilter(finding, filterConfiguration);
        }
        
        return result;
    }

    private static boolean passesScriptedFilter(FilterInput finding, EngineFilterConfiguration filterConfiguration) {
        Script script = filterConfiguration.getScriptedFilter().getScript();
        Binding binding = new Binding();
        binding.setVariable(INPUT_VARIABLE_NAME, finding);
        script.setBinding(binding);
        Object rawResult = null;
        try {
            rawResult = script.run();
        } catch (GroovyRuntimeException e) {
            rethrowWithDetailedMessage(e);
        } catch (Exception e) {
            throw new CheckmarxRuntimeException("An unexpected error has occurred while executing the filter script.", e);
        }

        if (rawResult instanceof Boolean) {
            return (boolean) rawResult;
        } else {
            throw new CheckmarxRuntimeException("Filtering script must return a boolean value.");
        }
    }

    private boolean passesSimpleFilter(FilterInput finding, EngineFilterConfiguration filterConfiguration) {
        List<Filter> filters = filterConfiguration.getSimpleFilters();
        return CollectionUtils.isEmpty(filters) || findingPassesFilter(finding, filters);
    }

    private static boolean hasScriptedFilter(EngineFilterConfiguration filterConfiguration) {
        return filterConfiguration != null &&
                filterConfiguration.getScriptedFilter() != null &&
                filterConfiguration.getScriptedFilter().getScript() != null;
    }

    private static boolean hasSimpleFilters(EngineFilterConfiguration filterConfiguration) {
        return filterConfiguration != null &&
                CollectionUtils.isNotEmpty(filterConfiguration.getSimpleFilters());
    }

    private boolean findingPassesFilter(FilterInput finding, List<Filter> filters) {
        Map<Filter.Type, List<String>> valuesByType = groupFilterValuesByFilterType(filters);

        return fieldMatches(finding.getStatus(), valuesByType.get(Filter.Type.STATUS)) &&
                fieldMatches(finding.getState(), valuesByType.get(Filter.Type.STATE)) &&
                fieldMatches(finding.getSeverity(), valuesByType.get(Filter.Type.SEVERITY)) &&
                fieldMatches(finding.getCwe(), valuesByType.get(Filter.Type.CWE)) &&
                fieldMatches(finding.getCategory(), valuesByType.get(Filter.Type.TYPE)) &&
                scoreIsAtLeast(finding.getScore(), valuesByType.get(Filter.Type.SCORE));
    }

    private boolean scoreIsAtLeast(Double scoreToCheck, List<String> filterValues) {
        boolean passes = true;
        Double minAllowedScore;
        if (scoreToCheck != null && (minAllowedScore = getNumericScore(filterValues)) != null) {
            passes = (scoreToCheck >= minAllowedScore);
        }
        return passes;
    }

    private Double getNumericScore(List<String> filterValues) {
        Double result = null;
        if (!CollectionUtils.isEmpty(filterValues)) {
            if (filterValues.size() == 1) {
                String scoreString = filterValues.get(0);
                if(!StringUtils.isEmpty(scoreString)){
                    try {
                        result = numberFormat.parse(scoreString).doubleValue();
                    } catch (ParseException e) {
                        log.warn("Invalid {} filter value: '{}', ignoring.", Filter.Type.SCORE, scoreString);
                    }
                }
                else{
                    log.debug("{} is empty. ignoring", Filter.Type.SCORE);
                }
            } else {
                log.warn("More than 1 {} filter is specified, ignoring.", Filter.Type.SCORE);
            }
        }
        return result;
    }

    private static Map<Filter.Type, List<String>> groupFilterValuesByFilterType(List<Filter> filters) {
        // First prepare an empty list for each Filter.Type enum member.
        Map<Filter.Type, List<String>> valuesByType = Arrays.stream(Filter.Type.values())
                .collect(Collectors.toMap(Function.identity(),
                        filterType -> new ArrayList<>()));

        // Populate the lists using the provided filters.
        for (Filter filter : filters) {
            List<String> targetList = valuesByType.get(filter.getType());
            String safeValue = StringUtils.defaultString(filter.getValue());
            targetList.add(safeValue.toUpperCase(Locale.ROOT));
        }

        return valuesByType;
    }

    private static void rethrowWithDetailedMessage(GroovyRuntimeException cause) {
        List<String> existingFields = Arrays.stream(FilterInput.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        String message = String.format("A runtime error has occurred while executing the filter script. " +
                        "Please use %s.<property> in your expressions, where <property> is one of %s.",
                INPUT_VARIABLE_NAME,
                existingFields);

        throw new CheckmarxRuntimeException(message, cause);
    }

    private static boolean fieldMatches(String fieldValue, List<String> allowedValues) {
        boolean isMatch = false;

        if (allowedValues.isEmpty()) {
            return true;
        }

        if (fieldValue != null) {
            isMatch =  allowedValues.contains(fieldValue.toUpperCase(Locale.ROOT));
        }
        return isMatch;
    }

}