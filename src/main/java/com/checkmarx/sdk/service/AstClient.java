package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.sca.CombinedResults;
import com.checkmarx.sdk.dto.sca.ScanParams;

import java.io.IOException;

public interface AstClient {

    /**
     * Create new SCA scan for a new/existing project with a remote repository source
     * @return scan results
     */
    CombinedResults scanRemoteRepo(ScanParams scaParams) throws IOException;
}