package com.checkmarx.sdk.dto.ast.report;

import com.checkmarx.sdk.dto.SummaryResults;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Summary for external use as a part of general scan results.
 */
@Getter
@Setter
public class AstSummaryResults extends SummaryResults implements Serializable {
    private List<StatusCounter> statusCounters;
    private int totalCounter;
}
