package com.checkmarx.sdk.dto.cx;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CxProjectBranchingStatus {
    private Integer id;
    private Integer originalProjectId;
    private String originalProjectName;
    private Integer branchedOnScanId;
    private Integer branchedProjectId;
    private LocalDateTime timestamp;
    private String comment;
    private Status status;
    private String errorMessage;

    @Value
    @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class Status {
        private Integer id;
        private String value;
    }
}
