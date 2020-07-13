package com.checkmarx.sdk.dto.ast;


import com.cx.restclient.ast.dto.common.SummaryResults;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ASTResults {
    private String scanId;

    public SummaryResults getSummary() {
        return null;
    }
}
