package com.cx.restclient.ast.dto.sast.report;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public
class SingleScanSummary implements Serializable {
    private String scanId;
    private List<SeverityCounter> severityCounters = new ArrayList<>();
    private List<StatusCounter> statusCounters = new ArrayList<>();
    private int totalCounter;
}
