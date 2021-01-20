package com.cx.restclient.ast.dto.sast.report;

import com.cx.restclient.ast.dto.common.SummaryResults;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Summary for external use as a part of general scan results.
 */
@Getter
@Setter
public class AstSastSummaryResults extends SummaryResults implements Serializable {
    private List<StatusCounter> statusCounters;
    private int totalCounter;
}
