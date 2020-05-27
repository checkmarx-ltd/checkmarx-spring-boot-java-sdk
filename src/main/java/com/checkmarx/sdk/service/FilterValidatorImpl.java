package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.checkmarx.sdk.dto.filtering.ScriptInput;
import com.checkmarx.sdk.dto.filtering.ScriptedFilter;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.Script;
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
    private static final String INPUT_VARIABLE_NAME = "finding";

    @Override
    public boolean passesFilter(QueryType findingGroup, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

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

    @Override
    public boolean passesFilter(ResultType finding, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

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

    @Override
    public boolean passesScriptedFilter(@NotNull QueryType findingGroup,
                                        @NotNull ResultType finding,
                                        @NotNull ScriptedFilter filter) {
        String status = getEffectiveStatus(finding);
        String severity = findingGroup.getSeverity().toUpperCase(Locale.ROOT);

        ScriptInput input = ScriptInput.builder()
                .status(status)
                .severity(severity)
                .build();

        return evaluateFilterScript(input, filter.getScript());
    }

    private boolean evaluateFilterScript(ScriptInput input, Script script) {
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

    private String getEffectiveStatus(@NotNull ResultType finding) {
        String effectiveStatus;
        if (finding.getStatus().equalsIgnoreCase(NEW_STATUS_SPECIAL_CASE)) {
            // Filter type is called 'status', but we actually use finding status only in this specific case.
            effectiveStatus = NEW_STATUS_SPECIAL_CASE;
        } else {
            // In the rest of the cases, finding state is used (as opposed to status).
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
