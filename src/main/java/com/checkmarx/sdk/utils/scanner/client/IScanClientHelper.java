package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.dto.ResultsBase;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.utils.State;

import java.io.File;
import java.io.IOException;


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
    ScanResults getReportContent(File file, FilterConfiguration filter) throws CheckmarxException;

    String initiateSbom(String scanId, String fileFormat,boolean hideDev,boolean showLicenses) throws IOException;
}
