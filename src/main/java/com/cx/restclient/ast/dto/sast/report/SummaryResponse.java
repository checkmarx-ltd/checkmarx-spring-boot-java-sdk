package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SummaryResponse implements Serializable {
    private List<SingleScanSummary> scansSummaries = new ArrayList<>();
    private int totalCount;
}
