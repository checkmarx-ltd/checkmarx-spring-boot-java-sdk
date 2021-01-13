package com.cx.restclient.ast.dto.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class StartScanRequest {
    /**
     * What to scan.
     */
    private ProjectToScan project;

    /**
     * How to scan.
     */
    private List<ScanConfig> config;
}
