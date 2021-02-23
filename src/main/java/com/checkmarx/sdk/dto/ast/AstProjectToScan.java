package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.sca.ScaScanStartHandler;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AstProjectToScan {
    private String id;
    private String type;
    private AstScanStartHandler handler;
}
