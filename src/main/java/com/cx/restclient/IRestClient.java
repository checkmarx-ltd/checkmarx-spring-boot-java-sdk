package com.cx.restclient;

import com.cx.restclient.dto.IResults;
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
