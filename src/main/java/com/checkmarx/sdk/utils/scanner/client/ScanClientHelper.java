package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.dto.*;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.utils.ScanWaiter;
import com.checkmarx.sdk.utils.State;
import com.checkmarx.sdk.utils.UrlUtils;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.ResultsBase;
import com.checkmarx.sdk.dto.SourceLocationType;
import com.checkmarx.sdk.utils.scanner.client.httpClient.CxHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public abstract class ScanClientHelper {

    private static final String LOCATION_HEADER = "Location";


    protected final RestClientConfig config;
    protected final Logger log;

    protected CxHttpClient httpClient;

    private State state = State.SUCCESS;
    
    public static final String GET_SCAN ="/api/scans/%s";
    public static final String CREATE_SCAN = "/api/scans";
    public static final String GET_UPLOAD_URL = "/api/uploads";
    public static final String CX_FLOW_SCAN_ORIGIN_NAME = "CxFlow";
    
    public ScanClientHelper(RestClientConfig config, Logger log) {
        validate(config, log);
        this.config = config;
        this.log = log;
    }

    protected abstract String getScannerDisplayName();

    protected abstract ScanConfig getScanConfig();

    protected abstract HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo);
    
    protected abstract String getWebReportPath() throws UnsupportedEncodingException;
    
    protected CxHttpClient createHttpClient(String baseUrl) {
        log.debug("Creating HTTP client.");
        CxHttpClient client = new CxHttpClient(baseUrl,
                config.isDisableCertificateValidation(),
                log);
        //initializing Team Path to prevent null pointer in login when called from automation
        client.setTeamPathHeader("");

        return client;
    }

    private void validate(RestClientConfig config, Logger log) {
        if (config == null && log == null) {
            throw new ScannerRuntimeException("Both scan config and log must be provided.");
        }
    }

    protected abstract HttpResponse sendStartScanRequest(RemoteRepositoryInfo repoInfo,
                                                SourceLocationType sourceLocation,
                                                String projectId)throws IOException ;

    protected HttpResponse submitSourcesFromRemoteRepo(String projectId, ScanConfigBase baseConfig) throws IOException {
        log.info("Using remote repository flow.");
        RemoteRepositoryInfo repoInfo = baseConfig.getRemoteRepositoryInfo();
        validateRepoInfo(repoInfo);

        URL sanitizedUrl = sanitize(repoInfo.getUrl());
        log.info("Repository URL: {}", sanitizedUrl);
        return sendStartScanRequest(repoInfo, SourceLocationType.REMOTE_REPOSITORY, projectId);
    }

    protected void waitForScanToFinish(String scanId) {

        log.info("------------------------------------Get {} Results:-----------------------------------", getScannerDisplayName());
        log.info("Waiting for {} scan to finish", getScannerDisplayName());

        ScanWaiter waiter = new ScanWaiter(httpClient, config, getScannerDisplayName(), log);
        waiter.waitForScanToFinish(scanId);
        log.info("{} scan finished successfully. Retrieving {} scan results.", getScannerDisplayName(), getScannerDisplayName());
    }
    

    protected String getWebReportLink(String baseUrl) {
        String result = null;
        String warning = null;
        try {
            if (StringUtils.isNotEmpty(baseUrl)) {
                String path = getWebReportPath();
                result = UrlUtils.parseURLToString(baseUrl, path);
            } else {
                warning = "Web app URL is not specified.";
            }
        } catch (MalformedURLException e) {
            warning = "Invalid web app URL.";
        } catch (Exception e) {
            warning = "General error.";
        }

        Optional.ofNullable(warning)
                .ifPresent(warn -> log.warn("Unable to generate web report link. {}", warn));

        return result;
    }

    /**
     * Removes the userinfo part of the input URL (if present), so that the URL may be logged safely.
     * The URL may contain userinfo when a private repo is scanned.
     */
    protected URL sanitize(URL url) throws MalformedURLException {
        return new URL(url.getProtocol(), url.getHost(), url.getFile());
    }

    private void validateRepoInfo(RemoteRepositoryInfo repoInfo) {
        log.debug("Validating remote repository info.");
        if (repoInfo == null) {
            String message = String.format(
                    "%s must be provided in %s configuration when using source location of type %s.",
                    RemoteRepositoryInfo.class.getName(),
                    getScannerDisplayName(),
                    SourceLocationType.REMOTE_REPOSITORY.name());

            throw new ScannerRuntimeException(message);
        }
    }

    protected String extractScanIdFrom(HttpResponse response) {
        String result = null;

        log.debug("Extracting scan ID from the '{}' response header.", LOCATION_HEADER);
        if (response != null && response.getLastHeader(LOCATION_HEADER) != null) {
            // Expecting values like
            //      /api/scans/1ecffa00-0e42-49b2-8755-388b9f6a9293
            //      /07e5b4b0-184a-458e-9d82-7f3da407f940
            String urlPathWithScanId = response.getLastHeader(LOCATION_HEADER).getValue();
            result = FilenameUtils.getName(urlPathWithScanId);
        }

        if (StringUtils.isNotEmpty(result)) {

            log.info("Scan started successfully. Scan ID: {}", result);
        } else {
            throw new ScannerRuntimeException("Unable to get scan ID.");
        }
        return result;
    }

    protected void handleInitError(Exception e, ResultsBase results) {
        String message = String.format("Failed to init %s client. %s", getScannerDisplayName(), e.getMessage());
        log.error(message);
        setState(State.FAILED);
        results.setException(new ScannerRuntimeException(message, e));
    }

    protected HttpResponse initiateScanForUpload(String projectId, byte[] zipFile, ScanConfigBase configBase) throws IOException {
        String uploadedArchiveUrl = getSourcesUploadUrl();
        String cleanPath = uploadedArchiveUrl.split("\\?")[0];
        log.info("Uploading to: {}", cleanPath);
        uploadArchive(zipFile, uploadedArchiveUrl);

        //delete only if path not specified in the config
        //If zipFilePath is specified in config, it means that the user has prepared the zip file themselves. The user obviously doesn't want this file to be deleted.
        //If zipFilePath is NOT specified, we will create the zip itself. After uploading the zip, the code should clean after itself (delete the zip file that it created).

        configBase.getRemoteRepositoryInfo().setUrl(new URL(uploadedArchiveUrl));

        return sendStartScanRequest(configBase.getRemoteRepositoryInfo(), SourceLocationType.LOCAL_DIRECTORY, projectId);
    }

    private String getSourcesUploadUrl() throws IOException {
        JsonNode response = httpClient.postRequest(GET_UPLOAD_URL, null, null, JsonNode.class,
                HttpStatus.SC_OK, "get upload URL for sources");

        if (response == null || response.get("url") == null) {
            throw new ScannerRuntimeException("Unable to get the upload URL.");
        }

        return response.get("url").asText();
    }

    protected abstract void uploadArchive(byte[] source, String uploadUrl) throws IOException;

  

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
