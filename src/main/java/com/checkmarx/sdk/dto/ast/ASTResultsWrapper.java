package com.checkmarx.sdk.dto.ast;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ASTResultsWrapper {
    private SCAResults scaResults;
    private ASTResults astResults;
}
