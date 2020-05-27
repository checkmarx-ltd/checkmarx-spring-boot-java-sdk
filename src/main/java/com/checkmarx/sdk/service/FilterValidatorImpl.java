package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.cx.xml.QueryType;
import com.checkmarx.sdk.dto.cx.xml.ResultType;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class FilterValidatorImpl implements FilterValidator {
    private static final Map<String, Integer> STATUS_MAP = ImmutableMap.of(
            "TO VERIFY", 0,
            "CONFIRMED", 2,
            "URGENT", 3,
            "PROPOSED NOT EXPLOITABLE", 4
    );

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
                if (filter.getValue().equalsIgnoreCase("New") &&
                        finding.getStatus().equalsIgnoreCase("New")) {
                    return true;
                }
                status.add(STATUS_MAP.get(filter.getValue().toUpperCase(Locale.ROOT)));
            }
        }
        return status.isEmpty() || status.contains(Integer.parseInt(finding.getState()));
    }

    private static boolean fieldMatches(String fieldValue, List<String> allowedValues) {
        return allowedValues.isEmpty() ||
                allowedValues.contains(fieldValue.toUpperCase(Locale.ROOT));
    }
}
