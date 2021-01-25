package com.checkmarx.sdk.dto.ast;

import com.checkmarx.sdk.dto.ast.report.AstSastSummaryResults;
import com.checkmarx.sdk.dto.ast.report.Finding;
import com.checkmarx.sdk.dto.IResults;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AstSastResults extends IResults implements Serializable {
    private String scanId;
    private AstSastSummaryResults summary;
    private String webReportLink;
    private List<Finding> findings;
}
