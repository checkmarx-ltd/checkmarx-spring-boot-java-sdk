package com.cx.restclient.ast;

import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.common.*;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.SastScanConfigValue;
import com.cx.restclient.ast.dto.sast.report.*;
import com.cx.restclient.ast.dto.sca.ClientType;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.common.State;
import com.cx.restclient.common.UrlUtils;
import com.cx.restclient.common.zip.CxZipUtils;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.*;
import com.cx.restclient.dto.scansummary.Severity;
import com.cx.restclient.exception.CxHTTPClientException;
import com.cx.restclient.httpClient.CxHttpClient;
import com.cx.restclient.httpClient.utils.ContentType;
//import com.cx.restclient.osa.dto.ClientType;
//import com.cx.restclient.sast.utils.State;
//import com.cx.restclient.sast.utils.zip.CxZipUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class AstSastClient extends AstClient implements Scanner {
    private static final String ENGINE_TYPE_FOR_API = "sast";
    private static final String REF_TYPE_BRANCH = "branch";
    private static final String SUMMARY_PATH = properties.get("astSast.scanSummary");
    private static final String SCAN_RESULTS_PATH = properties.get("astSast.scanResults");
    private static final String AUTH_PATH = properties.get("astSast.authentication");
    private static final String WEB_PROJECT_PATH = properties.get("astSast.webProject");
    private static final String URL_PARSING_EXCEPTION = "URL parsing exception.";
    private static final String DESCRIPTIONS_PATH = properties.get("astSast.descriptionPath");

    private static final int DEFAULT_PAGE_SIZE = 1000;
    private static final int NO_FINDINGS_CODE = 4004;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_VERSION = "*/*; version=0.1";
    private static final String SCAN_ID_PARAM_NAME = "scan-id";
    private static final String OFFSET_PARAM_NAME = "offset";
    private static final String LIMIT_PARAM_NAME = "limit";
    private static final String ID_PARAM_NAME = "ids";
    private static final int URL_MAX_CHAR_SIZE = 1490;

    private String scanId;

    public AstSastClient(CxScanConfig config, Logger log) {
        super(config, log);

        AstSastConfig astConfig = this.config.getAstSastConfig();
        validate(astConfig);

        // Make sure we won't get URLs like "http://example.com//api/scans".
        String normalizedUrl = StringUtils.stripEnd(astConfig.getApiUrl(), "/");

        httpClient = createHttpClient(normalizedUrl);
        httpClient.addCustomHeader(HttpHeaders.ACCEPT, API_VERSION);
    }

    @Override
    public Results init() {
        log.debug("Initializing {} client.", getScannerDisplayName());
        AstSastResults astResults = new AstSastResults();
        try {
            ClientType clientType = getClientType();
            LoginSettings settings = getLoginSettings(clientType);
            httpClient.login(settings);
        } catch (Exception e) {
            super.handleInitError(e, astResults);
        }
        return astResults;
    }

    private LoginSettings getLoginSettings(ClientType clientType) throws MalformedURLException {
        String authUrl = UrlUtils.parseURLToString(config.getAstSastConfig().getApiUrl(), AUTH_PATH);
        return LoginSettings.builder()
                .accessControlBaseUrl(authUrl)
                .clientTypeForPasswordAuth(clientType)
                .build();
    }

    private ClientType getClientType() {
        AstSastConfig astConfig = config.getAstSastConfig();
        return ClientType.builder()
                .clientId(astConfig.getClientId())
                .clientSecret(astConfig.getClientSecret())
                .scopes("ast-api")
                .grantType("client_credentials")
                .build();
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SAST.getDisplayName();
    }
    
    @Override
    protected void uploadArchive(byte[] source, String uploadUrl) throws IOException {
        log.info("Uploading the zipped data.");

        HttpEntity request = new ByteArrayEntity(source);
        String baseAstUri = httpClient.getRootUri();
        httpClient.setRootUri(uploadUrl);

        try {
            // Relative path is empty, because we use the whole upload URL as the base URL for the HTTP client.
            // Content type is empty, because the server at uploadUrl throws an error if Content-Type is non-empty.
            httpClient.putRequest("", "", request, JsonNode.class, HttpStatus.SC_OK, "upload ZIP file");
        }
        finally {
            httpClient.setRootUri(baseAstUri);
        }
    }
    
    @Override
    public Results initiateScan() {
        log.info("----------------------------------- Initiating {} Scan:------------------------------------",
                getScannerDisplayName());

        AstSastResults astResults = new AstSastResults();
        scanId = null;

        AstSastConfig astConfig = config.getAstSastConfig();
        try {
            SourceLocationType locationType = astConfig.getSourceLocationType();
            HttpResponse response;
            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(astConfig, config.getProjectName());
            } else {

                response = submitAllSourcesFromLocalDir(config.getProjectName(), astConfig.getZipFilePath());
            }
            scanId = extractScanIdFrom(response);
            astResults.setScanId(scanId);
        } catch (Exception e) {
            log.error(e.getMessage());
            setState(State.FAILED);
            astResults.setException(new ASTRuntimeException("Error creating scan.", e));
        }
        return astResults;
    }

    protected HttpResponse submitAllSourcesFromLocalDir(String projectId, String zipFilePath) throws IOException {
        log.info("Using local directory flow.");

        PathFilter filter = new PathFilter("", "", log);
        String sourceDir = config.getSourceDir();
        byte[] zipFile = CxZipUtils.getZippedSources(config, filter, sourceDir, log);

        return initiateScanForUpload(projectId, zipFile, zipFilePath);
    }

    @Override
    protected ScanConfig getScanConfig() {
        String presetName = config.getAstSastConfig().getPresetName();
        if (StringUtils.isEmpty(presetName)) {
            throw new ASTRuntimeException("Scan preset must be specified.");
        }

        String isIncremental = Boolean.toString(config.getAstSastConfig().isIncremental());
        ScanConfigValue configValue = SastScanConfigValue.builder()
                .incremental(isIncremental)
                .presetName(presetName)
                .build();

        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(configValue)
                .build();
    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        // We need to return this object even if no branch is specified in repoInfo.
        return HandlerRef.builder()
                .type(REF_TYPE_BRANCH)
                .value(repoInfo.getBranch())
                .build();
    }

    @Override
    public Results waitForScanResults() {
        AstSastResults result;
        try {
            waitForScanToFinish(scanId);
            result = retrieveScanResults();
        } catch (ASTRuntimeException e) {
            log.error(e.getMessage());
            result = new AstSastResults();
            result.setException(e);
        }
        return result;
    }

    private AstSastResults retrieveScanResults() {
        try {
            AstSastResults result = new AstSastResults();
            result.setScanId(scanId);

            AstSastSummaryResults scanSummary = getSummary();
            result.setSummary(scanSummary);

            List<Finding> findings = getFindings();
            result.setFindings(findings);

            String projectLink = getWebReportLink(config.getAstSastConfig().getWebAppUrl());
            result.setWebReportLink(projectLink);

            return result;
        } catch (IOException e) {
            String message = String.format("Error getting %s scan results.", getScannerDisplayName());
            throw new ASTRuntimeException(message, e);
        }
    }

    @Override
    protected String getWebReportPath() throws UnsupportedEncodingException {
        return String.format(WEB_PROJECT_PATH,
                URLEncoder.encode(config.getProjectName(), ENCODING));
    }

    private AstSastSummaryResults getSummary() {
        AstSastSummaryResults result = new AstSastSummaryResults();

        String summaryUrl = getRelativeSummaryUrl();
        SummaryResponse summaryResponse = getSummaryResponse(summaryUrl);

        SingleScanSummary nativeSummary = getNativeSummary(summaryResponse);
        setFindingCountsPerSeverity(nativeSummary.getSeverityCounters(), result);

        result.setStatusCounters(nativeSummary.getStatusCounters());
        result.setTotalCounter(nativeSummary.getTotalCounter());

        return result;
    }

    private List<Finding> getFindings() throws IOException {
        int offset = 0;
        int limit = config.getAstSastConfig().getResultsPageSize();
        if (limit <= 0) {
            limit = DEFAULT_PAGE_SIZE;
        }

        List<Finding> allFindings = new ArrayList<>();
        while (true) {
            String relativeUrl = getRelativeResultsUrl(offset, limit);
            ScanResultsResponse response = getScanResultsResponse(relativeUrl);
            List<Finding> findingsFromResponse = response.getResults();
            allFindings.addAll(findingsFromResponse);
            offset += findingsFromResponse.size();
            if (offset >= response.getTotalCount()) {
                break;
            }
        }

        log.info(String.format("Total findings: %d", allFindings.size()));


        try {
            populateAdditionalFields(allFindings);
        } catch (ASTRuntimeException e) {
            log.error(e.getMessage());
        }

        return allFindings;
    }

    private void populateAdditionalFields(List<Finding> allFindings) throws IOException {

        final Map<String, QueryDescription> allQueryDescriptionMap = new HashMap<>();

        Set<String> queryIDs = allFindings.stream().map(finding -> finding.getQueryID()).collect(Collectors.toSet());

        while (queryIDs.size() > 0) {
            Set<String> processedQueryIds = new HashSet<String>();
            List<QueryDescription> queryDescriptionList = processQueryIDs(queryIDs, processedQueryIds);

            allQueryDescriptionMap.putAll(
                    queryDescriptionList.stream().collect(Collectors.toMap(QueryDescription::getQueryId, queryDescription -> queryDescription)));

            queryIDs.removeAll(processedQueryIds);
        }

        log.info(String.format("QueryIds with descriptions size: {} ", allQueryDescriptionMap.size()));

        allFindings.stream().forEach(finding -> {
            String queryId = finding.getQueryID();
            QueryDescription query = allQueryDescriptionMap.get(queryId);
            finding.setDescription(query.getResultDescription());
        });


    }

    private String prepareURL(Set<String> ids, Set<String> processedIds) {
        try {
            int lengthOtherParams = new URIBuilder().setPath(DESCRIPTIONS_PATH).setParameter(SCAN_ID_PARAM_NAME, scanId)
                    .build()
                    .toString().length();

            URIBuilder uriBuilder = new URIBuilder();
            uriBuilder.setPath(DESCRIPTIONS_PATH);

            int idsAllowedLength = URL_MAX_CHAR_SIZE - lengthOtherParams;

            List<NameValuePair> nameValues = new LinkedList<>();

            for (String id : ids) {
                idsAllowedLength = idsAllowedLength - ID_PARAM_NAME.length() - 2 - id.length();
                if (idsAllowedLength > 0) {
                    processedIds.add(id);
                    nameValues.add(new BasicNameValuePair(ID_PARAM_NAME, id));
                }
            }

            uriBuilder.setParameters(nameValues);
            String result = uriBuilder.setParameter(SCAN_ID_PARAM_NAME, scanId)
                    .build()
                    .toString();


            log.debug(String.format("Getting descriptions from %s", result));

            return result;
        } catch (URISyntaxException e) {
            throw new ASTRuntimeException(URL_PARSING_EXCEPTION, e);
        }
    }

    private String getRelativeResultsUrl(int offset, int limit) {
        try {
            String result = new URIBuilder()
                    .setPath(SCAN_RESULTS_PATH)
                    .setParameter(SCAN_ID_PARAM_NAME, scanId)
                    .setParameter(OFFSET_PARAM_NAME, Integer.toString(offset))
                    .setParameter(LIMIT_PARAM_NAME, Integer.toString(limit))
                    .build()
                    .toString();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Getting findings from %s", result));
            }

            return result;
        } catch (URISyntaxException e) {
            throw new ASTRuntimeException(URL_PARSING_EXCEPTION, e);
        }
    }

    private List<QueryDescription> processQueryIDs(Set<String> ids, Set<String> processedIds) throws IOException {

        String relativeUrl = prepareURL(ids, processedIds);

        List<QueryDescription> result = (List<QueryDescription>) httpClient.getRequest(relativeUrl,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                QueryDescription.class,
                HttpStatus.SC_OK,
                "retrieving queries description",
                true);

        return result;
    }

    private ScanResultsResponse getScanResultsResponse(String relativeUrl) throws IOException {
        return httpClient.getRequest(relativeUrl,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                ScanResultsResponse.class,
                HttpStatus.SC_OK,
                "retrieving scan results",
                false);
    }

    private SummaryResponse getSummaryResponse(String relativeUrl) {
        SummaryResponse result;
        try {
            result = httpClient.getRequest(relativeUrl,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    SummaryResponse.class,
                    HttpStatus.SC_OK,
                    "retrieving scan summary",
                    false);
        } catch (Exception e) {
            result = getEmptySummaryIfApplicable(e);
        }
        return result;
    }

    private SummaryResponse getEmptySummaryIfApplicable(Exception e) {
        SummaryResponse result;
        if (noFindingsWereDetected(e)) {
            result = new SummaryResponse();
            result.getScansSummaries().add(new SingleScanSummary());
        } else {
            throw new ASTRuntimeException("Error getting scan summary.", e);
        }
        return result;
    }

    /**
     * When no findings are detected, AST-SAST API returns the 404 status with a specific
     * error code, which is quite awkward.
     * Response example: {"code":4004,"message":"can't find all the provided scan ids","data":null}
     *
     * @return true: scan completed successfully and the result contains no findings (normal flow).
     * false: some other error has occurred (error flow).
     */
    private boolean noFindingsWereDetected(Exception e) {
        boolean result = false;
        if (e instanceof CxHTTPClientException) {
            CxHTTPClientException httpException = (CxHTTPClientException) e;
            if (httpException.getStatusCode() == HttpStatus.SC_NOT_FOUND &&
                    StringUtils.isNotEmpty(httpException.getResponseBody())) {
                try {
                    JsonNode body = objectMapper.readTree(httpException.getResponseBody());
                    result = (body.get("code").asInt() == NO_FINDINGS_CODE);
                } catch (Exception parsingException) {
                    log.warn("Error parsing the 'Not found' response.", parsingException);
                }
            }
        }
        return result;
    }


    private String getRelativeSummaryUrl() {
        try {
            String result = new URIBuilder()
                    .setPath(SUMMARY_PATH)
                    .setParameter("scan-ids", scanId)
                    .build()
                    .toString();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Getting summary from %s", result));
            }

            return result;
        } catch (URISyntaxException e) {
            throw new ASTRuntimeException(URL_PARSING_EXCEPTION, e);
        }
    }

    private static void setFindingCountsPerSeverity(List<SeverityCounter> nativeCounters, AstSastSummaryResults target) {
        if (nativeCounters == null) {
            return;
        }

        for (SeverityCounter counter : nativeCounters) {
            Severity parsedSeverity = EnumUtils.getEnum(Severity.class, counter.getSeverity());
            int value = counter.getCounter();
            if (parsedSeverity != null) {
                if (parsedSeverity == Severity.HIGH) {
                    target.setHighVulnerabilityCount(value);
                } else if (parsedSeverity == Severity.MEDIUM) {
                    target.setMediumVulnerabilityCount(value);
                } else if (parsedSeverity == Severity.LOW) {
                    target.setLowVulnerabilityCount(value);
                }
            }
        }
    }

    private static SingleScanSummary getNativeSummary(SummaryResponse summaryResponse) {
        return Optional.ofNullable(summaryResponse).map(SummaryResponse::getScansSummaries)
                // We are sending a single scan ID in the request and therefore expect exactly 1 scan summary.
                .filter(scanSummaries -> scanSummaries.size() == 1)
                .map(scanSummaries -> scanSummaries.get(0))
                .orElseThrow(() -> new ASTRuntimeException("Invalid summary response."));
    }

    @Override
    public Results getLatestScanResults() {
        log.error("Unsupported Operation.");
        AstSastResults result = new AstSastResults();
        result.setException(new ASTRuntimeException(new UnsupportedOperationException()));
        return result;
    }

    @Override
    public void close() {
        Optional.ofNullable(httpClient).ifPresent(CxHttpClient::close);
    }

    private void validate(ASTConfig astSastConfig) {
        log.debug("Validating config.");
        String error = null;
        if (astSastConfig == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isBlank(astSastConfig.getApiUrl())) {
            error = "%s API URL must be provided.";
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }
}
