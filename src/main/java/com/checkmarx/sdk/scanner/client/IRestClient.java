package com.checkmarx.sdk.scanner.client;

import com.checkmarx.sdk.dto.IResults;
import com.checkmarx.sdk.utils.common.State;


/**
 * Common functionality for vulnerability scanners.
 */
public interface IRestClient {
    IResults init();

    IResults initiateScan();

    IResults waitForScanResults();

    IResults getLatestScanResults();

    void close();

    default State getState() {
        return State.SUCCESS;
    }
}
