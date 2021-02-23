package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.dto.*;
import com.checkmarx.sdk.dto.ast.*;
import com.checkmarx.sdk.dto.ast.report.Finding;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.config.AstConfig;
import com.checkmarx.sdk.dto.ast.report.*;
import com.checkmarx.sdk.dto.sca.ClientType;
import com.checkmarx.sdk.utils.State;
import com.checkmarx.sdk.utils.UrlUtils;
import com.checkmarx.sdk.utils.scanner.client.httpClient.HttpClientHelper;
import com.checkmarx.sdk.utils.zip.CxZipUtils;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.scansummary.Severity;
import com.checkmarx.sdk.exception.CxHTTPClientException;
import com.checkmarx.sdk.utils.scanner.client.httpClient.CxHttpClient;
import com.checkmarx.sdk.config.ContentType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import static com.checkmarx.sdk.config.Constants.ENCODING;

public class AstClientHelper extends ScanClientHelper implements IScanClientHelper {

    private final String AST_SCAN_TYPE = "git";
    public static final String OAUTH2 = "oauth2:";
    private static final String TOKEN_SCM_SEPARATOR = "@";    
    private static final String CREDENTIALS_TYPE = "apiKey";
    private static final String ENGINE_TYPE_FOR_API = "sast";
    private static final String REF_TYPE_BRANCH = "branch";
    private static final String SUMMARY_PATH = "/api/scan-summary";
    private static final String SCAN_RESULTS_PATH = "/api/results";
    private static final String AUTH_PATH = "/auth/realms/organization/protocol/openid-connect/token";
    private static final String WEB_PROJECT_PATH = "/#/projects/%s/overview";
    private static final String URL_PARSING_EXCEPTION = "URL parsing exception.";
    private static final String DESCRIPTIONS_PATH = "/api/queries/descriptions";

    private static final int DEFAULT_PAGE_SIZE = 1000;
    private static final int NO_FINDINGS_CODE = 4004;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String API_VERSION = "*/*; version=1.0";
    private static final String SCAN_ID_PARAM_NAME = "scan-id";
    private static final String OFFSET_PARAM_NAME = "offset";
    private static final String LIMIT_PARAM_NAME = "limit";
    private static final String ID_PARAM_NAME = "ids";
    private static final int URL_MAX_CHAR_SIZE = 1490;

    public static  final String AST_GET_PROJECT_ID = "/api/projects/?offset=0&limit=20&names=";
    public static  final String AST_CREATE_PROJECT = "/api/projects/";

    private String scanId;

    public AstClientHelper(RestClientConfig config, Logger log) {
        super(config, log);

        AstConfig astConfig = this.config.getAstConfig();
        validate(astConfig);

        // Make sure we won't get URLs like "http://example.com//api/scans".
        String normalizedUrl = StringUtils.stripEnd(astConfig.getApiUrl(), "/");

        httpClient = createHttpClient(normalizedUrl);
        httpClient.setCustomHeader(HttpHeaders.ACCEPT, API_VERSION);
    }

    @Override
    public ResultsBase init() {
        log.debug("Initializing {} client.", getScannerDisplayName());
        ASTResults astResults = new ASTResults();
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
        String authUrl = UrlUtils.parseURLToString(config.getAstConfig().getApiUrl(), AUTH_PATH);
        return LoginSettings.builder()
                .accessControlBaseUrl(authUrl)
                .clientTypeForPasswordAuth(clientType)
                .build();
    }

    private ClientType getClientType() {
        AstConfig astConfig = config.getAstConfig();
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
    public ResultsBase initiateScan() {
        log.info("----------------------------------- Initiating {} Scan:------------------------------------",
                getScannerDisplayName());

        ASTResults astResults = new ASTResults();
        scanId = null;

        AstConfig astConfig = config.getAstConfig();
        try {
            SourceLocationType locationType = astConfig.getSourceLocationType();
            HttpResponse response;
            String projectId = determineProjectId(config.getProjectName());
            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(projectId, astConfig);
            } else {
                response = submitAllSourcesFromLocalDir(projectId, astConfig);
            }
            scanId = extractScanIdFrom(response);
            astResults.setScanId(scanId);
        } catch (Exception e) {
            log.error(e.getMessage());
            setState(State.FAILED);
            astResults.setException(new ScannerRuntimeException("Error creating scan.", e));
        }
        return astResults;
    }

    private HttpResponse submitAllSourcesFromLocalDir(String projectId,  ScanConfigBase configBase) throws IOException {
        log.info("Using local directory flow.");

        PathFilter filter = new PathFilter("", "", log);
        String sourceDir = this.config.getSourceDir();
        byte[] zipFile = CxZipUtils.getZippedSources(this.config, filter, sourceDir, log);
        
        return initiateScanForUpload(projectId, zipFile , configBase);
    }

    @Override
    protected ScanConfig getScanConfig() {
        String presetName = config.getAstConfig().getPresetName();
        if (StringUtils.isEmpty(presetName)) {
            throw new ScannerRuntimeException("Scan preset must be specified.");
        }

        String isIncremental = Boolean.toString(config.getAstConfig().isIncremental());
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
    public ResultsBase waitForScanResults() {
        ASTResults result;
        try {
            waitForScanToFinish(scanId);
            result = retrieveScanResults();
        } catch (ScannerRuntimeException e) {
            log.error(e.getMessage());
            result = new ASTResults();
            result.setException(e);
        }
        return result;
    }

    private ASTResults retrieveScanResults() {
        try {
            ASTResults result = new ASTResults();
            result.setScanId(scanId);

            AstSummaryResults scanSummary = getSummary();
            result.setSummary(scanSummary);

            List<Finding> findings = getFindings();
            result.setFindings(findings);

            String projectLink = getWebReportLink(config.getAstConfig().getWebAppUrl());
            result.setWebReportLink(projectLink);

            return result;
        } catch (IOException e) {
            String message = String.format("Error getting %s scan results.", getScannerDisplayName());
            throw new ScannerRuntimeException(message, e);
        }
    }

    @Override
    protected String getWebReportPath() throws UnsupportedEncodingException {
        return String.format(WEB_PROJECT_PATH,
                URLEncoder.encode(config.getProjectName(), ENCODING));
    }

    private AstSummaryResults getSummary() {
        AstSummaryResults result = new AstSummaryResults();

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
        int limit = config.getAstConfig().getResultsPageSize();
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
        } catch (ScannerRuntimeException e) {
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
            throw new ScannerRuntimeException(URL_PARSING_EXCEPTION, e);
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
            throw new ScannerRuntimeException(URL_PARSING_EXCEPTION, e);
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
            throw new ScannerRuntimeException("Error getting scan summary.", e);
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
            throw new ScannerRuntimeException(URL_PARSING_EXCEPTION, e);
        }
    }

    private static void setFindingCountsPerSeverity(List<SeverityCounter> nativeCounters, AstSummaryResults target) {
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
                .orElseThrow(() -> new ScannerRuntimeException("Invalid summary response."));
    }

    @Override
    public ResultsBase getLatestScanResults() {
        log.error("Unsupported Operation.");
        ASTResults result = new ASTResults();
        result.setException(new ScannerRuntimeException(new UnsupportedOperationException()));
        return result;
    }

    @Override
    public void close() {
        Optional.ofNullable(httpClient).ifPresent(CxHttpClient::close);
    }

    private void validate(ScanConfigBase astScaSastConfig) {
        log.debug("Validating config.");
        String error = null;
        if (astScaSastConfig == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isBlank(astScaSastConfig.getApiUrl())) {
            error = "%s API URL must be provided.";
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }

    protected String determineProjectId(String projectName) {
        String projectId = "";

        try {
            String failedMessage = "Failed to get scan ID for scan " + projectName;
            ProjectsList projectList = httpClient.getRequest(AST_GET_PROJECT_ID + projectName, ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    ProjectsList.class, HttpStatus.SC_OK, failedMessage, false);

            if(projectList.getProjects().size() == 0){
                projectId = createProject(projectName);
            }else{
                projectId = projectList.getProjects().get(0).getId();
            }

        } catch (Exception e) {
            throw new RestClientException(e.getMessage());
        }

        return projectId;
    }

    private synchronized String createProject(String projectName) {
        String projectId;

        Project project = new Project();
        project.setName(projectName);
        log.info("Sending the 'start scan' request.");
        
        try {
            StringEntity entity = HttpClientHelper.convertToStringEntity(project);
            
            ProjectId result  = httpClient.postRequest(AST_CREATE_PROJECT, ContentType.CONTENT_TYPE_APPLICATION_JSON, entity,
                    ProjectId.class, HttpStatus.SC_CREATED, "start the scan");
            projectId = result.getId();
            httpClient.setCustomHeader(HttpHeaders.ACCEPT, API_VERSION);
            
        } catch (IOException e) {
            throw new RestClientException(e.getMessage());
        }

        return projectId;
    }
    
    /**
     * @param repoInfo may represent an actual git repo or a presigned URL of an uploaded archive.
     * @param sourceLocation
     */
    protected AstScanStartHandler getScanStartHandler(RemoteRepositoryInfo repoInfo, SourceLocationType sourceLocation) {
        log.debug("Creating the handler object.");

        try {
            HandlerRef ref = getBranchToScan(repoInfo);
            URL effectiveUrl = repoInfo.getUrl();
            
            String username = "";

            GitCredentials credentials = calculateGitCredentials(repoInfo, sourceLocation);

            if (sourceLocation.REMOTE_REPOSITORY.equals(sourceLocation)) {
                effectiveUrl = sanitize(repoInfo.getUrl());
            }
            
            // The ref/username/credentials properties are mandatory even if not specified in repoInfo.
            return AstScanStartHandler.builder()
                    .ref(ref)
                    .username(username)
                    .credentials(credentials)
                    .repoUrl(effectiveUrl.toString())
                    .build();
        
        } catch (MalformedURLException e) {
            throw new ScannerRuntimeException(e.getMessage());
        }
    }

    private GitCredentials calculateGitCredentials(RemoteRepositoryInfo repoInfo, SourceLocationType sourceLocation) {
        String credentialsType = "";
        String token = "";
        
        if (sourceLocation.REMOTE_REPOSITORY.equals(sourceLocation)) {
            
            String authority = repoInfo.getUrl().getAuthority();
            //If token is supplied  authority field contains token@scm.com
            if (StringUtils.isNotEmpty(authority) && authority.contains(TOKEN_SCM_SEPARATOR)){
                token = authority.substring(0, authority.indexOf(TOKEN_SCM_SEPARATOR));
                //Gitlab use case. Authority field will be like oauth2:token@gitlab.com
                if(token.contains(OAUTH2)){
                    //remove the OAUTH2 header
                    token = token.split(OAUTH2)[1];
                }
                credentialsType = CREDENTIALS_TYPE;
            }
            
        }

        return GitCredentials.builder()
                .type(credentialsType)
                .value(token)
                .build();
    }

    protected HttpResponse sendStartScanRequest(RemoteRepositoryInfo repoInfo,
                                                SourceLocationType sourceLocation,
                                                String projectId) throws IOException {
        log.debug("Constructing the 'start scan' request");

        AstScanStartHandler handler = getScanStartHandler(repoInfo, sourceLocation);

        AstProjectToScan project = AstProjectToScan.builder()
                .id(projectId)
                //a constant value after AST API version 1.0 
                .type(AST_SCAN_TYPE)
                .handler(handler)
                .build();

        List<ScanConfig> apiScanConfig = Collections.singletonList(getScanConfig());

        AstStartScanRequest request = AstStartScanRequest.builder()
                .branch(repoInfo.getBranch())
                .project(project)
                .config(apiScanConfig)
                .build();

        if (sourceLocation.LOCAL_DIRECTORY.equals(sourceLocation)){
            request.setUploadUrl(repoInfo.getUrl().getPath());
        }
        
        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        log.info("Sending the 'start scan' request.");
        return httpClient.postRequest(CREATE_SCAN, ContentType.CONTENT_TYPE_APPLICATION_JSON, entity,
                HttpResponse.class, HttpStatus.SC_CREATED, "start the scan");
    }


}
