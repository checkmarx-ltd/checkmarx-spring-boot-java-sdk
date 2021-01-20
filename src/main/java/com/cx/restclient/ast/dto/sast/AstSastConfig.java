package com.cx.restclient.ast.dto.sast;

import com.cx.restclient.ast.dto.common.ASTConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class AstSastConfig extends ASTConfig implements Serializable  {
    private String clientSecret;
    private String clientId;
    private String presetName;
    private boolean incremental;

    /**
     * Used as a paging parameter in scan result requests.
     */
    private int resultsPageSize;
}
