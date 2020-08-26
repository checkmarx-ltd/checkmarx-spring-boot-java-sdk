package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;

public interface AstClient {

    /**
     * Create new AST/SCA scan for a new/existing project
     *
     * @return scan results
     */
    ASTResultsWrapper scan(ScanParams scanParams);

    ASTResultsWrapper getLatestScanResults(ScanParams scanParams);
}