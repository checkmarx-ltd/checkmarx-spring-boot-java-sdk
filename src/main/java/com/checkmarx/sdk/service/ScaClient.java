package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.sca.SCAParams;
import com.checkmarx.sdk.dto.sca.SCAResults;

import java.io.IOException;

public interface ScaClient {

    /**
     * Create new SCA scan for a new/existing project with a remote repository source
     * @return scan results
     */
    SCAResults scanRemoteRepo(SCAParams scaParams) throws IOException;

    /**
     * Create new SCA scan for a new/existing project with a local zip source
     * @return scan results
     */
    SCAResults scanLocalSource(SCAParams scaParams) throws IOException;
}