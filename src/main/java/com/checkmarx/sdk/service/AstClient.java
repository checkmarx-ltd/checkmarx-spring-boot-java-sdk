package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;

import java.io.IOException;

public interface AstClient {

    /**
     * Create new SCA scan for a new/existing project with a remote repository source
     * @return scan results
     */
    ASTResultsWrapper scanRemoteRepo(ScanParams scaParams) throws IOException;

    ASTResultsWrapper scanLocalSource(ScanParams scanParams) throws IOException;
}