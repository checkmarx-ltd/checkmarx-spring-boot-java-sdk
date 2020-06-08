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
import groovy.lang.Script;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class FilterValidatorImpl implements FilterValidator {
    private static final Map<String, Integer> STATE_MAP = ImmutableMap.of(
            "TO VERIFY", 0,
            "CONFIRMED", 2,
            "URGENT", 3,
            "PROPOSED NOT EXPLOITABLE", 4
    );
    private static final Map<Integer, String> STATE_ID_TO_NAME = getInvertedStateMap();

    private static final String NEW_STATUS_SPECIAL_CASE = "NEW";

    /**
     * An object variable with this name will be passed to the filtering script.
     */
    private static final String INPUT_VARIABLE_NAME = "finding";

    @Override
    public boolean passesFilter(@NotNull QueryType findingGroup, @NotNull ResultType finding,
                                FilterConfiguration filterConfiguration) {
        validate(filterConfiguration);
        boolean result;
        if (!filtersAreSpecified(filterConfiguration)) {
            // No filters => everything passes.
            result = true;
        } else if (filterConfiguration.getScriptedFilter() != null) {
            result = passesScriptedFilter(findingGroup, finding, filterConfiguration.getScriptedFilter());
        } else {
            List<Filter> filters = filterConfiguration.getSimpleFilters();
            result = CollectionUtils.isEmpty(filters) ||
                    (findingGroupPassesFilter(findingGroup, filters) && findingPassesFilter(finding, filters));
        }
        return result;
    }

    private static void validate(FilterConfiguration filterConfiguration) {
        if (filterConfiguration != null &&
                filterConfiguration.getScriptedFilter() != null &&
                CollectionUtils.isNotEmpty(filterConfiguration.getSimpleFilters())) {

            throw new CheckmarxRuntimeException("Simple filters and scripted filter cannot be used together. " +
                    "Please either specify one of them or don't use filters.");
        }
    }

    private static boolean filtersAreSpecified(FilterConfiguration filterConfiguration) {
        return filterConfiguration != null &&
                (CollectionUtils.isNotEmpty(filterConfiguration.getSimpleFilters()) ||
                        filterConfiguration.getScriptedFilter() != null);
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
        List<Integer> status = new ArrayList<>();
        for (Filter filter : filters) {
            if (filter.getType().equals(Filter.Type.STATUS)) {
                //handle New Status separately (this field is Status as opposed to State for the others
                if (filter.getValue().equalsIgnoreCase(NEW_STATUS_SPECIAL_CASE) &&
                        finding.getStatus().equalsIgnoreCase(NEW_STATUS_SPECIAL_CASE)) {
                    return true;
                }
                status.add(STATE_MAP.get(filter.getValue().toUpperCase(Locale.ROOT)));
            }
        }
        return status.isEmpty() || status.contains(Integer.parseInt(finding.getState()));
    }

    private static boolean passesScriptedFilter(QueryType findingGroup, ResultType finding, ScriptedFilter filter) {
        ScriptInput input = getScriptInput(findingGroup, finding);
        return evaluateFilterScript(input, filter.getScript());
    }

    private static ScriptInput getScriptInput(QueryType findingGroup, ResultType finding) {
        String severity = findingGroup.getSeverity().toUpperCase(Locale.ROOT);

        return ScriptInput.builder()
                .category(findingGroup.getName())
                .cwe(findingGroup.getCweId())
                .severity(severity)
                .status(getEffectiveStatus(finding))
                .build();
    }

    private static boolean evaluateFilterScript(ScriptInput input, Script script) {
        Binding binding = new Binding();
        binding.setVariable(INPUT_VARIABLE_NAME, input);
        script.setBinding(binding);
        Object rawResult = script.run();

        if (rawResult instanceof Boolean) {
            return (boolean) rawResult;
        } else {
            throw new CheckmarxRuntimeException("Filtering script must return a boolean value.");
        }
    }

    private static String getEffectiveStatus(@NotNull ResultType finding) {
        String effectiveStatus;
        if (finding.getStatus().equalsIgnoreCase(NEW_STATUS_SPECIAL_CASE)) {
            // Filter type is called 'status', but we actually use finding status only in this specific case.
            effectiveStatus = NEW_STATUS_SPECIAL_CASE;
        } else {
            // In the rest of the cases, finding state is used (and not status).
            effectiveStatus = STATE_ID_TO_NAME.get(Integer.parseInt(finding.getState()));
        }
        return effectiveStatus;
    }

    private static boolean fieldMatches(String fieldValue, List<String> allowedValues) {
        return allowedValues.isEmpty() ||
                allowedValues.contains(fieldValue.toUpperCase(Locale.ROOT));
    }

    private static Map<Integer, String> getInvertedStateMap() {
        Map<Integer, String> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : FilterValidatorImpl.STATE_MAP.entrySet()) {
            result.put(entry.getValue(), entry.getKey());
        }
        return ImmutableMap.copyOf(result);
    }
}
