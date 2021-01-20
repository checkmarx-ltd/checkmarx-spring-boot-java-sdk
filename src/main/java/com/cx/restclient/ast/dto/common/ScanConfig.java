package com.cx.restclient.ast.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Defines a single scan engine that will be used during scan.
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScanConfig {
    /**
     * Scan engine type.
     */
    private String type;

    /**
     * Engine-specific config.
     */
    private ScanConfigValue value;
}
