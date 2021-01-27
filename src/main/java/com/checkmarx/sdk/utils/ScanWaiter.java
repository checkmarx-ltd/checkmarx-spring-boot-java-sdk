package com.checkmarx.sdk.utils;

import com.checkmarx.sdk.exception.ScannerRuntimeException;

import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.utils.scanner.client.ScanClientHelper;
import com.checkmarx.sdk.utils.scanner.client.httpClient.CxHttpClient;
import com.checkmarx.sdk.config.ContentType;
import com.checkmarx.sdk.dto.ScanInfoResponse;
import com.checkmarx.sdk.dto.ScanStatus;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpStatus;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static com.checkmarx.sdk.config.Constants.ENCODING;


@RequiredArgsConstructor
public class ScanWaiter {
    private static final int CONNECTION_RETRIES = 3;
    private final CxHttpClient httpClient;
    private final RestClientConfig config;
    private final String scannerDisplayName;
    private long startTimestampSec;
    private final Logger log;
    private static final int DEFAULT_TIMEOUT = 30;
     
    public void waitForScanToFinish(String scanId) {
        startTimestampSec = System.currentTimeMillis() / 1000;
        Duration timeout = getTimeout(config);
        Duration pollInterval = getPollInterval(config);

        int maxErrorCount = getMaxErrorCount(config);
        AtomicInteger errorCounter = new AtomicInteger();

        try {
            String urlPath = String.format(ScanClientHelper.GET_SCAN, URLEncoder.encode(scanId, ENCODING));

            Awaitility.await()
                    .atMost(timeout)
                    .pollDelay(Duration.ZERO)
                    .pollInterval(pollInterval)
                    .until(() -> scanIsCompleted(urlPath, errorCounter, maxErrorCount));

        } catch (ConditionTimeoutException e) {
            String message = String.format(
                    "Failed to perform %s scan. The scan has been automatically aborted: " +
                            "reached the user-specified timeout (%d minutes).",
                    scannerDisplayName,
                    timeout.toMinutes());
            throw new ScannerRuntimeException(message);
        } catch (UnsupportedEncodingException e) {
            log.error("Unexpected error.", e);
        }
    }

    private static Duration getTimeout(RestClientConfig config) {
        return Duration.ofMinutes(DEFAULT_TIMEOUT);
    }

    private static Duration getPollInterval(RestClientConfig config) {
        int rawPollInterval = ObjectUtils.defaultIfNull(config.getProgressInterval(), 20);
        return Duration.ofSeconds(rawPollInterval);
    }

    private static int getMaxErrorCount(RestClientConfig config) {
        return CONNECTION_RETRIES;
    }

    private boolean scanIsCompleted(String path, AtomicInteger errorCounter, int maxErrorCount) {
        ScanInfoResponse response = null;
        String errorMessage = null;
        try {
            String failedMessage = scannerDisplayName + " scan";
            response = httpClient.getRequest(path, ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    ScanInfoResponse.class, HttpStatus.SC_OK, failedMessage, false);
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        boolean completedSuccessfully = false;
        if (response == null) {
            // A network error is likely to have occurred -> retry.
            countError(errorCounter, maxErrorCount, errorMessage);
        } else {
            ScanStatus status = extractScanStatusFrom(response);
            completedSuccessfully = handleScanStatus(status);
        }

        return completedSuccessfully;
    }

    private boolean handleScanStatus(ScanStatus status) {
        boolean completedSuccessfully = false;
        if (status == ScanStatus.COMPLETED) {
            completedSuccessfully = true;
        } else if (status == ScanStatus.FAILED) {
            // Scan has failed on the back end, no need to retry.
            throw new ScannerRuntimeException(String.format("Scan status is %s, aborting.", status));
        } else if (status == null) {
            log.warn("Unknown status.");
        }
        return completedSuccessfully;
    }

    private void countError(AtomicInteger errorCounter, int maxErrorCount, String message) {
        int currentErrorCount = errorCounter.incrementAndGet();
        int triesLeft = maxErrorCount - currentErrorCount;
        if (triesLeft < 0) {
            String fullMessage = String.format("Maximum number of errors was reached (%d), aborting.", maxErrorCount);
            throw new ScannerRuntimeException(fullMessage);
        } else {
            String note = (triesLeft == 0 ? "last attempt" : String.format("tries left: %d", triesLeft));
            log.info(String.format("Failed to get status from %s with the message: %s. Retrying (%s)",
                    scannerDisplayName,
                    message,
                    note));
        }
    }

    private ScanStatus extractScanStatusFrom(ScanInfoResponse response) {
        String rawStatus = response.getStatus();
        String elapsedTimestamp = SdkUtils.getTimestampSince(startTimestampSec);
        log.info(String.format("Waiting for %s scan results. Elapsed time: %s. Status: %s.",
                scannerDisplayName,
                elapsedTimestamp,
                rawStatus));
        return EnumUtils.getEnumIgnoreCase(ScanStatus.class, rawStatus);
    }
    
}
