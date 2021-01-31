package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.dto.*;
import com.checkmarx.sdk.dto.sca.Summary;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.sca.ScaConfig;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.dto.sca.*;
import com.checkmarx.sdk.dto.sca.report.ScaSummaryBaseFormat;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.dto.sca.report.Package;

import com.checkmarx.sdk.utils.State;
import com.checkmarx.sdk.utils.UrlUtils;
import com.checkmarx.sdk.utils.zip.CxZipUtils;
import com.checkmarx.sdk.utils.zip.NewCxZipFile;
import com.checkmarx.sdk.utils.zip.Zipper;
import com.checkmarx.sdk.config.RestClientConfig;


import com.checkmarx.sdk.exception.CxHTTPClientException;
import com.checkmarx.sdk.utils.scanner.client.httpClient.CxHttpClient;
import com.checkmarx.sdk.config.ContentType;
import com.checkmarx.sdk.utils.scanner.client.httpClient.HttpClientHelper;

import com.checkmarx.sdk.dto.sca.CxSCAResolvingConfiguration;
import com.checkmarx.sdk.utils.sca.CxSCAFileSystemUtils;
import com.checkmarx.sdk.utils.sca.fingerprints.CxSCAScanFingerprints;
import com.checkmarx.sdk.utils.sca.fingerprints.FingerprintCollector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import static com.checkmarx.sdk.config.Constants.ENCODING;

/**
 * SCA - Software Composition Analysis - is the successor of OSA.
 */
public class ScaClientHelper extends ScanClientHelper implements IScanClientHelper {
    
    private static final String RISK_MANAGEMENT_API = "/risk-management/";
    private static final String PROJECTS = RISK_MANAGEMENT_API + "projects";
    private static final String SUMMARY_REPORT = RISK_MANAGEMENT_API + "riskReports/%s/summary";
    private static final String FINDINGS = RISK_MANAGEMENT_API + "riskReports/%s/vulnerabilities";
    private static final String PACKAGES = RISK_MANAGEMENT_API + "riskReports/%s/packages";
    private static final String LATEST_SCAN = RISK_MANAGEMENT_API + "riskReports?size=1&projectId=%s";
    private static final String WEB_REPORT = "/#/projects/%s/reports/%s";
    private static final String RESOLVING_CONFIGURATION_API = "/settings/projects/%s/resolving-configuration";

    private static final String REPORT_SCA_PACKAGES = "cxSCAPackages";
    private static final String REPORT_SCA_FINDINGS = "cxSCAVulnerabilities";
    private static final String REPORT_SCA_SUMMARY = "cxSCASummary";
    private static final String JSON_EXTENSION = ".json";
    public static final String AUTHENTICATION = "identity/connect/token";
    private static final String ENGINE_TYPE_FOR_API = "sca";

    private static final String TENANT_HEADER_NAME = "Account-Name";


    public static final String CX_REPORT_LOCATION = File.separator + "Checkmarx" + File.separator + "Reports";

    private static final ObjectMapper caseInsensitiveObjectMapper = new ObjectMapper()
            // Ignore any fields that can be added to SCA API in the future.
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // We need this feature to properly deserialize finding severity,
            // e.g. "High" (in JSON) -> Severity.HIGH (in Java).
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    private final ScaConfig scaConfig;


    private String projectId;
    private String scanId;
    private final FingerprintCollector fingerprintCollector;
    private CxSCAResolvingConfiguration resolvingConfiguration;
    private static final String FINGERPRINT_FILE_NAME = ".cxsca.sig";

    public ScaClientHelper(RestClientConfig config, Logger log) {
        super(config, log);

        this.scaConfig = config.getScaConfig();
        validate(scaConfig);

        httpClient = createHttpClient(scaConfig.getApiUrl());
        this.resolvingConfiguration = null;
        fingerprintCollector = new FingerprintCollector(log);
        // Pass tenant name in a custom header. This will allow to get token from on-premise access control server
        // and then use this token for SCA authentication in cloud.
        httpClient.addCustomHeader(TENANT_HEADER_NAME, config.getScaConfig().getTenant());
        httpClient.addCustomHeader(CxHttpClient.ORIGIN_HEADER, ScanClientHelper.CX_FLOW_SCAN_ORIGIN_NAME);
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SCA.getDisplayName();
    }

    @Override
    protected ScanConfig getScanConfig() {
        return ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .build();
    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        if (StringUtils.isNotEmpty(repoInfo.getBranch())) {
            // If we pass the branch to start scan API, the API will return an error:
            // "Git references (branch, commit ID, etc.) are not yet supported."
            //
            // We can't just ignore the branch, because it will lead to confusion.
            String message = String.format("Branch specification is not yet supported by %s.", getScannerDisplayName());
            throw new ScannerRuntimeException(message);
        }
        return null;
    }

    /**
     * Transforms the repo URL if credentials are specified in repoInfo.
     */
    @Override
    protected URL getEffectiveRepoUrl(RemoteRepositoryInfo repoInfo) {
        URL result;
        URL initialUrl = repoInfo.getUrl();

        // Otherwise we may get something like "https://mytoken:null@github.com".
        String username = StringUtils.defaultString(repoInfo.getUsername());
        String password = StringUtils.defaultString(repoInfo.getPassword());

        try {
            if (StringUtils.isNotEmpty(username) || StringUtils.isNotEmpty(password)) {
                log.info("Adding credentials as the userinfo part of the URL, because {} only supports this kind of authentication.",
                        getScannerDisplayName());

                result = new URIBuilder(initialUrl.toURI())
                        .setUserInfo(username, password)
                        .build()
                        .toURL();
            } else {
                result = repoInfo.getUrl();
            }
        } catch (Exception e) {
            throw new ScannerRuntimeException("Error getting effective repo URL.");
        }
        return result;
    }

    @Override
    public ResultsBase init() {
        log.debug("Initializing {} client.", getScannerDisplayName());
        SCAResults scaResults = new SCAResults();
        try {
            login();
        } catch (Exception e) {
            super.handleInitError(e, scaResults);
        }
        return scaResults;
    }

    public CxSCAResolvingConfiguration getCxSCAResolvingConfigurationForProject(String projectId) throws IOException {
        log.info("Resolving configuration for project: {}", projectId);
        String path = String.format(RESOLVING_CONFIGURATION_API, URLEncoder.encode(projectId, ENCODING));

        return httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                CxSCAResolvingConfiguration.class,
                HttpStatus.SC_OK,
                "get CxSCA resolving configuration",
                false);
    }

    /**
     * Waits for SCA scan to finish, then gets scan results.
     *
     * @throws ScannerRuntimeException in case of a network error, scan failure or when scan is aborted by timeout.
     */
    @Override
    public ResultsBase waitForScanResults() {
        SCAResults scaResults;
        try {
            waitForScanToFinish(scanId);
            scaResults = tryGetScanResults().orElseThrow(() -> new ScannerRuntimeException("Unable to get scan results: scan not found."));
        } catch (ScannerRuntimeException e) {
            log.error(e.getMessage());
            scaResults = new SCAResults();
            scaResults.setException(e);
        }
        return scaResults;
    }

    @Override
    protected void uploadArchive(byte[] source, String uploadUrl) throws IOException {
        log.info("Uploading the zipped data.");
        CxHttpClient uploader = null;
        HttpEntity request = new ByteArrayEntity(source);

        try {
            uploader = createHttpClient(uploadUrl);

            // Relative path is empty, because we use the whole upload URL as the base URL for the HTTP client.
            // Content type is empty, because the server at uploadUrl throws an error if Content-Type is non-empty.
            uploader.putRequest("", "", request, JsonNode.class, HttpStatus.SC_OK, "upload ZIP file");
        }finally {
            Optional.ofNullable(uploader).ifPresent(CxHttpClient::close);
        }

    }
    
    @Override
    public ResultsBase initiateScan() {
        log.info("----------------------------------- Initiating {} Scan:------------------------------------",
                getScannerDisplayName());
        SCAResults scaResults = new SCAResults();
        scanId = null;
        projectId = null;
        try {
            ScaConfig scaConfig = config.getScaConfig();
            SourceLocationType locationType = scaConfig.getSourceLocationType();
            HttpResponse response;

            projectId = resolveRiskManagementProject();
            boolean isManifestAndFingerprintsOnly = !config.getScaConfig().isIncludeSources();
            if (isManifestAndFingerprintsOnly) {
                this.resolvingConfiguration = getCxSCAResolvingConfigurationForProject(this.projectId);
                log.info("Got the following manifest patterns {}", this.resolvingConfiguration.getManifests());
                log.info("Got the following fingerprint patterns {}", this.resolvingConfiguration.getFingerprints());
            }

            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(scaConfig, projectId);
            } else {
                if (scaConfig.isIncludeSources()) {
                    response = submitAllSourcesFromLocalDir(projectId, this.scaConfig.getZipFilePath());
                } else {
                    response = submitManifestsAndFingerprintsFromLocalDir(projectId);
                }
            }
            this.scanId = extractScanIdFrom(response);
            scaResults.setScanId(scanId);
        } catch (Exception e) {
            log.error(e.getMessage());
            setState(State.FAILED);
            scaResults.setException(new ScannerRuntimeException("Error creating scan.", e));
        }
        return scaResults;
    }

    protected HttpResponse submitAllSourcesFromLocalDir(String projectId, String zipFilePath) throws IOException {
        log.info("Using local directory flow.");

        PathFilter filter = new PathFilter("", "", log);
        String sourceDir = config.getSourceDir();
        byte[] zipFile = CxZipUtils.getZippedSources(config, filter, sourceDir, log);

        return initiateScanForUpload(projectId, zipFile, zipFilePath);
    }

    private HttpResponse submitManifestsAndFingerprintsFromLocalDir(String projectId) throws IOException {
        log.info("Using manifest only and fingerprint flow");

        String sourceDir = config.getSourceDir();

        PathFilter userFilter = new PathFilter("", "", log);
        if (ArrayUtils.isNotEmpty(userFilter.getIncludes()) && !ArrayUtils.contains(userFilter.getIncludes(), "**")) {
            userFilter.addToIncludes("**");
        }
        Set<String> scannedFileSet = new HashSet<>(Arrays.asList(CxSCAFileSystemUtils.scanAndGetIncludedFiles(sourceDir, userFilter)));

        PathFilter manifestIncludeFilter = new PathFilter(null, getManifestsIncludePattern(), log);
        if (manifestIncludeFilter.getIncludes().length == 0) {
            throw new ScannerRuntimeException(String.format("Using manifest only mode requires include filter. Resolving config does not have include patterns defined: %s", getManifestsIncludePattern()));
        }

        List<String> filesToZip =
                Arrays.stream(CxSCAFileSystemUtils.scanAndGetIncludedFiles(sourceDir, manifestIncludeFilter))
                        .filter(scannedFileSet::contains).
                        collect(Collectors.toList());

        List<String> filesToFingerprint =
                Arrays.stream(CxSCAFileSystemUtils.scanAndGetIncludedFiles(sourceDir,
                        new PathFilter(null, getFingerprintsIncludePattern(), log)))
                        .filter(scannedFileSet::contains).
                        collect(Collectors.toList());


        CxSCAScanFingerprints fingerprints = fingerprintCollector.collectFingerprints(sourceDir, filesToFingerprint);

        File zipFile = zipDirectoryAndFingerprints(sourceDir, filesToZip, fingerprints);

        optionallyWriteFingerprintsToFile(fingerprints);

        return initiateScanForUpload(projectId, FileUtils.readFileToByteArray(zipFile), scaConfig.getZipFilePath());
    }


    private File zipDirectoryAndFingerprints(String sourceDir, List<String> paths, CxSCAScanFingerprints fingerprints) throws IOException {
        File result = config.getZipFile();
        if (result != null) {
            return result;
        }
        File tempFile = getZipFile();
        log.debug("Collecting files to zip archive: {}", tempFile.getAbsolutePath());
        long maxZipSizeBytes = CxZipUtils.MAX_ZIP_SIZE_BYTES;

        try (NewCxZipFile zipper = new NewCxZipFile(tempFile, maxZipSizeBytes, log)) {
            zipper.addMultipleFilesToArchive(new File(sourceDir), paths);
            if (zipper.getFileCount() == 0 && fingerprints.getFingerprints().isEmpty()) {
                throw handleFileDeletion(tempFile);
            }
            if (!fingerprints.getFingerprints().isEmpty()) {
                zipper.zipContentAsFile(FINGERPRINT_FILE_NAME, FingerprintCollector.getFingerprintsAsJsonString(fingerprints).getBytes());
            } else {
                log.debug("No supported fingerprints found to zip");
            }

            log.debug("The sources were zipped to {}", tempFile.getAbsolutePath());
            return tempFile;
        } catch (Zipper.MaxZipSizeReached e) {
            throw handleFileDeletion(tempFile, new IOException("Reached maximum upload size limit of " + FileUtils.byteCountToDisplaySize(maxZipSizeBytes)));
        } catch (IOException ioException) {
            throw handleFileDeletion(tempFile, ioException);
        }
    }

    private ScannerRuntimeException handleFileDeletion(File file, IOException ioException) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            return new ScannerRuntimeException(e);
        }

        return new ScannerRuntimeException(ioException);

    }

    private ScannerRuntimeException handleFileDeletion(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            return new ScannerRuntimeException(e.getMessage());
        }

        return new ScannerRuntimeException("No files found to zip and no supported fingerprints found");
    }

    private String getFingerprintsIncludePattern() {
        if (StringUtils.isNotEmpty(scaConfig.getFingerprintsIncludePattern())) {
            return scaConfig.getFingerprintsIncludePattern();
        }

        return resolvingConfiguration.getFingerprintsIncludePattern();
    }

    private String getManifestsIncludePattern() {
        if (StringUtils.isNotEmpty(scaConfig.getManifestsIncludePattern())) {
            return scaConfig.getManifestsIncludePattern();
        }

        return resolvingConfiguration.getManifestsIncludePattern();
    }

    private File getZipFile() throws IOException {
        if (StringUtils.isNotEmpty(scaConfig.getZipFilePath())) {
            return new File(scaConfig.getZipFilePath());
        }
        return File.createTempFile(CxZipUtils.TEMP_FILE_NAME_TO_ZIP, ".bin");
    }

    private void optionallyWriteFingerprintsToFile(CxSCAScanFingerprints fingerprints) {
        if (StringUtils.isNotEmpty(scaConfig.getFingerprintFilePath())) {
            try {
                fingerprintCollector.writeScanFingerprintsFile(fingerprints, scaConfig.getFingerprintFilePath());
            } catch (IOException ioException) {
                log.error(String.format("Failed writing fingerprint file to %s", scaConfig.getFingerprintFilePath()), ioException);
            }
        }
    }

    /**
     * Gets latest scan results using  for the current config.
     *
     * @return results of the latest successful scan for a project, if present; null - otherwise.
     */
    @Override
    public ResultsBase getLatestScanResults() {
        SCAResults result = new SCAResults();
        try {
            log.info("Getting latest scan results.");
            projectId = getRiskManagementProjectId(config.getProjectName());
            scanId = getLatestScanId(projectId);
            result = tryGetScanResults().orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setException(new ScannerRuntimeException("Error getting latest scan results.", e));
        }
        return result;
    }

    private Optional<SCAResults> tryGetScanResults() {
        SCAResults result = null;
        if (StringUtils.isNotEmpty(scanId)) {
            result = getScanResults();
        } else {
            log.info("Unable to get scan results");
        }
        return Optional.ofNullable(result);
    }

    private String getLatestScanId(String projectId) throws IOException {
        String result = null;
        if (StringUtils.isNotEmpty(projectId)) {
            log.debug("Getting latest scan ID for project ID: {}", projectId);
            String path = String.format(LATEST_SCAN, URLEncoder.encode(projectId, ENCODING));
            JsonNode response = httpClient.getRequest(path,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    ArrayNode.class,
                    HttpStatus.SC_OK,
                    "scan ID by project ID",
                    false);

            result = Optional.ofNullable(response)
                    // 'riskReportId' is in fact scanId, but the name is kept for backward compatibility.
                    .map(resp -> resp.at("/0/riskReportId").textValue())
                    .orElse(null);
        }
        String message = (result == null ? "Scan not found" : String.format("Scan ID: %s", result));
        log.info(message);
        return result;
    }


    private void printWebReportLink(SCAResults scaResult) {
        if (!StringUtils.isEmpty(scaResult.getWebReportLink())) {
            log.info("{} scan results location: {}", getScannerDisplayName(), scaResult.getWebReportLink());
        }
    }

    void testConnection() throws IOException {
        // The calls below allow to check both access control and API connectivity.
        login();
        getRiskManagementProjects();
    }

    public void login() throws IOException {
        log.info("Logging into {}", getScannerDisplayName());
        ScaConfig scaConfig = config.getScaConfig();

        String acUrl = scaConfig.getAccessControlUrl();
        LoginSettings settings = LoginSettings.builder()
                .accessControlBaseUrl(UrlUtils.parseURLToString(acUrl, AUTHENTICATION))
                .username(scaConfig.getUsername())
                .password(scaConfig.getPassword())
                .tenant(scaConfig.getTenant())
                .build();

        ClientTypeResolver resolver = new ClientTypeResolver(config);
        ClientType clientType = resolver.determineClientType(acUrl);
        settings.setClientTypeForPasswordAuth(clientType);

        httpClient.login(settings);
    }

    public void close() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * The following config properties are used:
     * astScaConfig
     * proxyConfig
     * cxOrigin
     * disableCertificateValidation
     */
    public void testScaConnection() {
        try {
            testConnection();
        } catch (IOException e) {
            throw new ScannerRuntimeException(e);
        }
    }

    private String resolveRiskManagementProject() throws IOException {
        String projectName = config.getProjectName();
        log.info("Getting project by name: '{}'", projectName);
        String resolvedProjectId = getRiskManagementProjectId(projectName);
        if (resolvedProjectId == null) {
            log.info("Project not found, creating a new one.");
            resolvedProjectId = createRiskManagementProject(projectName);
            log.info("Created a project with ID {}", resolvedProjectId);
        } else {
            log.info("Project already exists with ID {}", resolvedProjectId);
        }
        return resolvedProjectId;
    }

    private String getRiskManagementProjectId(String projectName) throws IOException {
        log.info("Getting project ID by name: '{}'", projectName);

        if (StringUtils.isEmpty(projectName)) {
            throw new ScannerRuntimeException("Non-empty project name must be provided.");
        }

        Project project = sendGetProjectRequest(projectName);

        String result = Optional.ofNullable(project)
                .map(Project::getId)
                .orElse(null);

        String message = (result == null ? "Project not found" : String.format("Project ID: %s", result));
        log.info(message);

        return result;
    }

    private Project sendGetProjectRequest(String projectName) throws IOException {
        Project result;
        try {
            String getProjectByName = String.format("%s?name=%s", PROJECTS, URLEncoder.encode(projectName, ENCODING));
            result = httpClient.getRequest(getProjectByName,
                    ContentType.CONTENT_TYPE_APPLICATION_JSON,
                    Project.class,
                    HttpStatus.SC_OK,
                    "CxSCA project ID by name",
                    false);
        } catch (CxHTTPClientException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                result = null;
            } else {
                throw e;
            }
        }
        return result;
    }

    private void getRiskManagementProjects() throws IOException {
        httpClient.getRequest(PROJECTS,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                Project.class,
                HttpStatus.SC_OK,
                "CxSCA projects",
                true);
    }

    private String createRiskManagementProject(String name) throws IOException {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(name);

        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        Project newProject = httpClient.postRequest(PROJECTS,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                entity,
                Project.class,
                HttpStatus.SC_CREATED,
                "create a project");

        return newProject.getId();
    }

    private SCAResults getScanResults() {
        SCAResults result;
        log.debug("Getting results for scan ID {}", scanId);
        try {
            result = new SCAResults();
            result.setScanId(this.scanId);

            ScaSummaryBaseFormat summaryBaseFormat = getSummaryReport(scanId);
 
            printSummary(summaryBaseFormat, this.scanId);

            ModelMapper mapper = new ModelMapper();
            Summary summary = mapper.map(summaryBaseFormat, Summary.class);

            Map<Filter.Severity, Integer> findingCountsPerSeverity = getFindingCountMap(summaryBaseFormat);
            summary.setFindingCounts(findingCountsPerSeverity);

            result.setSummary(summary);
            
            List<Finding> findings = getFindings(scanId);
            result.setFindings(findings);

            List<Package> packages = getPackages(scanId);
            result.setPackages(packages);

            String reportLink = getWebReportLink(config.getScaConfig().getWebAppUrl());
            result.setWebReportLink(reportLink);
            printWebReportLink(result);
            result.setScaResultReady(true);
            log.info("Retrieved SCA results successfully.");
        } catch (IOException e) {
            throw new ScannerRuntimeException("Error retrieving CxSCA scan results.", e);
        }
        return result;
    }

    protected Map<Filter.Severity, Integer> getFindingCountMap(ScaSummaryBaseFormat summary) {
        EnumMap<Filter.Severity, Integer> result = new EnumMap<>(Filter.Severity.class);
        result.put(Filter.Severity.HIGH, summary.getHighVulnerabilityCount());
        result.put(Filter.Severity.MEDIUM, summary.getMediumVulnerabilityCount());
        result.put(Filter.Severity.LOW, summary.getLowVulnerabilityCount());
        return result;
    }

    @Override
    protected String getWebReportPath() throws UnsupportedEncodingException {
        return String.format(WEB_REPORT,
                URLEncoder.encode(projectId, ENCODING),
                URLEncoder.encode(scanId, ENCODING));
    }

    private ScaSummaryBaseFormat getSummaryReport(String scanId) throws IOException {
        log.debug("Getting summary report.");

        String path = String.format(SUMMARY_REPORT, URLEncoder.encode(scanId, ENCODING));

        return httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                ScaSummaryBaseFormat.class,
                HttpStatus.SC_OK,
                "CxSCA report summary",
                false);
    }

    private List<Finding> getFindings(String scanId) throws IOException {
        log.debug("Getting findings.");

        String path = String.format(FINDINGS, URLEncoder.encode(scanId, ENCODING));

        ArrayNode responseJson = httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                ArrayNode.class,
                HttpStatus.SC_OK,
                "CxSCA findings",
                false);

        Finding[] findings = caseInsensitiveObjectMapper.treeToValue(responseJson, Finding[].class);

        return Arrays.asList(findings);
    }

    private List<Package> getPackages(String scanId) throws IOException {
        log.debug("Getting packages.");

        String path = String.format(PACKAGES, URLEncoder.encode(scanId, ENCODING));

        return (List<Package>) httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                Package.class,
                HttpStatus.SC_OK,
                "CxSCA findings",
                true);
    }

    private void printSummary(ScaSummaryBaseFormat summary, String scanId) {
        if (log.isInfoEnabled()) {
            log.info("----CxSCA risk report summary----");
            log.info("Created on: {}", summary.getCreatedOn());
            log.info("Direct packages: {}", summary.getDirectPackages());
            log.info("High vulnerabilities: {}", summary.getHighVulnerabilityCount());
            log.info("Medium vulnerabilities: {}", summary.getMediumVulnerabilityCount());
            log.info("Low vulnerabilities: {}", summary.getLowVulnerabilityCount());
            log.info("Scan ID: {}", scanId);
            log.info(String.format("Risk score: %.2f", summary.getRiskScore()));
            log.info("Total packages: {}", summary.getTotalPackages());
            log.info("Total outdated packages: {}", summary.getTotalOutdatedPackages());
        }
    }

    private void validate(ScaConfig config) {
        String error = null;
        if (config == null) {
            error = "%s config must be provided.";
        } else if (StringUtils.isEmpty(config.getApiUrl())) {
            error = "%s API URL must be provided.";
        } else if (StringUtils.isEmpty(config.getAccessControlUrl())) {
            error = "%s access control URL must be provided.";
        } else {
            RemoteRepositoryInfo repoInfo = config.getRemoteRepositoryInfo();
            if (repoInfo == null && config.getSourceLocationType() == SourceLocationType.REMOTE_REPOSITORY) {
                error = "%s remote repository info must be provided.";
            } else if (repoInfo != null && StringUtils.isNotEmpty(repoInfo.getBranch())) {
                error = "%s doesn't support specifying custom branches. It currently uses the default branch of a repo.";
            }
        }

        if (error != null) {
            throw new IllegalArgumentException(String.format(error, getScannerDisplayName()));
        }
    }

    public static void writeJsonToFile(String name, Object jsonObj, File workDirectory, Boolean cliOsaGenerateJsonReport, Logger log) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);

            if (cliOsaGenerateJsonReport) {
                //workDirectory = new File(workDirectory.getPath().replace(".json", "_" + name + ".json"));
                if (!workDirectory.isAbsolute()) {
                    workDirectory = new File(System.getProperty("user.dir") + CX_REPORT_LOCATION + File.separator + workDirectory);
                }
                if (!workDirectory.getParentFile().exists()) {
                    workDirectory.getParentFile().mkdirs();
                }
                name = name.endsWith(JSON_EXTENSION) ? name : name + JSON_EXTENSION;
                File jsonFile = new File(workDirectory + File.separator + name);
                FileUtils.writeStringToFile(jsonFile, json);
                log.info(name + " saved under location: " + jsonFile);
            } else {
                String now = new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date());
                String fileName = name + "_" + now + JSON_EXTENSION;
                File jsonFile = new File(workDirectory + CX_REPORT_LOCATION, fileName);
                FileUtils.writeStringToFile(jsonFile, json);
                log.info(name + " saved under location: " + workDirectory + CX_REPORT_LOCATION + File.separator + fileName);
            }
        } catch (Exception ex) {
            log.warn("Failed to write OSA JSON report (" + name + ") to file: " + ex.getMessage());
        }
    }
}
