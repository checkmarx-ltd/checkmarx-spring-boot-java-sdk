package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.sca.SCAParams;
import com.cx.restclient.dto.DependencyScanResults;

import java.io.IOException;

public interface ScaClient {

    /**
     * Create new SCA scan for a new/existing project with a remote repository source
     * @return  Scan results object
     * @throws IOException
     */
    DependencyScanResults createScanFromRemoteRepo(SCAParams scaParams) throws IOException;
}