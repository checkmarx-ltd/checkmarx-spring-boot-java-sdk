package com.checkmarx.sdk.dto.sca;

import lombok.Getter;

@Getter
public class SbomInfoResponse {
    private String exportId;
    private String exportStatus;
    private String fileUrl;
}
