package com.checkmarx.sdk.dto.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ASTResultsWrapper {

    private SCAResults scaResults = null;
    private ASTResults astResults = null;
    
    public ASTResultsWrapper(){}
}
