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

        for (Filter f : filters) {
            Filter.Type type = f.getType();
            String value = f.getValue();
            if (type.equals(Filter.Type.SEVERITY)) {
                severity.add(value.toUpperCase(Locale.ROOT));
            } else if (type.equals(Filter.Type.TYPE)) {
                category.add(value.toUpperCase(Locale.ROOT));
            } else if (type.equals(Filter.Type.CWE)) {
                cwe.add(value.toUpperCase(Locale.ROOT));
            }
        }
        if (!severity.isEmpty() && !severity.contains(findingGroup.getSeverity().toUpperCase(Locale.ROOT))) {
            return false;
        }
        if (!cwe.isEmpty() && !cwe.contains(findingGroup.getCweId())) {
            return false;
        }

        return category.isEmpty() || category.contains(findingGroup.getName().toUpperCase(Locale.ROOT));
    }

    @Override
    public boolean passesFilter(ResultType finding, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        List<Integer> status = new ArrayList<>();

        for (Filter f : filters) {
            if (f.getType().equals(Filter.Type.STATUS)) {
                //handle New Status separately (this field is Status as opposed to State for the others
                if (f.getValue().equalsIgnoreCase("New")) {
                    if (finding.getStatus().equalsIgnoreCase("New")) {
                        return true;
                    }
                }
                status.add(STATUS_MAP.get(f.getValue().toUpperCase(Locale.ROOT)));
            }
        }
        return status.isEmpty() || status.contains(Integer.parseInt(finding.getState()));
    }
}
