package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.ScanConfigValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AST-SAST-specific config parameters. Should be expanded with other supported properties.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SastScanConfigValue implements ScanConfigValue {
    private String presetName;

    /**
     * Must be a string ("true" or "false").
     */
    private String incremental;
}
