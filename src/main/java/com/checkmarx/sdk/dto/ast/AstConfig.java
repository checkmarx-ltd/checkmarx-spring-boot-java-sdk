package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.ScaConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class AstConfig extends ScaConfig implements Serializable  {
    private String clientSecret;
    private String clientId;
    private String presetName;
    private boolean incremental;

    /**
     * Used as a paging parameter in scan result requests.
     */
    private int resultsPageSize;
}
