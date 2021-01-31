package com.checkmarx.sdk.dto;

import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.sca.SCAResults;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AstScaResults {
    private SCAResults scaResults;
    private ASTResults astResults;
}
