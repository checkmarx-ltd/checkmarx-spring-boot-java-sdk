package com.checkmarx.sdk.dto.sca;

import com.checkmarx.sdk.dto.sca.ScaScanStartHandler;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScaProjectToScan {
    private String id;
    private String type;
    private ScaScanStartHandler handler;
}
