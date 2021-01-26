package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.ast.report.AstSastSummaryResults;
import com.checkmarx.sdk.dto.ast.report.Finding;
import com.checkmarx.sdk.dto.ResultsBase;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AstSastResults extends ResultsBase implements Serializable {
    private String scanId;
    private AstSastSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
}
