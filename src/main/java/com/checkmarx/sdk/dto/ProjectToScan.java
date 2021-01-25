package com.checkmarx.sdk.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProjectToScan {
    private String id;
    private String type;
    private ScanStartHandler handler;
}
