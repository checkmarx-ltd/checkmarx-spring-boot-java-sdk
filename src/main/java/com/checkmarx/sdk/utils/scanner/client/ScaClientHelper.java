package com.checkmarx.sdk.utils.scanner.client;

import com.checkmarx.sdk.config.ContentType;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.*;
import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.sca.*;
import com.checkmarx.sdk.dto.sca.report.*;
import com.checkmarx.sdk.dto.sca.report.Package;
import com.checkmarx.sdk.dto.sca.xml.*;
import com.checkmarx.sdk.dto.scansummary.Severity;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.CxHTTPClientException;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.utils.CxRepoFileHelper;
import com.checkmarx.sdk.utils.ScanUtils;
import com.checkmarx.sdk.utils.State;
import com.checkmarx.sdk.utils.UrlUtils;
import com.checkmarx.sdk.utils.sca.CxSCAFileSystemUtils;
import com.checkmarx.sdk.utils.sca.fingerprints.CxSCAScanFingerprints;
import com.checkmarx.sdk.utils.sca.fingerprints.FingerprintCollector;
import com.checkmarx.sdk.utils.scaResolver.ScaResolverUtils;
import com.checkmarx.sdk.utils.scanner.client.httpClient.CxHttpClient;
import com.checkmarx.sdk.utils.scanner.client.httpClient.HttpClientHelper;
import com.checkmarx.sdk.utils.zip.CxZipUtils;
import com.checkmarx.sdk.utils.zip.NewCxZipFile;
import com.checkmarx.sdk.utils.zip.Zipper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static com.checkmarx.sdk.config.Constants.ENCODING;

/**
 * SCA - Software Composition Analysis - is the successor of OSA.
 */
public class ScaClientHelper extends ScanClientHelper implements IScanClientHelper {

    private static final String CREDENTIALS_TYPE = "password";
    private static final String RISK_MANAGEMENT_API = "/risk-management/";
    private static final String POLICY_MANAGEMENT_API = "/policy-management/";
    private static final String POLICIES_API = POLICY_MANAGEMENT_API + "policies";
    private static final String POLICIES_API_BY_ID = POLICIES_API + "/%s";
    private static final String RISK_REPORT_URL = RISK_MANAGEMENT_API + "risk-reports?projectId=%s&size=1";
    private static final String POLICY_EVALUATION_URL = POLICY_MANAGEMENT_API + "policy-evaluation?reportId=%s";
    private static final String PROJECTS = RISK_MANAGEMENT_API + "projects";
    private static final String PROJECTS_BY_ID = PROJECTS + "/%s";
    private static final String SUMMARY_REPORT = RISK_MANAGEMENT_API + "riskReports/%s/summary";
    private static final String FINDINGS = RISK_MANAGEMENT_API + "riskReports/%s/vulnerabilities";
    private static final String PACKAGES = RISK_MANAGEMENT_API + "riskReports/%s/packages";
    private static final String LATEST_SCAN = RISK_MANAGEMENT_API + "riskReports?size=1&projectId=%s";
    private static final String WEB_REPORT = "/#/projects/%s/reports/%s";
    private static final String RESOLVING_CONFIGURATION_API = "/settings/projects/%s/resolving-configuration";
    private static final String REPORT_IN_XML_WITH_SCANID = RISK_MANAGEMENT_API + "risk-reports/%s/export?format=xml";

    private static final String REPORT_SCA_PACKAGES = "cxSCAPackages";
    private static final String REPORT_SCA_FINDINGS = "cxSCAVulnerabilities";
    private static final String REPORT_SCA_SUMMARY = "cxSCASummary";
    private static final String JSON_EXTENSION = ".json";
    public static final String AUTHENTICATION = "identity/connect/token";
    private static final String ENGINE_TYPE_FOR_API = "sca";

    private static final String TENANT_HEADER_NAME = "Account-Name";


    public static final String CX_REPORT_LOCATION = File.separator + "Checkmarx" + File.separator + "Reports";
    public static final String TEMP_FILE_NAME_TO_SCA_RESOLVER_RESULTS_ZIP = "ScaResolverResults";
    public static final String ERROR_WITH_XML_REPORT = "Error with XML report";
    public static final String ERROR_PROCESSING_SCAN_RESULTS = "Error while processing scan results";

    private static final ObjectMapper caseInsensitiveObjectMapper = new ObjectMapper()
            // Ignore any fields that can be added to SCA API in the future.
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // We need this feature to properly deserialize finding severity,
            // e.g. "High" (in JSON) -> Severity.HIGH (in Java).
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    private final ScaConfig scaConfig;
    private final ScaProperties scaProperties;


    private String projectId;
    private String scanId;
    private final FingerprintCollector fingerprintCollector;
    private CxSCAResolvingConfiguration resolvingConfiguration;
    private static final String FINGERPRINT_FILE_NAME = ".cxsca.sig";
    public static final String SCA_RESOLVER_RESULT_FILE_NAME = ".cxsca-results.json";

    public static final String SAST_RESOLVER_RESULT_FILE_NAME =".cxsca-sast-results.json";

    public ScaClientHelper(RestClientConfig config, Logger log, ScaProperties scaProperties) {
        super(config, log);

        this.scaConfig = config.getScaConfig();
        this.scaProperties = scaProperties;
        validate(scaConfig);

        httpClient = createHttpClient(scaConfig.getApiUrl());
        this.resolvingConfiguration = null;
        fingerprintCollector = new FingerprintCollector(log);
        // Pass tenant name in a custom header. This will allow to get token from on-premise access control server
        // and then use this token for SCA authentication in cloud.
        httpClient.setCustomHeader(TENANT_HEADER_NAME, config.getScaConfig().getTenant());
        httpClient.setCustomHeader(CxHttpClient.ORIGIN_HEADER, ScanClientHelper.CX_FLOW_SCAN_ORIGIN_NAME);
    }

    @Override
    protected String getScannerDisplayName() {
        return ScannerType.AST_SCA.getDisplayName();
    }

    @Override
    protected ScanConfig getScanConfig() {
    	
    	CxSCAScanAPIConfig apiConfig = new CxSCAScanAPIConfig();
    	apiConfig.setIncludeSourceCode(Boolean.toString(scaConfig.isIncludeSources()));
    
        ScanConfig scanConfig =  ScanConfig.builder()
                .type(ENGINE_TYPE_FOR_API)
                .value(apiConfig)
                .build();
        return scanConfig;
    }

    @Override
    protected HandlerRef getBranchToScan(RemoteRepositoryInfo repoInfo) {
        if (StringUtils.isNotEmpty(repoInfo.getBranch())) {
            
           HandlerRef ref = new HandlerRef();
            ref.setType("branch");
            ref.setValue(repoInfo.getBranch());
            return ref;

        }
        return null;
    }

    /**
     * Transforms the repo URL if credentials are specified in repoInfo.
     */
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
            log.debug("location type {}",locationType);
            HttpResponse response;

            projectId = resolveRiskManagementProject();
            boolean isManifestAndFingerprintsOnly = !config.getScaConfig().isIncludeSources();
            if (isManifestAndFingerprintsOnly) {
                this.resolvingConfiguration = getCxSCAResolvingConfigurationForProject(this.projectId);
                log.info("Got the following manifest patterns {}", this.resolvingConfiguration.getManifests());
                log.info("Got the following fingerprint patterns {}", this.resolvingConfiguration.getFingerprints());
            }

            if (locationType == SourceLocationType.REMOTE_REPOSITORY) {
                response = submitSourcesFromRemoteRepo(projectId, scaConfig);
            } else {
                if (scaConfig.isIncludeSources()) {
                    response = submitAllSourcesFromLocalDir(projectId, scaConfig);
                }else if(scaProperties.isEnableScaResolver())
                {
                    response = submitScaResolverEvidenceFile(scaConfig);
                }else {
                    response = submitManifestsAndFingerprintsFromLocalDir(projectId, scaConfig);
                }
            }
            this.scanId = extractScanIdFrom(response);
            scaResults.setScanId(scanId);
        } catch (Exception e) {
            log.error(e.getMessage());
            setState(State.FAILED);
            scaResults.setException(new ScannerRuntimeException("Error creating scan.", e));
        } finally {
            if (config.isClonedRepo() && config.getZipFile() != null) {
                log.info("Deleting cloned repo zip file: {}", config.getZipFile());
                FileUtils.deleteQuietly(config.getZipFile());
            }
        }
        return scaResults;
    }
    private HttpResponse submitScaResolverEvidenceFile(ScaConfig scaConfig) throws IOException
    {
        //varibles required
        CxRepoFileHelper cxRepoFileHelper = new CxRepoFileHelper();
        File file = new File(config.getSourceDir());
        String sourceDir = file.getAbsolutePath();
        String projectName = config.getProjectName();
        String resultPath = cxRepoFileHelper.getGitClonePath();
        String additionalParameters = manageParameters(scaProperties.getScaResolverAddParameters());
        String sastResultPath ="";
        ArrayList<File> resultToZip = new ArrayList<>();

        //file creation
        resultPath=resultPath+File.separator+ uniqueFolderName() + File.separator + SCA_RESOLVER_RESULT_FILE_NAME;

        String mandatoryFields = "-s "+sourceDir +" "+"-n "+projectName+" "+"-r "+resultPath;
        log.debug("mandatory {}",mandatoryFields);
        log.info("Executing SCA Resolver flow.");
        log.info("Path to Sca Resolver: {}", scaProperties.getPathToScaResolver());
        //log.info("Sca Resolver Additional Parameters: {}", scaProperties.getScaResolverAddParameters());
        File zipFile =null;
        int exitCode = ScaResolverUtils.runScaResolver(scaProperties.getPathToScaResolver(),mandatoryFields,additionalParameters,resultPath,log,scaConfig);
        if (exitCode == 0) {
            log.info("***************SCA resolution completed successfully.******************");
            File resultFilePath = new File(resultPath);
            resultToZip.add(resultFilePath);

            //check if sast-result-path is present, if exists add to zip.
            if(scaProperties.getScaResolverAddParameters().contains("--sast-result-path"))
            {
                sastResultPath = getSastResultFilePathFromAdditionalParams(additionalParameters);
                File sastResultFile = new File(sastResultPath);
                resultToZip.add(sastResultFile);
            }

                zipFile = zipEvidenceFile(resultToZip);

        }else{
            throw new CxHTTPClientException("Error while running sca resolver executable. Exit code: "+exitCode);
        }
        return initiateScanForUpload(projectId, FileUtils.readFileToByteArray(zipFile), config.getScaConfig());
    }

    private String manageParameters(String additionalParameters)
    {
        String newAdditionalParameters="";
        if(additionalParameters.contains("--sast-result-path"))
        {
            String sastResultPath =getSastResultFilePathFromAdditionalParams(additionalParameters);
            File sastResultFile = new File(sastResultPath);
            if(sastResultFile.isDirectory())
            {
                sastResultPath = sastResultPath + File.separator + uniqueFolderName()+ File.separator + SAST_RESOLVER_RESULT_FILE_NAME;
            }
            else {
                String parentName = sastResultFile.getParent();
                sastResultPath = parentName + File.separator + uniqueFolderName()+ File.separator + SAST_RESOLVER_RESULT_FILE_NAME;
            }
            newAdditionalParameters = setSastResultFilePathFromAdditionalParams(additionalParameters,sastResultPath);
            return newAdditionalParameters;
        }
        return additionalParameters;
    }

    private String uniqueFolderName()
    {
        Date date = new Date();
        Timestamp ts=new Timestamp(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String prefixFolderNameSCA=formatter.format(ts)+gen();
        return prefixFolderNameSCA;
    }
    private int gen() {
        Random r = new Random( System.currentTimeMillis() );
        return 10000 + r.nextInt(20000);
    }
    private  String getSastResultFilePathFromAdditionalParams(String scaResolverAddParams)
    {
        String pathToEvidenceDir ="";
        List<String> arguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(scaResolverAddParams);
        while (m.find())
            arguments.add(m.group(1));

        for (int i = 0; i <  arguments.size() ; i++) {
            if (arguments.get(i).equals("--sast-result-path") )
                pathToEvidenceDir =  arguments.get(i+1);
        }
        return pathToEvidenceDir ;
    }

    private  String setSastResultFilePathFromAdditionalParams(String scaResolverAddParams,String valueToSet)
    {
        StringBuilder newAdditionalParams = new StringBuilder();
        List<String> arguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(scaResolverAddParams);
        while (m.find())
            arguments.add(m.group(1));

        for (int i = 0; i <  arguments.size() ; i++) {
            if (arguments.get(i).equals("--sast-result-path") )
            {
                if(arguments.size()-1==i)
                {
                    arguments.add(valueToSet);
                }
                else {
                    arguments.set(i+1,valueToSet);
                }

            }
            if(arguments.size()-1==i)
            {
                newAdditionalParams.append(arguments.get(i));
            }
            else {
                newAdditionalParams.append(arguments.get(i)).append(" ");
            }

        }
        return newAdditionalParams.toString() ;
    }
    private File zipEvidenceFile(ArrayList<File> filePath) throws IOException {
        File tempUploadFile = File.createTempFile(TEMP_FILE_NAME_TO_SCA_RESOLVER_RESULTS_ZIP, ".zip");
        log.info("Collecting files to zip archive: {}", tempUploadFile.getAbsolutePath());
        long maxZipSizeBytes = CxZipUtils.MAX_ZIP_SIZE_BYTES;

            try (NewCxZipFile zipper = new NewCxZipFile(tempUploadFile, maxZipSizeBytes, log)) {
                for(File file : filePath)
                {
                    String sourceDir = file.getParent();
                    List<String> paths = new ArrayList <>();
                    paths.add(file.getName());
                    log.debug("Source dir:{}",sourceDir);
                    log.debug("paths:{}",paths);
                    zipper.addMultipleFilesToArchive(new File(sourceDir), paths);
                }
                log.info("Added {} files to zip.",  zipper.getFileCount());
                log.info("The sources were zipped to {}", tempUploadFile.getAbsolutePath());
                return tempUploadFile;
            } catch (Zipper.MaxZipSizeReached e) {
                throw handleFileDeletion(filePath.get(0), new IOException("Reached maximum upload size limit of " + FileUtils.byteCountToDisplaySize(maxZipSizeBytes)));
            } catch (IOException ioException) {
                throw handleFileDeletion(filePath.get(0), ioException);
            }
    }

    public void deleteProjectById(String projectId) throws IOException {
        log.info("Deleting project with id: {}", projectId);
        httpClient.deleteRequest(String.format(PROJECTS_BY_ID, projectId), HttpStatus.SC_NO_CONTENT, "delete a project");
    }

    public Project getProjectDetailsByProjectId(String projectId) throws IOException {
        log.info("Getting project details by project id: {}", projectId);
        return httpClient.getRequest(String.format(PROJECTS_BY_ID, projectId),
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                Project.class,
                HttpStatus.SC_OK,
                "project details",
                false);
    }

    protected HttpResponse submitAllSourcesFromLocalDir(String projectId, ScanConfigBase scaConfig) throws IOException {
        log.info("Using local directory flow.");


        String sourceDir = config.getSourceDir();
        byte[] zipFile = null;
        if (config.isClonedRepo()){
            CxRepoFileHelper cxRepoFileHelper = new CxRepoFileHelper();
            File clonedLocalDir = new File(sourceDir);
            String zipFilePath = cxRepoFileHelper.zipClonedRepo(clonedLocalDir, config.getScaConfig().getExcludeFiles());
            cxRepoFileHelper.deleteCloneLocalDir(clonedLocalDir);
            config.setZipFile(new File(zipFilePath));
            zipFile = FileUtils.readFileToByteArray(new File(zipFilePath));
        } else {
            // CLI Mode
            // The Exclude files parameter is used as a regular expression but
            // for this method it is used as include,exclude pattern which requires exclude files
            // to begin with an ! to be then used by the directoryScanner used in this utility.
            // So the below method converts the list to comma separated string and all elements starts with !.
            String pattern = "";
            if(this.scaConfig.getExcludeFiles() != null) {
	            for (String nextpattern: this.scaConfig.getExcludeFiles()) {
	                pattern += "!" + nextpattern + ",";
	            }
	            // removing the last comma from the string
	            pattern = pattern.substring(0,pattern.length()-1);
            }

            PathFilter filter = new PathFilter("", pattern, log);
            zipFile = CxZipUtils.getZippedSources(config, filter, sourceDir, log);
        }

        return initiateScanForUpload(projectId, zipFile, scaConfig);
    }

    private HttpResponse submitManifestsAndFingerprintsFromLocalDir(String projectId, ScanConfigBase configBase) throws IOException {
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

        if (config.isClonedRepo()){
            CxRepoFileHelper cxRepoFileHelper = new CxRepoFileHelper();
            cxRepoFileHelper.deleteCloneLocalDir(new File(sourceDir));
            config.setZipFile(zipFile);
        }
        return initiateScanForUpload(projectId, FileUtils.readFileToByteArray(zipFile), configBase);
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

    @Override
    public ScanResults getReportContent(File file, FilterConfiguration filter) throws CheckmarxException {
        SCAResults scaResult=new SCAResults();
        ScanResults result = null;
        if (file == null) {
            throw new CheckmarxException("File not provided for processing of results");
        }
        try {

            /* protect against XXE */
            JAXBContext jc = JAXBContext.newInstance(SCARiskReportType.class);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            List<ScanResults.XIssue> issueList = new ArrayList<>();
            JAXBElement<SCARiskReportType> event = (JAXBElement<SCARiskReportType>)unmarshaller.unmarshal(file);
            SCARiskReportType scaResults = event.getValue();
            ScanResults.ScanResultsBuilder scaScanBuilder = ScanResults.builder();

            RiskReportSummaryType iskReportSummaryType=scaResults.getRiskReportSummary();

            PackagesType packagesType=scaResults.getPackages();

            VulnerabilitiesType vulnerabilitiesType=scaResults.getVulnerabilities();

            LicensesType licensesType=scaResults.getLicenses();

            PoliciesType policiesType=scaResults.getPolicies();

            this.scanId =iskReportSummaryType.getRiskReportId();
            this.projectId=iskReportSummaryType.getProjectId();

            scaResult =getLatestScaResults(iskReportSummaryType,packagesType,vulnerabilitiesType,licensesType,policiesType) ;

            scaResult.setScanId(scanId);

            AstScaResults internalResults = new AstScaResults(new SCAResults(), new ASTResults());

            result = toScanResults(scaResult);



            return result;

        } catch (JAXBException e) {
            log.error(ERROR_WITH_XML_REPORT);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException(ERROR_PROCESSING_SCAN_RESULTS);
        } catch (NullPointerException e) {
            log.info("Null error");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException(ERROR_PROCESSING_SCAN_RESULTS);
        }
    }

    private ScanResults toScanResults(SCAResults scaResult) {
        return ScanResults.builder()
                .scaResults(scaResult)
                .build();
    }

    private SCAResults getLatestScaResults(RiskReportSummaryType iskReportSummaryType, PackagesType packagesType, VulnerabilitiesType vulnerabilitiesType, LicensesType licensesType, PoliciesType policiesType) {
        SCAResults result = new SCAResults();
        try {
            log.info("Getting latest scan results.");
            result = tryGetScaResults(iskReportSummaryType,packagesType,vulnerabilitiesType,licensesType,policiesType).orElse(null);
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setException(new ScannerRuntimeException("Error getting latest scan results.", e));
        }
        return result;
    }

    private Optional<SCAResults> tryGetScaResults(RiskReportSummaryType iskReportSummaryType, PackagesType packagesType, VulnerabilitiesType vulnerabilitiesType, LicensesType licensesType, PoliciesType policiesType) {
        SCAResults result = null;
        if (result==null) {
            result = getScaResults(iskReportSummaryType,packagesType,vulnerabilitiesType,licensesType,policiesType);
        } else {
            log.info("Unable to get scan results");
        }
        return Optional.ofNullable(result);
    }

    private SCAResults getScaResults(RiskReportSummaryType riskReportSummaryType, PackagesType packagesType, VulnerabilitiesType vulnerabilitiesType, LicensesType licensesType, PoliciesType policiesType) {
        SCAResults result;
        ScaSummaryBaseFormat summaryBaseFormat=new ScaSummaryBaseFormat();
        List<Package> packages = null;
        log.debug("Getting results for scan ID {}", scanId);
        try {
            result = new SCAResults();
            result.setScanId(this.scanId);

            summaryBaseFormat = getScaSummaryReport(riskReportSummaryType,summaryBaseFormat);

            printSummary(summaryBaseFormat, this.scanId);

            ModelMapper mapper = new ModelMapper();
            Summary summary = mapper.map(summaryBaseFormat, Summary.class);
            Map<Filter.Severity, Integer> findingCountsPerSeverity = getFindingCountMap(summaryBaseFormat);
	        summary.setFindingCounts(findingCountsPerSeverity);
            result.setSummary(summary);

            List<Finding> findings = getScaFindings(vulnerabilitiesType);          
            result.setFindings(findings);

            packages = getScaPackages(packagesType,packages);
            
            result.setPackages(packages);

            String reportLink = getWebReportLink(config.getScaConfig().getWebAppUrl());
            result.setWebReportLink(reportLink);
            printWebReportLink(result);
            result.setScaResultReady(true);

            List<PolicyEvaluation> policyEvaluationsByReport = getScaPolicyEvaluationByReport(policiesType);
            List<String> scanViolatedPolicies = getScanViolatedPolicies(policyEvaluationsByReport);
            
	        result.setPolicyViolated(!scanViolatedPolicies.isEmpty());
            result.setViolatedPolicies(scanViolatedPolicies);

            log.info("Retrieved SCA results successfully.");
        } catch (Exception e) {
            throw new ScannerRuntimeException("Error retrieving CxSCA scan results.", e);
        }
        return result;
    }

    private List<PolicyEvaluation> getScaPolicyEvaluationByReport(PoliciesType policiesType) {

        PolicyEvaluation policyEvaluation=new PolicyEvaluation();
        PolicyAction policyAction=new PolicyAction();

        List<PolicyEvaluation> policyEvaluationList=new ArrayList<>();

        for(int count=0;count<policiesType.getPolicy().size();count++)
        {
            policyEvaluation.setName(policiesType.getPolicy().get(count).getPolicyName());
            policyEvaluation.setViolated(Boolean.parseBoolean(policiesType.getPolicy().get(count).getIsViolating()));
            policyAction.setBreakBuild(Boolean.parseBoolean(policiesType.getPolicy().get(count).getBreakBuild()));
            policyEvaluation.setActions(policyAction);
            policyEvaluationList.add(policyEvaluation);
            policyEvaluation=new PolicyEvaluation();
        }

        return policyEvaluationList;
    }

    private List<Package> getScaPackages(PackagesType packagesType, List<Package> packages) {

        List<PackageType> packageTypeList=packagesType.getPackage();
        Package packge=new Package();
        packages=new ArrayList<>();

        List<String> licenses =null;
        for (int count=0;count<packageTypeList.size();count++)
        {
            packge.setId(packageTypeList.get(count).getId());
            packge.setName(packageTypeList.get(count).getName());
            packge.setVersion(packageTypeList.get(count).getVersion());
            packge.setMatchType(packageTypeList.get(count).getMatchType());
            packge.setHighVulnerabilityCount(packageTypeList.get(count).getHighVulnerabilityCount());
            packge.setLowVulnerabilityCount(packageTypeList.get(count).getLowVulnerabilityCount());
            packge.setMediumVulnerabilityCount(packageTypeList.get(count).getMediumVulnerabilityCount());
            packge.setNumberOfVersionsSinceLastUpdate(packageTypeList.get(count).getNumberOfVersionsSinceLastUpdate());
            packge.setNewestVersion(packageTypeList.get(count).getNewestVersion());
            packge.setOutdated(Boolean.parseBoolean(packageTypeList.get(count).getOutdated()));
            packge.setReleaseDate(packageTypeList.get(count).getReleaseDate().toString());
            packge.setRiskScore(packageTypeList.get(count).getRiskScore());
            PackageSeverity severity=scaToScanPackageSeverity(packageTypeList.get(count).getSeverity());
            packge.setSeverity(severity);
            packge.setLocations(packageTypeList.get(count).getLocations().getLocation());
            packge.setPackageRepository(packageTypeList.get(count).getPackageRepository());
            packge.setIsDirectDependency(Boolean.parseBoolean(packageTypeList.get(count).getIsDirectDependency()));
            packge.setIsDevelopment(Boolean.parseBoolean(packageTypeList.get(count).getIsDevelopmentDependency()));

            LicensesType licensesType=packageTypeList.get(count).getLicenses();

            licenses = new ArrayList<>();

            for(int licensesTypeCount=0;licensesTypeCount<licensesType.getLicense().size();licensesTypeCount++)
            {
                licenses.add(licensesType.getLicense().get(licensesTypeCount).getContent().toString());

                packge.setLicenses(licenses);
            }
            packages.add(packge);
            packge= new Package();
        }
        return packages;

    }

    private PackageSeverity scaToScanPackageSeverity(String severity) {

        PackageSeverity scaPackageSeverity = null;

        switch (severity)
        {
            case "High":
                scaPackageSeverity = PackageSeverity.HIGH;
                break;

            case "Medium":
                scaPackageSeverity = PackageSeverity.MEDIUM;
                break;

            case "Low":
                scaPackageSeverity = PackageSeverity.LOW;
                break;
            case "None":
                scaPackageSeverity = PackageSeverity.NONE;
                break;
            default:
                break;
        }
        return scaPackageSeverity;
    }



    private List<Finding> getScaFindings(VulnerabilitiesType vulnerabilitiesType) {
        Finding finding=new Finding();
        List<Finding> findingList=new ArrayList<>();
        List<String> references = new ArrayList<>();
        List<String> reference = new ArrayList<>();
        for(int count=0;count<vulnerabilitiesType.getVulnerability().size();count++)
        {
            finding.setId(vulnerabilitiesType.getVulnerability().get(count).getId());
            finding.setCveName(vulnerabilitiesType.getVulnerability().get(count).getCveName());
            finding.setScore(vulnerabilitiesType.getVulnerability().get(count).getScore());
            references=vulnerabilitiesType.getVulnerability().get(count).getReferences().getReference();
            reference = new ArrayList<>();
            for(int referenceCount=0;referenceCount<references.size();referenceCount++)
            {
                reference.add(references.get(referenceCount));
                finding.setReferences(reference);
            }
            Severity severity=scaToScanResultSeverity(vulnerabilitiesType.getVulnerability().get(count).getSeverity());
            finding.setSeverity(severity);
            finding.setPublishDate(vulnerabilitiesType.getVulnerability().get(count).getPublishDate().toString());
            finding.setCveName(vulnerabilitiesType.getVulnerability().get(count).getCveName());
            finding.setDescription(vulnerabilitiesType.getVulnerability().get(count).getDescription());
            finding.setRecommendations(vulnerabilitiesType.getVulnerability().get(count).getRecommendations());
            finding.setPackageId(vulnerabilitiesType.getVulnerability().get(count).getPackageId());
            finding.setIgnored(Boolean.parseBoolean(vulnerabilitiesType.getVulnerability().get(count).getIsIgnored()));
            finding.setViolatingPolicy(Boolean.parseBoolean(vulnerabilitiesType.getVulnerability().get(count).getIsViolatingPolicy()));
            finding.setFixResolutionText(String.valueOf(vulnerabilitiesType.getVulnerability().get(count).getFixResolutionText()));
            findingList.add(finding);
            finding=new Finding();
        }

        return findingList;
    }

    private Severity scaToScanResultSeverity(String severity) {
        Severity scaSeverity = null;
        switch (severity) {
            case "High":
                scaSeverity = Severity.HIGH;
                break;

            case "Medium":
                scaSeverity = Severity.MEDIUM;
                break;

            case "Low":
                scaSeverity = Severity.LOW;
                break;
            default:
                break;

        }

        return scaSeverity;
    }

    private ScaSummaryBaseFormat getScaSummaryReport(RiskReportSummaryType riskReportSummaryType, ScaSummaryBaseFormat scaSummaryBaseFormat) {
        scaSummaryBaseFormat.setDirectPackages(riskReportSummaryType.getDirectPackages());
        scaSummaryBaseFormat.setRiskScore(riskReportSummaryType.getRiskScore());
        scaSummaryBaseFormat.setTotalPackages(riskReportSummaryType.getTotalPackages());
        scaSummaryBaseFormat.setTotalOutdatedPackages(riskReportSummaryType.getTotalOutdatedPackages());
        scaSummaryBaseFormat.setCreatedOn(riskReportSummaryType.getCreatedOn().toString());
        scaSummaryBaseFormat.setHighVulnerabilityCount(riskReportSummaryType.getHighVulnerabilityCount());
        scaSummaryBaseFormat.setLowVulnerabilityCount(riskReportSummaryType.getLowVulnerabilityCount());
        scaSummaryBaseFormat.setMediumVulnerabilityCount(riskReportSummaryType.getMediumVulnerabilityCount());

        return scaSummaryBaseFormat;
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

    public String createRiskManagementProject(String name) throws IOException {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(name);

        determineProjectTeam(request);
        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        Project newProject = httpClient.postRequest(PROJECTS,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                entity,
                Project.class,
                HttpStatus.SC_CREATED,
                "create a project");

        return newProject.getId();
    }

    private void determineProjectTeam(CreateProjectRequest request) {
        String team = StringUtils.firstNonEmpty(config.getScaConfig().getTeam(), scaProperties.getTeam());
        if (StringUtils.isNotEmpty(team)) {
            setTeam(request, team);
        } else {
            log.info("SCA project team was not defined. Assigning with default 'All Users' value");
            request.setAssignedTeams(null);
        }
    }

    /*
        SCA team setter is relevant for project creation stage only.
        After a project is getting created, team cannot be modified or changed.
     */
    private void setTeam(CreateProjectRequest request, String team) {
        log.info("Assigning SCA project with team: {}", team);
        request.setAssignedTeams(Collections.singletonList(team));
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

            String riskReportId = getRiskReportByProjectId(this.projectId);
            List<PolicyEvaluation> policyEvaluationsByReportId = getPolicyEvaluationByReportId(riskReportId);
            List<String> scanViolatedPolicies = getScanViolatedPolicies(policyEvaluationsByReportId);
            result.setPolicyViolated(!scanViolatedPolicies.isEmpty());
            result.setViolatedPolicies(scanViolatedPolicies);

            if(scaProperties.isPreserveXml()){
                String path = String.format(REPORT_IN_XML_WITH_SCANID, URLEncoder.encode(scanId, ENCODING));
                String xml = httpClient.getRequest(path,
                        ContentType.CONTENT_TYPE_APPLICATION_JSON,
                        String.class,
                        HttpStatus.SC_OK,
                        "CxSCA findings",
                        false);
                xml = xml.trim().replaceFirst("^([\\W]+)<", "<");
                String xml2 = ScanUtils.cleanStringUTF8_2(xml);
                result.setOutput(xml2);
            }

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

    private String getRiskReportByProjectId(String projectId) throws IOException {
        log.debug("Getting risk report by project-id: {}", projectId);

        String path = String.format(RISK_REPORT_URL, projectId);
        JsonNode response = httpClient.getRequest(path,
                null,
                ArrayNode.class,
                HttpStatus.SC_OK,
                "getting risk report by project-id",
                false);

        return Optional.ofNullable(response)
                .map(report -> report.at("/0/riskReportId").textValue())
                .orElse(null);
    }

    private List<PolicyEvaluation> getPolicyEvaluationByReportId(String reportId) throws IOException {
        log.debug("Getting policy evaluation by report-id: {}", reportId);

        String path = String.format(POLICY_EVALUATION_URL, reportId);
        ArrayNode responseJson = httpClient.getRequest(path,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                ArrayNode.class,
                HttpStatus.SC_OK,
                "getting policy evaluation be report-id",
                false);

        PolicyEvaluation[] policyEvaluations = caseInsensitiveObjectMapper.treeToValue(responseJson, PolicyEvaluation[].class);

        return Arrays.asList(policyEvaluations);
    }

    public String createNewPolicy(Policy policyToCreate) throws IOException {
        log.debug("Creating new policy with name: {} to project-ids: {}", policyToCreate.getName(), policyToCreate.getProjectIds());
        StringEntity policyEntity = HttpClientHelper.convertToStringEntity(policyToCreate);

        String policyId =  httpClient.postRequest(POLICIES_API,
                ContentType.CONTENT_TYPE_APPLICATION_JSON,
                policyEntity,
                String.class,
                HttpStatus.SC_OK,
                "creating a policy");

        /*
            SCA returns the policy-id as a string while declaring the response type as application/json instead of text
            this causes the return type to be like this "policy-id" and when assigning this value
            to a local String it gets like: ""policy-id"" - so here we remove the double quotes manually until they will fix it
         */
        return policyId.replace("\"", "");
    }

    public void deletePolicy(String policyId) throws IOException {
        log.info("Deleting policy with id: {}", policyId);
        String path = String.format(POLICIES_API_BY_ID, policyId);
        httpClient.deleteRequest(path, HttpStatus.SC_OK, "delete a policy");
    }

    private List<String> getScanViolatedPolicies(List<PolicyEvaluation> policyEvaluationList) {
        List<String> violatedPolicies = new ArrayList<>();

        policyEvaluationList.forEach(policy -> {
            if (policy.isViolated() && policy.getActions().isBreakBuild()) {
                violatedPolicies.add(policy.getName());
            }
        });

        return violatedPolicies;
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

    protected HttpResponse sendStartScanRequest(RemoteRepositoryInfo repoInfo,
                                                SourceLocationType sourceLocation,
                                                String projectId) throws IOException {
        log.debug("Constructing the 'start scan' request");

        ScaScanStartHandler handler = getScanStartHandler(repoInfo);

        ScaProjectToScan project = ScaProjectToScan.builder()
                .id(projectId)
                .type(sourceLocation.getApiValue())
                .handler(handler)
                .build();

        List<ScanConfig> apiScanConfig = Collections.singletonList(getScanConfig());

        ScaStartScanRequest request = ScaStartScanRequest.builder()
                .project(project)
                .config(apiScanConfig)
                .build();

        StringEntity entity = HttpClientHelper.convertToStringEntity(request);

        log.info("Sending the 'start scan' request.");
        return httpClient.postRequest(CREATE_SCAN, ContentType.CONTENT_TYPE_APPLICATION_JSON, entity,
                HttpResponse.class, HttpStatus.SC_CREATED, "start the scan");
    }
    

    /**
     * @param repoInfo may represent an actual git repo or a presigned URL of an uploaded archive.
     */
    protected ScaScanStartHandler getScanStartHandler(RemoteRepositoryInfo repoInfo) {
        log.debug("Creating the handler object.");

        HandlerRef ref = getBranchToScan(repoInfo);
        
        String password = StringUtils.defaultString(repoInfo.getPassword());
        String username = StringUtils.defaultString(repoInfo.getUsername());

        GitCredentials credentials = GitCredentials.builder()
                .type(CREDENTIALS_TYPE)
                .value(password)
                .build();

        URL effectiveRepoUrl = getEffectiveRepoUrl(repoInfo);

        // The ref/username/credentials properties are mandatory even if not specified in repoInfo.
        return ScaScanStartHandler.builder()
                .ref(ref)
                .username(username)
                .credentials(credentials)
                .url(effectiveRepoUrl.toString())
                .build();
    }
}
