package com.checkmarx.sdk.dto.ast;


import com.cx.restclient.ast.dto.common.SummaryResults;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ASTResults {
    AstSastResults results;
}
