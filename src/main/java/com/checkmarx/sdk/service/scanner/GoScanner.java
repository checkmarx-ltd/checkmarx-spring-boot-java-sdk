package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.CxGoProperties;
import com.checkmarx.sdk.config.CxPropertiesBase;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.sca.Summary;
import com.checkmarx.sdk.dto.cx.CxProject;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.cx.CxScanSettings;
import com.checkmarx.sdk.dto.cx.CxScanSummary;
import com.checkmarx.sdk.dto.cxgo.*;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.CheckmarxRuntimeException;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.dto.sca.report.Package;
import com.checkmarx.sdk.dto.scansummary.Severity;
import com.checkmarx.sdk.service.CxGoAuthService;
import com.checkmarx.sdk.utils.CxRepoFileHelper;
import com.checkmarx.sdk.service.FilterInputFactory;
import com.checkmarx.sdk.service.FilterValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Class used to orchestrate submitting scans and retrieving results
 */
@Service
@Slf4j
public class GoScanner implements ILegacyClient {
    private static final String UNKNOWN = "-1";
    private static final Integer UNKNOWN_INT = -1;

    //
    /// Rest API endpoints
    //
    private static final String CREATE_SCAN = "/v1/scans";
    private static final String SCAN_STATUS = "/v1/scans/{scan_id}/status";
    private static final String SCAN = "/v1/scans/{scan_id}";
    private static final String SCANS = "/v1/scans";
    private static final String SCAN_RESULTS_ENCODED = "/results/results?criteria=%7B%22criteria%22%3A%5B%7B%22key%22%3A%22projectId%22%2C%22value%22%3A%22{project_id}%22%7D%2C%7B%22key%22%3A%22scanId%22%2C%22value%22%3A%22{scan_id}%22%7D%5D%2C%22pagination%22%3A%7B%22currentPage%22%3A{current_page%2C%22pageSize%22%3A{page_size}%7D%7D";
    private static final String SCAN_RESULTS = "/v1/scans/{scan_id}/results";
    private static final String SCAN_FILE = "/projects/projects/{project_id}/scans/{scan_id}/files?filePath={file_path};";
    private static final String CREATE_APPLICATION = "/applications/applications";
    private static final String CREATE_PROJECT = "/projects/projects";
    private static final String GET_PROJECTS = "/projects/projects?criteria=%7B%22criteria%22%3A%5B%7B%22key%22%3A%22applicationId%22%2C%22value%22%3A%22{app_id}%22%7D%5D%2C%22pagination%22%3A%7B%22currentPage%22%3A{cur_page}%2C%22pageSize%22%3A{page_size}%7D%2C%22sorting%22%3A%5B%5D%7D";
    private static final String GET_SCAN_STATUS = "/scans/scans?criteria=%7B%22filters%22%3A%5B%5D%2C%22criteria%22%3A%5B%7B%22key%22%3A%22projectId%22%2C%22value%22%3A%22{project_id}%22%7D%5D%2C%22sorting%22%3A%5B%5D%2C%22pagination%22%3A%7B%22currentPage%22%3A{cur_page}%2C%22pageSize%22%3A{page_size}%7D%7D";
    private static final String DEEP_LINK = "/scan/business-unit/%s/application/%s/project/%s/scans/%s";
    private static final String SCA_DEEP_LINK = "/scan/business-unit/%s/application/%s/project/%s";
    private static final String ADDITIONAL_DETAILS_KEY = "results";
    private static final String CXGO_SCAN_STATUS_DONE = "Done";
    private static final String CXGO_SCAN_STATUS_PARTIAL = "Partial";
    private static final String CXGO_SCAN_STATUS_COMPLETED = "Completed";

    /// CxOD required extra information for API calls not used by the SAST SDK. This
    /// data structure is used to capture that information as CxService calls are made
    /// during scan requests. This information is tracked using the current CxOD scan ID
    /// as the key. The 'scanIdMap' and 'scanProbeMap' have to be constructed using
    /// different keys because CxService is starting the processes differently; with new
    /// scans we start with asking CxOD for a scanID, and with requests for previous
    /// results we are starting with project information and trying to find the last
    /// scanID.
    //
    private static Map<String, CxScanParams> scanIdMap = new HashMap<>();
    //
    /// This was used for /scanresults API calls to avoid modifying CxFlow. This
    /// captures information at key points as CxService API calls are made so
    /// that it will be required later when needed using the team name as the key.
    //
    private static final List<CxScanParams> scanProbeMap = new LinkedList<>();

    private final CxGoProperties cxGoProperties;
    private final CxGoAuthService authClient;
    private final RestTemplate restTemplate;
    private final Map<String, Object> codeCache = new HashMap<>();
    private CxRepoFileHelper cxRepoFileHelper;
    private final FilterInputFactory filterInputFactory;
    private final FilterValidator filterValidator;

    public GoScanner(CxGoProperties cxProperties, CxGoAuthService authClient,
                     @Qualifier("cxRestTemplate") RestTemplate restTemplate,
                     FilterInputFactory filterInputFactory, FilterValidator filterValidator) {
        this.cxGoProperties = cxProperties;
        this.authClient = authClient;
        this.restTemplate = restTemplate;
        this.filterInputFactory = filterInputFactory;
        this.filterValidator = filterValidator;
        this.cxRepoFileHelper = new CxRepoFileHelper(cxGoProperties);
    }

    @Override
    public Integer getScanIdOfExistingScanIfExists(Integer projectId) {
        return UNKNOWN_INT;
    }

    @Override
    public void cancelScan(Integer scanId) throws CheckmarxException {

    }
    
    private String createApplication(String appName, String appDesc, String baBuId) {
        log.info("Creating new CxGo application {}.", appName);
        HttpEntity<String> httpEntity = new HttpEntity<>(
                getJSONCreateAppReq(appName, appDesc, baBuId),
                authClient.createAuthHeaders());
        ResponseEntity<OdApplicationCreate> createResp = restTemplate.exchange(
                cxGoProperties.getUrl().concat(CREATE_APPLICATION),
                HttpMethod.PUT,
                httpEntity,
                OdApplicationCreate.class);
        OdApplicationCreate appCreate = createResp.getBody();
        assert appCreate != null;
        return appCreate.getData().getBaId();
    }

    /**
     * Generate JSON http request body for creating new Application
     *
     * @return String representation of the process
     */
    private String getJSONCreateAppReq(String appName, String appDesc, String baBuId) {
        JSONObject requestBody = new JSONObject();
        JSONObject createBody = new JSONObject();
        try {
            createBody.put("baName", appName);
            createBody.put("description", appDesc);
            createBody.put("criticality", 5);
            createBody.put("baBuId", baBuId);
            createBody.put("licenseType", "standard");
            requestBody.put("businessApplication", createBody);
        } catch (JSONException e) {
            log.error("Error generating JSON App create Request object - JSON object will be empty");
        }
        return requestBody.toString();
    }

    public String createCxGoProject(String appId, String projectName, String presets) {
        log.info("Creating new CxGo project.");
        HttpEntity<?> httpEntity = new HttpEntity<>(
                getJSONCreateProjectReq(appId, projectName, presets),
                authClient.createAuthHeaders());
        ResponseEntity<OdProjectCreate> createResp = restTemplate.exchange(
                cxGoProperties.getUrl().concat(CREATE_PROJECT),
                HttpMethod.PUT,
                httpEntity,
                OdProjectCreate.class);
        OdProjectCreate appCreate = createResp.getBody();
        return appCreate.getData().getId();
    }

    /**
     * Generate JSON http request body for creating a new project
     *
     * @return String representation of the process
     */
    private String getJSONCreateProjectReq(String appId, String projectName, String preset) {
        JSONObject requestBody = new JSONObject();
        JSONObject createBody = new JSONObject();
        try {
            createBody.put("businessApplicationId", appId);
            createBody.put("name", projectName);
            createBody.put("description", "");
            if(StringUtils.isEmpty(preset)){
                preset = cxGoProperties.getScanPreset();
            }
            String [] presets = preset.split(",");
            createBody.put("typeIds", presets);
            createBody.put("criticality", 5);
            requestBody.put("project", createBody);
        } catch (JSONException e) {
            log.error("Error generating JSON Project create Request object - JSON object will be empty");
        }
        return requestBody.toString();
    }

    @Override
    public Integer createScan(CxScanParams params, String comment) throws CheckmarxException {
        //
        /// Create the project if it doesn't exist.
        //
        try {
            String appID = params.getTeamId();
            Integer projectID = getProjectId(appID, params.getProjectName());
            if (projectID.equals(UNKNOWN_INT)) {
                projectID = Integer.parseInt(createCxGoProject(appID, params.getProjectName(), params.getScanPreset()));
            }
            params.setProjectId(projectID);
            /// Create the scan
            CreateScan scan = CreateScan.builder()
                    .projectId(params.getProjectId())
                    .engineTypes(cxGoProperties.getEngineTypes())
                    .build();
            log.info("Sending scan to CxGo for projectID {}.", params.getProjectId());

            HttpHeaders headers = authClient.createAuthHeaders(params.getClientSecret());
            HttpEntity<CreateScan> httpEntity = new HttpEntity<>(scan, headers);
            ResponseEntity<CreateScanResponse> createResp = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(CREATE_SCAN),
                    HttpMethod.POST,
                    httpEntity,
                    CreateScanResponse.class);
            CreateScanResponse scanCreate = createResp.getBody();

            assert scanCreate != null;

            Integer scanId = scanCreate.getScan().getId();
            log.info("CxGo started scan with scanId {}.", scanId);
            ///The repo to be scanned is uploaded to amazon bucket
            log.info("CxGo Uploading Scan file {}.", scanId);

            File archive;
            if (params.getSourceType() == CxScanParams.Type.FILE) {
                archive = new File(params.getFilePath());
            } else {
                archive = new File(cxRepoFileHelper.prepareRepoFile(params));
            }

            uploadScanFile(scanCreate.getStorage(), archive);
            FileSystemUtils.deleteRecursively(archive);
            return scanId;
        }catch (HttpClientErrorException | HttpServerErrorException e){
            log.error("Http Exception: {}", ExceptionUtils.getRootCauseMessage(e), e);
            throw new CheckmarxException("Http error occurred");
        }catch (NullPointerException e){
            log.error("Null Exception: {}", ExceptionUtils.getRootCauseMessage(e), e);
            throw new CheckmarxException("NullPointerException occurred");
        }
    }

    /**
     * Upload Source to pre-signed URL
     *
     * @param scanStorage Response Object from CxGo for S3 details
     * @param file File to upload
     */
    private void uploadScanFile(Storage scanStorage, File file) throws CheckmarxException{
        try {
            Fields scanFields = scanStorage.getFields();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("key", scanFields.getKey());
            body.add("bucket", scanFields.getBucket());
            body.add("X-Amz-Algorithm", scanFields.getXAmzAlgorithm());
            body.add("X-Amz-Credential", scanFields.getXAmzCredential());
            body.add("X-Amz-Date", scanFields.getXAmzDate());
            body.add("X-Amz-Security-Token", scanFields.getXAmzSecurityToken());
            body.add("Policy", scanFields.getPolicy());
            body.add("X-Amz-Signature", scanFields.getXAmzSignature());

            FileSystemResource fsr = new FileSystemResource(file);
            body.add("file", fsr);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            restTemplate.exchange(
                    scanStorage.getUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch(HttpClientErrorException e) {
            log.error("CxGo error uploading file.", e);
            throw new CheckmarxException("Error Uploading Source to ".concat(scanStorage.getUrl()));
        }
    }

    /**
     * Searches the navigation tree for the Business Unit.
     * @return the Business Unit ID or -1
     */
    public String getTeamId(String teamPath) throws CheckmarxException {
        return extractTeamId(teamPath, null);
    }

    @Override
    public String getTeamIdByClientSecret(String teamPath, String clientSecret) throws CheckmarxException {
        return extractTeamId(teamPath, clientSecret);
    }

    private String extractTeamId(String teamPath, String clientSecret) throws CheckmarxException {
        String[] buTokens = teamPath.split(Pattern.quote("\\"));
        OdNavigationTree navTree = getNavigationTree(clientSecret);
        LinkedHashMap<String, ArrayList<Object>> navTreeData = (LinkedHashMap) navTree.getAdditionalProperties().get("data");
        ArrayList<LinkedHashMap<String, LinkedHashMap<String, Object>>> tree = (ArrayList) navTreeData.get("tree");
        int i = 1;
        String token = buTokens[i++];
        for (LinkedHashMap<String, LinkedHashMap<String, Object>> item : tree) {
            Object o = item.get("id");
            Integer id = (Integer) o;
            o = item.get("title");
            String title = (String) o;
            title = title.trim();
            o = item.get("children");
            ArrayList<Object> children = (ArrayList<Object>) o;
            if (title.equals(token)) {
                if (i == buTokens.length) {
                    CxScanParams csp = getScanProbeByTeam(id.toString());
                    csp.setTeamName(teamPath);
                    return id.toString();
                } else {
                    return searchTreeChildren(teamPath, buTokens, i, children);
                }
            }
        }
        return UNKNOWN;
    }

    private String searchTreeChildren(String teamPath, String []buTokens, int i, ArrayList<Object> children) {
        String token = buTokens[i++];
        for(Object item : children) {
            LinkedHashMap<String, Object> node = (LinkedHashMap<String, Object>)item;
            Object o = node.get("id");
            Integer id = (Integer) o;
            o = node.get("title");
            String title = (String)o;
            title = title.trim();
            o = node.get("children");
            ArrayList<Object> nodeChildren = (ArrayList<Object>)o;
            if(title.equals(token)) {
                if(i == buTokens.length) {
                    CxScanParams csp = getScanProbeByTeam(id.toString());
                    csp.setTeamName(teamPath);
                    return id.toString();
                } else {
                    return searchTreeChildren(teamPath, buTokens, i, nodeChildren);
                }
            }
        }
        return UNKNOWN;
    }

    private OdNavigationTree getNavigationTree(String clientSecret) throws CheckmarxException {
        HttpEntity<?> httpEntity = new HttpEntity<>(authClient.createAuthHeaders(clientSecret));
        try {
            log.debug("Retrieving OD Navigation Tree");
            ResponseEntity<OdNavigationTree> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat("/navigation-tree/navigation-tree"),
                    HttpMethod.GET,
                    httpEntity,
                    OdNavigationTree.class);
            return response.getBody();
        } catch(HttpStatusCodeException e) {
            log.error("Error occurred while retrieving the navigation tree.");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error retrieving Business Units.");
        }
    }

    @Override
    public String createTeam(String parentID, String teamName) throws CheckmarxException {
        return createApplication(teamName, "Generated by CxFlow", parentID);
    }

    @Override
    public ScanResults getReportContentByScanId(Integer scanId, FilterConfiguration filter) throws CheckmarxException {
        ScanResults.ScanResultsBuilder results = ScanResults.builder();
        Scan scan = getScanDetails(scanId);
        Integer projectId = scan.getProjectId();
        Integer buId = scan.getBusinessUnitId();
        Integer appId = scan.getApplicationId();

        GoScanResults resultFromAllEngines = getScanResults(scanId);

        List<ScanResults.XIssue> xIssues = new ArrayList<>();
        //SAST
        List<SASTScanResult> mainResultInfos = Optional.ofNullable(resultFromAllEngines)
                .map(GoScanResults::getSast)
                .orElse(null);

        if (mainResultInfos != null) {
            Map<String, OdScanResultItem> additionalResultInfos = getScanResultsPage(projectId, scanId);
            Map<String, Integer> issuesBySeverity = new HashMap<>();
            log.debug("SAST finding count before filtering: {}", mainResultInfos.size());
            log.info("Processing SAST results");
            mainResultInfos.stream()
                    .filter(applySastFilter(additionalResultInfos, filter))
                    .forEach(mainResultInfo -> handleSastIssue(xIssues, mainResultInfo, additionalResultInfos, projectId, scanId, issuesBySeverity));
            CxScanSummary scanSummary = getCxScanSummary(scan);
            Map<String, Object> flowSummary = new HashMap<>();
            flowSummary.put(Constants.SUMMARY_KEY, issuesBySeverity);
            flowSummary.put(Constants.SCAN_ID_KEY, scanId);
            results.additionalDetails(flowSummary);
            results.scanSummary(scanSummary);
        }

        //SCA
        List<SCAScanResult> rawScanResults = Optional.ofNullable(resultFromAllEngines)
                .map(GoScanResults::getSca).orElse(null);
        if (rawScanResults != null) {
            logRawScaScanResults(rawScanResults);

            List<Finding> findings = new ArrayList<>();
            List<Package> packages = new ArrayList<>();

            log.info("Processing SCA results");
            rawScanResults.stream()
                    .filter(rawScanResult -> !rawScanResult.isIgnored())
                    .filter(applyScaFilter(filter))
                    .forEach(rawScanResult -> handleScaIssue(xIssues, findings, packages, rawScanResult));

            logFindings(findings);
            logPackages(packages);

            SCAResults scaResults = new SCAResults();
            scaResults.setFindings(findings);
            scaResults.setPackages(packages);
            if (!rawScanResults.isEmpty()) {
                scaResults.setScanId(rawScanResults.get(0).getScanId().toString());
            }
            Summary summary = getScaScanSummary(scan);
            scaResults.setSummary(summary);
            String urlTemplate = cxGoProperties.getPortalUrl().concat(SCA_DEEP_LINK);
            String scaDeepLink = String.format(urlTemplate, buId, appId, projectId, scanId);
            scaResults.setWebReportLink(scaDeepLink);
            results.scaResults(scaResults);
        }

        results.xIssues(xIssues);
        results.projectId(projectId.toString());
        String urlTemplate = cxGoProperties.getPortalUrl().concat(DEEP_LINK);
        String deepLink = String.format(urlTemplate, buId, appId, projectId, scanId);
        results.link(deepLink);

        return results.build();
    }

    private static void logRawScaScanResults(List<SCAScanResult> rawScanResults) {
        if (log.isDebugEnabled()) {
            log.debug("Raw CxGo-SCA findings, before filtering:");
            rawScanResults.forEach(scaScanResult -> log.debug("Id: {}, package id: {}",
                    scaScanResult.getId(),
                    scaScanResult.getPackageId()));
        }
    }

    private static void logPackages(List<Package> packages) {
        if (log.isDebugEnabled()) {
            log.debug("CxGo-SCA packages:");
            packages.forEach(aPackage -> log.debug("Id: {}, version: {}",
                    aPackage.getId(),
                    aPackage.getVersion()));
        }
    }

    private static void logFindings(List<Finding> findings) {
        if (log.isDebugEnabled()) {
            log.debug("CxGo-SCA findings after filtering:");
            findings.forEach(finding -> log.debug("Id: {}, package id: {}", finding.getId(), finding.getPackageId()));
        }
    }

    private static Summary getScaScanSummary(Scan scanDetails) {
        Map<String, Object> scaScanDetails = (Map<String, Object>) scanDetails.getEngines().get("sca");
        Summary summary = new Summary();
        Map<Filter.Severity, Integer> severityMap = new EnumMap<>(Filter.Severity.class);
        if (scaScanDetails != null) {
            int high = (int) scaScanDetails.get("high_severities_count");
            int med = (int) scaScanDetails.get("medium_severities_count");
            int low = (int) scaScanDetails.get("low_severities_count");
            severityMap.put(Filter.Severity.HIGH, high);
            severityMap.put(Filter.Severity.MEDIUM, med);
            severityMap.put(Filter.Severity.LOW, low);
            severityMap.put(Filter.Severity.INFO, 0);
        }
        summary.setFindingCounts(severityMap);
        return summary;
    }

    private static CxScanSummary getCxScanSummary(Scan scanDetails) {
        CxScanSummary scanSummary = new CxScanSummary();
        Map<String, Object> sastScanDetails = (Map<String, Object>) scanDetails.getEngines().get("sast");
        if (sastScanDetails != null) {
            int high = (int) sastScanDetails.get("high_severities_count");
            int med = (int) sastScanDetails.get("medium_severities_count");
            int low = (int) sastScanDetails.get("low_severities_count");
            scanSummary.setHighSeverity(high);
            scanSummary.setMediumSeverity(med);
            scanSummary.setLowSeverity(low);
            scanSummary.setInfoSeverity(0); // Does not exist
        }
        return scanSummary;
    }

    private Predicate<SASTScanResult> applySastFilter(Map<String, OdScanResultItem> additionalResultInfos,
                                                      FilterConfiguration filter) {
        return mainResultInfo -> {
            String resultId = mainResultInfo.getId().toString();
            OdScanResultItem additionalResultInfo = additionalResultInfos.get(resultId);
            FilterInput filterInput = filterInputFactory.createFilterInputForCxGoSast(mainResultInfo, additionalResultInfo);
            return filterValidator.passesFilter(filterInput, filter.getSastFilters());
        };
    }


    private Predicate<? super SCAScanResult> applyScaFilter(FilterConfiguration filterConfig) {
        return rawScanResult -> {
            FilterInput filterInput = filterInputFactory.createFilterInputForCxGoSca(rawScanResult);
            return filterValidator.passesFilter(filterInput, filterConfig.getScaFilters());
        };
    }

    private void handleSastIssue(List<ScanResults.XIssue> xIssues, SASTScanResult sastResult,
                                Map<String, OdScanResultItem> scanResultItems,
                                int projectId, int scanId, Map<String, Integer> issuesBySeverity){
        boolean newIssue = true;
        OdScanResultItem x = scanResultItems.get(sastResult.getId().toString());
        sastResult.setVulnerabilityType(x.getTitle());
        ScanResults.XIssue xIssue = ScanResults.XIssue.builder()
                .vulnerability(sastResult.getVulnerabilityType())
                .file(sastResult.getSourceNode().getFilePath())
                .description(sastResult.getDescription())
                .cwe(sastResult.getCwe())
                .language(sastResult.getLanguageName())
                .severity(sastResult.getSeverity().getSeverity())
                .similarityId(sastResult.getSimilarityId().toString())
                .build();

        if(xIssues.contains(xIssue)){
            ScanResults.XIssue tmp = xIssues.get(xIssues.indexOf(xIssue));
            if(tmp != null){
                newIssue = false;
                xIssue = tmp;
            }
        }
        else {
            Integer count = issuesBySeverity.get(sastResult.getSeverity().getSeverity());
            if(count == null){
                issuesBySeverity.put(sastResult.getSeverity().getSeverity(), 1);
            }
            else {
                count ++;
                issuesBySeverity.put(sastResult.getSeverity().getSeverity(), count);
            }
            xIssue.setDetails(new HashMap<>());
        }

        if (sastResult.getState() == SASTScanResult.State.NOT_EXPLOITABLE.getValue()) {
            setXIssueFalsePositiveCount(xIssue);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("source",getNodeData(sastResult.getSourceNode()));
        result.put("sink",getNodeData(sastResult.getSinkNode()));
        result.put("state", sastResult.getState());

        List<Map<String, Object>> resultList;
        if (xIssue.getAdditionalDetails() == null || xIssue.getAdditionalDetails().get(ADDITIONAL_DETAILS_KEY) == null) {  //new list
            resultList = new ArrayList<>();
            if (xIssue.getAdditionalDetails() == null){
                xIssue.setAdditionalDetails(new HashMap<>());
            }
        } else {
            resultList = (List<Map<String, Object>>) xIssue.getAdditionalDetails().get(ADDITIONAL_DETAILS_KEY);
        }
        
        resultList.add(result);

        int loc = sastResult.getSourceNode().getLine();
        String snippet = extractCodeSnippet(projectId, scanId, loc, sastResult.getSourceNode().getFilePath());

        ScanResults.IssueDetails details = new ScanResults.IssueDetails();
        details.setCodeSnippet(snippet);
        if(sastResult.getState().equals(SASTScanResult.State.NOT_EXPLOITABLE.getValue())) {
            details.setFalsePositive(true);
        }
        xIssue.getDetails().put(loc, details);
        xIssue.getAdditionalDetails().put(ADDITIONAL_DETAILS_KEY, resultList);
        if(newIssue){ //only if the issue is new, add, otherwise references were updated
            xIssues.add(xIssue);
        }
    }

    private void setXIssueFalsePositiveCount(ScanResults.XIssue xIssue) {
        xIssue.setFalsePositiveCount(xIssue.getFalsePositiveCount() +1);
    }

    /**
     * Creates and adds a new item to each of the input lists based on scaResult properties.
     */
    private void handleScaIssue(List<ScanResults.XIssue> xIssues, List<Finding> findings, List<Package> packages, SCAScanResult scaResult) {
        Finding finding = toFinding(scaResult);
        findings.add(finding);

        Package pkg = toPackage(scaResult);
        packages.add(pkg);

        ScanResults.ScaDetails scaDetail = ScanResults.ScaDetails.builder()
                .finding(finding)
                .vulnerabilityLink("N/A")
                .vulnerabilityPackage(pkg)
                .build();

        List<ScanResults.ScaDetails> scaDetails = Collections.singletonList(scaDetail);

        xIssues.add(ScanResults.XIssue.builder()
                .similarityId(finding.getSimilarityId())
                .severity(finding.getSeverity().toString())
                .description(finding.getDescription())
                .scaDetails(scaDetails)
                .build());
    }

    private static Finding toFinding(SCAScanResult scaResult) {
        Finding finding = new Finding();
        finding.setCveName(scaResult.getCveName());
        finding.setDescription(scaResult.getDescription());
        finding.setId(scaResult.getId());
        finding.setIgnored(scaResult.isIgnored());
        finding.setPackageId(scaResult.getPackageId());
        finding.setFixResolutionText(scaResult.getFixResolutionText());
        finding.setPublishDate(scaResult.getPublishedAt());
        finding.setScore(scaResult.getScore());
        finding.setSimilarityId(scaResult.getSimilarityId());
        finding.setSeverity(Severity.valueOf(scaResult.getSeverity().getSeverity().toUpperCase()));
        finding.setSeverity(Severity.valueOf(scaResult.getSeverity().getSeverity().toUpperCase()));
        return finding;
    }

    private static Package toPackage(SCAScanResult scaResult) {
        Package pkg = new Package();
        pkg.setId(scaResult.getPackageId());
        pkg.setVersion(scaResult.getFixResolutionText());
        pkg.setName(scaResult.getPackageId());
        return pkg;
    }


    private Map<String, String> getNodeData(ResultNode node) {
        // Node data: file/line/object
        Map<String, String> nodeData = new HashMap<>();
        nodeData.put("file", node.getFileName());
        nodeData.put("line", node.getLine().toString());
        nodeData.put("column", node.getColumn().toString());
        nodeData.put("object", node.getName());
        return nodeData;
    }

    private void updateIssueSummary(CxScanSummary scanSummary, OdScanResultItem vulnerability) {
        if(scanSummary.getLowSeverity() == null) scanSummary.setLowSeverity(0);
        if(scanSummary.getMediumSeverity() == null) scanSummary.setMediumSeverity(0);
        if(scanSummary.getHighSeverity() == null) scanSummary.setHighSeverity(0);
        if(vulnerability.getSeverity().equals("low")) {
            scanSummary.setLowSeverity(scanSummary.getLowSeverity() + 1);
        }
        if(vulnerability.getSeverity().equals("medium")) {
            scanSummary.setMediumSeverity(scanSummary.getMediumSeverity() + 1);
        }
        if(vulnerability.getSeverity().equals("high")) {
            scanSummary.setHighSeverity(scanSummary.getHighSeverity() + 1);
        }
    }

    /**
     * Fetches the source file and extract the code on the line with the error. This attempts to
     * cache downloaded source files in memory to try conserve network bandwidth.
     *
     * @param projectId project to get source from
     * @param scanId specific scan within project to pull source file from
     * @param filePath the path to the file int he code base
     * @return String containing the extracted source file
     */
    private String extractCodeSnippet(Integer projectId,
                                      Integer scanId,
                                      Integer lineNumber,
                                      String filePath) {
        // Does the cache already contain the source file?
        String sourceCode;
        if(codeCache.containsKey(filePath)) {
            sourceCode = (String)codeCache.get(filePath);
        } else {
            HttpEntity<?> httpEntity = new HttpEntity<>(null, authClient.createAuthHeaders());
            ResponseEntity<OdScanFileResult> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(SCAN_FILE),
                    HttpMethod.GET,
                    httpEntity,
                    OdScanFileResult.class,
                    projectId,
                    scanId,
                    filePath
            );
            OdScanFileResult sfr = response.getBody();
            assert sfr != null;
            sourceCode = sfr.getData().getCode();
            codeCache.put(filePath, sourceCode);
        }
        //
        /// Now extract the code snippet to display
        //
        String codeLine = "NOT FOUND!";
        try {
            Reader code = new StringReader(sourceCode);
            BufferedReader codeReader = new BufferedReader(code);
            int curLine = 1;
            while((codeLine = codeReader.readLine()) != null) {
                if(curLine == lineNumber) {
                    break;
                }
                curLine++;
            }
        } catch(IOException e) {
            log.error("Error parsing source file: {}.", filePath);
        }
        assert codeLine != null;
        return codeLine.replace("\r","").replace("\n","");
    }


    public Integer getProjectId(String ownerId, String name) {
        log.debug("Retrieving OD Project List");
        OdProjectList appList = getProjectPage(ownerId);
        for(OdProjectListDataItem item : appList.getData().getItems()) {
            if(item.getName().equals(name)) {
                CxScanParams csp = getScanProbeByTeam(ownerId);
                csp.setProjectId(item.getId());
                return item.getId();
            }
        }
        return UNKNOWN_INT;
    }

    private OdProjectList getProjectPage(String ownerId) {
        HttpEntity<Void> httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        OdProjectList appList = new OdProjectList();
        boolean morePages = true;
        int curPage = 0;
        int pageSize = 50;
        long totalCount = 0;
        long rcvItemCnt = 0;
        while(morePages) {
            // Fetch the current page
            ResponseEntity<OdProjectList> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(GET_PROJECTS),
                    HttpMethod.GET,
                    httpEntity,
                    OdProjectList.class,
                    ownerId,
                    curPage,
                    pageSize);
            // Are there more results
            OdProjectList curList = response.getBody();
            if(curPage == 0) totalCount = curList.getData().getTotalCount();
            rcvItemCnt += curList.getData().getItems().size();
            // There are more items, add them to the list
            if (appList.getData() == null) {
                appList.setData(curList.getData());
            } else {
                appList.getData().getItems().addAll(curList.getData().getItems());
            }
            if(rcvItemCnt < totalCount) {
                curPage++;
            } else {
                morePages = false;
            }
        }
        return appList;
    }

    @Override
    public void waitForScanCompletion(Integer scanId) throws CheckmarxException {
        ScanStatus scanStatus = getScanStatusById(scanId);
        ScanStatus.Status status = scanStatus.getStatus();
        long timer = 0;
        try {
            while(!status.equals(ScanStatus.Status.COMPLETED) &&
                    !status.equals(ScanStatus.Status.FAILED) && !status.equals(ScanStatus.Status.PARTIAL)) {
                Thread.sleep(cxGoProperties.getScanPolling());
                scanStatus = getScanStatusById(scanId);
                status = scanStatus.getStatus();
                timer += cxGoProperties.getScanPolling();
                log.info("scanId: {}, status: {}, progress: {}", scanId, scanStatus.getStatus(), scanStatus.getProgress());
                if(timer >= (cxGoProperties.getScanTimeout() * 60000)) {
                    log.error("Scan timeout exceeded.  {} minutes", cxGoProperties.getScanTimeout());
                    throw new CheckmarxException("Timeout exceeded during scan");
                }
            }
        } catch(InterruptedException e) {
            log.error("Thread sleep error waiting for scan status!");
        }
        log.info("scanId: {}, status: {}, progress: {}", scanId, scanStatus.getStatus(), scanStatus.getProgress());
        if (status.equals(ScanStatus.Status.FAILED)) {
            throw new CheckmarxException("Scan was cancelled or failed");
        }

        if (status.equals(ScanStatus.Status.PARTIAL) && scanStatus.getProgress() < 100) {
            try {
                Thread.sleep(cxGoProperties.getScanPolling());
                waitForScanCompletion(scanId);
            } catch (InterruptedException e) {
                log.error("Thread sleep error waiting for scan status!");
            }
        }
    }

    private OdScanList getScanStatusPage(Integer projectId) {
        HttpEntity<Void> httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        OdScanList appList = new OdScanList();
        boolean morePages = true;
        int curPage = 0;
        int pageSize = 50;
        long totalCount = 0;
        long rcvItemCnt = 0;
        while(morePages) {
            // Fetch the current page
            ResponseEntity<OdScanList> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(GET_SCAN_STATUS),
                    HttpMethod.GET,
                    httpEntity,
                    OdScanList.class,
                    projectId,
                    curPage,
                    pageSize);
            // Are there more results
            OdScanList curList = response.getBody();
            if(curPage == 0) totalCount = curList.getData().getTotalCount();
            rcvItemCnt += curList.getData().getItems().size();
            // There are more items, add them to the list
            if (appList.getData() == null) {
                appList.setData(curList.getData());
            } else {
                appList.getData().getItems().addAll(curList.getData().getItems());
            }
            if(rcvItemCnt < totalCount) {
                curPage++;
            } else {
                morePages = false;
            }
        }
        return appList;
    }

    /**
     * CxOD doesn't have projects in the same sense as normal SAST, this fakes
     * it a little bit.
     *
     * @param projectId - the ID of the project setup
     * @return the "simulated" project information
     */

    public CxProject getProject(Integer projectId) {
        List<CxProject.CustomField> customFields = new ArrayList<>();
        CxProject.CxProjectBuilder builder = CxProject.builder();
        builder.id(projectId);
        builder.isPublic(true);
        builder.name("CxGo Temporary Project");
        builder.teamId(null);
        builder.links(null);
        builder.customFields(customFields);
        return builder.build();
    }

    /**
     * If this is used for CxFlow /scanresults API calls. The ScanID will only contain the
     * scan record if CxOD hasn't been restarted since the scan was run. This ensures the
     * scan record is available in memory so that CxService can correctly look up the values.
     */
    private void setupScanIdMap(Integer scanID, Integer projectID) {
        CxScanParams csp = getScanProbeByProject(projectID.toString());
        if(csp != null) {
            scanIdMap.put(scanID.toString(), csp);
        }
    }

    @Override
    public Integer getLastScanId(Integer projectId) {
        OdScanList appList = getScanStatusPage(projectId);
        for(OdScanListDataItem item : appList.getData().getItems()) {
            if(item.getStatus().equals(CXGO_SCAN_STATUS_DONE) || item.getStatus().equals(CXGO_SCAN_STATUS_PARTIAL) || item.getStatus().equals(CXGO_SCAN_STATUS_COMPLETED)) {
                this.setupScanIdMap(item.getId(), projectId);
                log.debug("getLastScanId - Found scanId '{}', with status: '{}'", item.getId(), item.getStatus());
                return item.getId();
            }
        }
        return UNKNOWN_INT;
    }

    /**
     * Examines the current scan scanProbeMap and returns the record matching the teamID
     * 'if' it exsits.
     *
     * @return the CxScanParams record
     */
    private CxScanParams getScanProbeByTeam(String teamID) {
        // First check it if it exists
        for(CxScanParams csp: scanProbeMap) {
            if(csp.getTeamId().equals(teamID)) {
                return csp;
            }
        }
        // If it doesn't exist then create it
        CxScanParams csp = new CxScanParams();
        csp.setTeamId(teamID);
        scanProbeMap.add(csp);
        return csp;
    }

    /**
     * Examines the current scan scanProbeMap and returns the record matching the teamID
     * 'if' it exsits.
     *
     * @return the CxScanParams record
     */
    private CxScanParams getScanProbeByProject(String projectID) {
        for(CxScanParams csp: scanProbeMap) {
            if(csp.getProjectId().toString().equals(projectID)) {
                return csp;
            }
        }
        return null;
    }
    

    public ScanStatus getScanStatusById(Integer scanId) throws CheckmarxException {
        HttpEntity<?> httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.debug("Retrieving ScanStatus for Scan Id {}", scanId);
            ResponseEntity<ScanStatus> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(SCAN_STATUS),
                    HttpMethod.GET,
                    httpEntity,
                    ScanStatus.class,
                    scanId);
            return response.getBody();
        } catch(HttpStatusCodeException e) {
            log.error("Error occurred while retrieving the scan status for id {}.", scanId);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error occurred while retrieving the scan status for id ".concat(Integer.toString(scanId)));
        }
    }

    public Scan getScanDetails(Integer scanId) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.debug("Retrieving scan with id {}", scanId);
            ResponseEntity<Scan> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(SCAN),
                    HttpMethod.GET,
                    httpEntity,
                    Scan.class,
                    scanId);

            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new CheckmarxRuntimeException("Scan details response body is missing."));
        } catch(HttpStatusCodeException e) {
            log.error("Error occurred while retrieving the scan with id {}", scanId);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error occurred while retrieving the scan with id".concat(Integer.toString(scanId)));
        }
    }

    public List<Scan> getScans() throws CheckmarxException {
        HttpEntity<?> httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.debug("Retrieving all scans");
            ResponseEntity<Scan[]> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(SCANS),
                    HttpMethod.GET,
                    httpEntity,
                    Scan[].class);
            return Arrays.asList(Objects.requireNonNull(response.getBody()));
        } catch(HttpStatusCodeException e) {
            log.error("Error occurred while retrieving scans");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error occurred while retrieving scans");
        }
    }

    private GoScanResults getScanResults(Integer scanId) throws CheckmarxException {
        HttpEntity<?> httpEntity = new HttpEntity<>(authClient.createAuthHeaders());

        try {
            log.info("Retrieving Scan Results for Scan Id {} ", scanId);
            ResponseEntity<GoScanResults> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(SCAN_RESULTS),
                    HttpMethod.GET,
                    httpEntity,
                    GoScanResults.class,
                    scanId);
            return response.getBody();
        } catch(HttpStatusCodeException e) {
            log.error("Error occurred while retrieving the scan results for id {}.", scanId);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error occurred while retrieving the scan status for id ".concat(Integer.toString(scanId)));
        }
    }

    private Map<String, OdScanResultItem> getScanResultsPage(Integer projectId, Integer scanId) {
        HttpEntity<?> httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        OdScanResults appList = new OdScanResults();
        boolean morePages = true;
        int curPage = 0;
        int pageSize = 50;
        long totalCount = 0;
        long rcvItemCnt = 0;
        while(morePages) {
            // Fetch the current page
            ResponseEntity<OdScanResults> response = restTemplate.exchange(
                    cxGoProperties.getUrl().concat(SCAN_RESULTS_ENCODED),
                    HttpMethod.GET,
                    httpEntity,
                    OdScanResults.class,
                    projectId,
                    scanId,
                    curPage,
                    pageSize
            );
            // Are there more results
            OdScanResults curList = response.getBody();
            if(curPage == 0) totalCount = curList.getData().getTotalCount();
            rcvItemCnt += curList.getData().getItems().size();
            // There are more items, add them to the list
            if (appList.getData() == null) {
                appList.setData(curList.getData());
            } else {
                appList.getData().getItems().addAll(curList.getData().getItems());
            }
            if(rcvItemCnt < totalCount) {
                curPage++;
            } else {
                morePages = false;
            }
        }
        //create a map lookup based on the id
        return appList.getData()
                        .getItems()
                        .stream()
                        .collect(Collectors.toMap(
                                odScanResultItem -> odScanResultItem.getId().toString(),
                                Function.identity(),
                                (thisItem, nextItem) -> nextItem)
                        );
    }
    

    @Override
    public Integer getProjectPresetId(Integer projectId) {
        return UNKNOWN_INT;
    }


    @Override
    public String getPresetName(Integer presetId) {
        return null;
    }
    
    @Override
    public String getScanConfigurationName(int configurationId) {
        return null;
    }

    @Override
    public CxScanSettings getScanSettingsDto(int projectId) {
        return null;
    }

    @Override
    public CxPropertiesBase getCxPropertiesBase() {
        return cxGoProperties;
    }
}
