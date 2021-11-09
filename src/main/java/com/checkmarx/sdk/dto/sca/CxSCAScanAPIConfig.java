package com.checkmarx.sdk.dto.sca;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.checkmarx.sdk.dto.ScanConfigValue;

@Getter
@Setter
@NoArgsConstructor
public class CxSCAScanAPIConfig implements ScanConfigValue {
    private String includeSourceCode;
}

