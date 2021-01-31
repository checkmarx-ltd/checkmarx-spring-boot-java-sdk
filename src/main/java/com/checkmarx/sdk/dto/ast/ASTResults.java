package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.ast.report.AstSummaryResults;
import com.checkmarx.sdk.dto.ast.report.Finding;
import com.checkmarx.sdk.dto.ResultsBase;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ASTResults extends ResultsBase implements Serializable {
    private String scanId;
    private AstSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
}
