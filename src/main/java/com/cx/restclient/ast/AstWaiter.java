package com.cx.restclient.ast;

import com.checkmarx.sdk.exception.ASTRuntimeException;
//import com.cx.restclient.common.ShragaUtils;
import com.cx.restclient.common.ShragaUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import com.cx.restclient.ast.dto.common.ScanInfoResponse;
import com.cx.restclient.ast.dto.common.ScanStatus;
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

import static com.cx.restclient.ast.AstClient.ENCODING;

@RequiredArgsConstructor
public class AstWaiter {
    private final CxHttpClient httpClient;
    private final CxScanConfig config;
    private final String scannerDisplayName;
    private long startTimestampSec;
    private final Logger log;

    public void waitForScanToFinish(String scanId) {
        startTimestampSec = System.currentTimeMillis() / 1000;
        Duration timeout = getTimeout(config);
        Duration pollInterval = getPollInterval(config);

        int maxErrorCount = getMaxErrorCount(config);
        AtomicInteger errorCounter = new AtomicInteger();

        try {
            String urlPath = String.format(AstClient.GET_SCAN, URLEncoder.encode(scanId, ENCODING));

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
            throw new ASTRuntimeException(message);
        } catch (UnsupportedEncodingException e) {
            log.error("Unexpected error.", e);
        }
    }

    private static Duration getTimeout(CxScanConfig config) {
        Integer rawTimeout = config.getOsaScanTimeoutInMinutes();
        final int DEFAULT_TIMEOUT = 30;
        rawTimeout = rawTimeout != null && rawTimeout > 0 ? rawTimeout : DEFAULT_TIMEOUT;
        return Duration.ofMinutes(rawTimeout);
    }

    private static Duration getPollInterval(CxScanConfig config) {
        int rawPollInterval = ObjectUtils.defaultIfNull(config.getOsaProgressInterval(), 20);
        return Duration.ofSeconds(rawPollInterval);
    }

    private static int getMaxErrorCount(CxScanConfig config) {
        return ObjectUtils.defaultIfNull(config.getConnectionRetries(), 3);
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
            throw new ASTRuntimeException(String.format("Scan status is %s, aborting.", status));
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
            throw new ASTRuntimeException(fullMessage);
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
        String elapsedTimestamp = ShragaUtils.getTimestampSince(startTimestampSec);
        log.info(String.format("Waiting for %s scan results. Elapsed time: %s. Status: %s.",
                scannerDisplayName,
                elapsedTimestamp,
                rawStatus));
        return EnumUtils.getEnumIgnoreCase(ScanStatus.class, rawStatus);
    }
    
}
