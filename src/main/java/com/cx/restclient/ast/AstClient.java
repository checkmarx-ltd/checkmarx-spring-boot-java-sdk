package com.cx.restclient.ast;

import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.common.*;
import com.cx.restclient.common.State;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.configuration.PropertyFileLoader;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.SourceLocationType;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
import com.cx.restclient.httpClient.utils.HttpClientHelper;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class AstClient {

    private static final String LOCATION_HEADER = "Location";
    private static final String CREDENTIAL_TYPE_PASSWORD = "password";
    protected static final String ENCODING = StandardCharsets.UTF_8.name();

    protected final CxScanConfig config;
    protected final Logger log;

    protected CxHttpClient httpClient;

    private State state = State.SUCCESS;
    
   // protected static final PropertyFileLoader properties = PropertyFileLoader.getDefaultInstance();
    public static final String GET_SCAN ="/api/scans/%s";
    public static final String CREATE_SCAN = "/api/scans";
    public static final String GET_UPLOAD_URL = "/api/uploads";

    public AstClient(CxScanConfig config, Logger log) {
        validate(config, log);
        this.config = config;
        this.log = log;
    }

    protected abstract String getScannerDisplayName();

    protected abstract ScanConfig getScanConfig();

    protected abstract HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo);

    protected abstract HttpResponse submitAllSourcesFromLocalDir(String projectId, String zipFilePath) throws IOException;

    protected abstract String getWebReportPath() throws UnsupportedEncodingException;

    protected CxHttpClient createHttpClient(String baseUrl) {
        log.debug("Creating HTTP client.");
        CxHttpClient client = new CxHttpClient(baseUrl,
                config.getCxOrigin(),
                config.isDisableCertificateValidation(),
                false,      // AST clients don't support SSO.
                null,
                config.isProxy(),
                config.getProxyConfig(),
                log,
                config.getNTLM());
        //initializing Team Path to prevent null pointer in login when called from automation
        client.setTeamPathHeader("");

        return client;
    }

    private void validate(CxScanConfig config, Logger log) {
        if (config == null && log == null) {
            throw new ASTRuntimeException("Both scan config and log must be provided.");
        }
    }

    protected HttpResponse sendStartScanRequest(RemoteRepositoryInfo repoInfo,
                                                SourceLocationType sourceLocation,
                                                String projectId) throws IOException {
        log.debug("Constructing the 'start scan' request");

        ScanStartHandler handler = getScanStartHandler(repoInfo);

        ProjectToScan project = ProjectToScan.builder()
                .id(projectId)
                .type(sourceLocation.getApiValue())
                .handler(handler)
                .build();

        List<ScanConfig> apiScanConfig = Collections.singletonList(getScanConfig());

        StartScanRequest request = StartScanRequest.builder()
                .project(project)
                .config(apiScanConfig)
                .build();

        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        log.info("Sending the 'start scan' request.");
        return httpClient.postRequest(CREATE_SCAN, ContentType.CONTENT_TYPE_APPLICATION_JSON, entity,
                HttpResponse.class, HttpStatus.SC_CREATED, "start the scan");
    }

    protected HttpResponse submitSourcesFromRemoteRepo(ASTConfig config, String projectId) throws IOException {
        log.info("Using remote repository flow.");
        RemoteRepositoryInfo repoInfo = config.getRemoteRepositoryInfo();
        validateRepoInfo(repoInfo);

        URL sanitizedUrl = sanitize(repoInfo.getUrl());
        log.info("Repository URL: {}", sanitizedUrl);
        return sendStartScanRequest(repoInfo, SourceLocationType.REMOTE_REPOSITORY, projectId);
    }

    protected void waitForScanToFinish(String scanId) {

        log.info("------------------------------------Get {} Results:-----------------------------------", getScannerDisplayName());
        log.info("Waiting for {} scan to finish", getScannerDisplayName());

        AstWaiter waiter = new AstWaiter(httpClient, config, getScannerDisplayName(), log);
        waiter.waitForScanToFinish(scanId);
        log.info("{} scan finished successfully. Retrieving {} scan results.", getScannerDisplayName(), getScannerDisplayName());
    }

    /**
     * @param repoInfo may represent an actual git repo or a presigned URL of an uploaded archive.
     */
    private ScanStartHandler getScanStartHandler(RemoteRepositoryInfo repoInfo) {
        log.debug("Creating the handler object.");

        HandlerRef ref = getBranchToScan(repoInfo);

        // AST-SAST doesn't allow nulls here.
        String password = StringUtils.defaultString(repoInfo.getPassword());
        String username = StringUtils.defaultString(repoInfo.getUsername());

        GitCredentials credentials = GitCredentials.builder()
                .type(CREDENTIAL_TYPE_PASSWORD)
                .value(password)
                .build();

        URL effectiveRepoUrl = getEffectiveRepoUrl(repoInfo);

        // The ref/username/credentials properties are mandatory even if not specified in repoInfo.
        return ScanStartHandler.builder()
                .ref(ref)
                .username(username)
                .credentials(credentials)
                .url(effectiveRepoUrl.toString())
                .build();
    }

    protected URL getEffectiveRepoUrl(RemoteRepositoryInfo repoInfo) {
        return repoInfo.getUrl();
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
    private static URL sanitize(URL url) throws MalformedURLException {
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

            throw new ASTRuntimeException(message);
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
            throw new ASTRuntimeException("Unable to get scan ID.");
        }
        return result;
    }

    protected void handleInitError(Exception e, Results results) {
        String message = String.format("Failed to init %s client. %s", getScannerDisplayName(), e.getMessage());
        log.error(message);
        setState(State.FAILED);
        results.setException(new ASTRuntimeException(message, e));
    }

    protected HttpResponse initiateScanForUpload(String projectId, byte[] zipFile, String zipFilePath) throws IOException {
        String uploadedArchiveUrl = getSourcesUploadUrl();
        String cleanPath = uploadedArchiveUrl.split("\\?")[0];
        log.info("Uploading to: {}", cleanPath);
        uploadArchive(zipFile, uploadedArchiveUrl);

        //delete only if path not specified in the config
        //If zipFilePath is specified in config, it means that the user has prepared the zip file themselves. The user obviously doesn't want this file to be deleted.
        //If zipFilePath is NOT specified, Common Client will create the zip itself. After uploading the zip, Common Client should clean after itself (delete the zip file that it created).

        RemoteRepositoryInfo uploadedFileInfo = new RemoteRepositoryInfo();
        uploadedFileInfo.setUrl(new URL(uploadedArchiveUrl));

        return sendStartScanRequest(uploadedFileInfo, SourceLocationType.LOCAL_DIRECTORY, projectId);
    }

    private String getSourcesUploadUrl() throws IOException {
        JsonNode response = httpClient.postRequest(GET_UPLOAD_URL, null, null, JsonNode.class,
                HttpStatus.SC_OK, "get upload URL for sources");

        if (response == null || response.get("url") == null) {
            throw new ASTRuntimeException("Unable to get the upload URL.");
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
