package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.dto.ResultsBase;
import com.checkmarx.sdk.utils.State;


/**
 * Common functionality for vulnerability scanners.
 */
public interface IScanClientHelper {
    ResultsBase init();

    ResultsBase initiateScan();

    ResultsBase waitForScanResults();

    ResultsBase getLatestScanResults();

    void close();

    default State getState() {
        return State.SUCCESS;
    }
}
