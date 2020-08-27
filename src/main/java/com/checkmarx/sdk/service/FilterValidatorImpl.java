package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.ScriptInput;
import com.checkmarx.sdk.dto.filtering.ScriptedFilter;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilterValidatorImpl implements FilterValidator {
    /**
     * Maps finding state name (as specified in filter) to a numeric state ID (as returned in SAST report).
     */
    private static final Map<String, String> STATE_MAP = ImmutableMap.of(
            "TO VERIFY", "0",
            "CONFIRMED", "2",
            "URGENT", "3",
            "PROPOSED NOT EXPLOITABLE", "4",
            "SUSPICIOUS", "5"
    );
    private static final Map<String, String> STATE_ID_TO_NAME = getInvertedStateMap();

    /**
     * An object variable with this name will be passed to the filtering script.
     */
    private static final String INPUT_VARIABLE_NAME = "finding";

    @Override
    public boolean passesFilter(@NotNull QueryType findingGroup, @NotNull ResultType finding,
                                FilterConfiguration filterConfiguration) {
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
            result = passesScriptedFilter(findingGroup, finding, filterConfiguration);
        } else {
            result = passesSimpleFilter(findingGroup, finding, filterConfiguration);
        }

        log.debug("Finding {} {} the filter.", finding.getNodeId(), result ? "passes" : "does not pass");

        return result;
    }

    private static boolean passesScriptedFilter(QueryType findingGroup,
                                                ResultType finding,
                                                FilterConfiguration filterConfiguration) {
        ScriptedFilter filter = filterConfiguration.getScriptedFilter();
        ScriptInput input = getScriptInput(findingGroup, finding);
        return evaluateFilterScript(filter.getScript(), input);
    }

    private static boolean passesSimpleFilter(QueryType findingGroup,
                                              ResultType finding,
                                              FilterConfiguration filterConfiguration) {
        List<Filter> filters = filterConfiguration.getSimpleFilters();
        return CollectionUtils.isEmpty(filters) ||
                (findingGroupPassesFilter(findingGroup, filters) && findingPassesFilter(finding, filters));
    }

    private static boolean hasScriptedFilter(FilterConfiguration filterConfiguration) {
        return filterConfiguration != null &&
                filterConfiguration.getScriptedFilter() != null &&
                filterConfiguration.getScriptedFilter().getScript() != null;
    }

    private static boolean hasSimpleFilters(FilterConfiguration filterConfiguration) {
        return filterConfiguration != null &&
                CollectionUtils.isNotEmpty(filterConfiguration.getSimpleFilters());
    }

    private static boolean findingGroupPassesFilter(QueryType findingGroup, List<Filter> filters) {
        List<String> severity = new ArrayList<>();
        List<String> cwe = new ArrayList<>();
        List<String> category = new ArrayList<>();

        for (Filter filter : filters) {
            Filter.Type type = filter.getType();
            String value = filter.getValue();
            List<String> targetList = null;
            if (type.equals(Filter.Type.SEVERITY)) {
                targetList = severity;
            } else if (type.equals(Filter.Type.TYPE)) {
                targetList = category;
            } else if (type.equals(Filter.Type.CWE)) {
                targetList = cwe;
            }

            if (targetList != null) {
                targetList.add(value.toUpperCase(Locale.ROOT));
            }
        }

        return fieldMatches(findingGroup.getSeverity(), severity) &&
                fieldMatches(findingGroup.getCweId(), cwe) &&
                fieldMatches(findingGroup.getName(), category);
    }

    private static boolean findingPassesFilter(ResultType finding, List<Filter> filters) {
        List<String> statuses = new ArrayList<>();
        List<String> states = new ArrayList<>();
        for (Filter filter : filters) {
            if (filter.getType().equals(Filter.Type.STATUS)) {
                statuses.add(filter.getValue().toUpperCase(Locale.ROOT));
            } else if (filter.getType().equals(Filter.Type.STATE)) {
                String stateName = filter.getValue().toUpperCase(Locale.ROOT);
                String stateId = STATE_MAP.get(stateName);
                if (stateId == null) {
                    log.warn("Unknown status is specified in filter: '{}'. This filter value will be ignored.",
                            filter.getValue());
                } else {
                    states.add(stateId);
                }
            }
        }

        return fieldMatches(finding.getStatus(), statuses) &&
                fieldMatches(finding.getState(), states);
    }

    private static ScriptInput getScriptInput(QueryType findingGroup, ResultType finding) {
        String stateName = STATE_ID_TO_NAME.get(finding.getState());

        return ScriptInput.builder()
                .category(findingGroup.getName().toUpperCase(Locale.ROOT))
                .cwe(findingGroup.getCweId())
                .severity(findingGroup.getSeverity().toUpperCase(Locale.ROOT))
                .status(finding.getStatus().toUpperCase(Locale.ROOT))
                .state(stateName)
                .build();
    }

    private static boolean evaluateFilterScript(Script script, ScriptInput input) {
        Binding binding = new Binding();
        binding.setVariable(INPUT_VARIABLE_NAME, input);
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

    private static void rethrowWithDetailedMessage(GroovyRuntimeException cause) {
        List<String> existingFields = Arrays.stream(ScriptInput.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        String message = String.format("A runtime error has occurred while executing the filter script. " +
                        "Please use %s.<property> in your expressions, where <property> is one of %s.",
                INPUT_VARIABLE_NAME,
                existingFields);

        throw new CheckmarxRuntimeException(message, cause);
    }

    private static boolean fieldMatches(String fieldValue, List<String> allowedValues) {
        return allowedValues.isEmpty() ||
                allowedValues.contains(fieldValue.toUpperCase(Locale.ROOT));
    }

    private static Map<String, String> getInvertedStateMap() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : FilterValidatorImpl.STATE_MAP.entrySet()) {
            result.put(entry.getValue(), entry.getKey());
        }
        return ImmutableMap.copyOf(result);
    }
}
