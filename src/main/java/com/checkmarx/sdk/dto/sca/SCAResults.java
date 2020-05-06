package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.Filter;
import lombok.Builder;

import java.util.Map;

@Builder
public class SCAResults {
    private int totalPackages;
    private int directPackages;
    private int totalOutdatedPackages;
    private double riskScore;
    private Map<Filter.Severity, Integer> findingCounts;
    private String webReportLink;
}
