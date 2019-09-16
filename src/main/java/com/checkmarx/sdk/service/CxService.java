package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.*;
import com.checkmarx.sdk.dto.cx.xml.*;
import com.checkmarx.sdk.exception.CheckmarxLegacyException;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.utils.ScanUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Class used to orchestrate submitting scans and retrieving results
 */
@Service
public class CxService implements CxClient{

    private static final String UNKNOWN = "-1";
    private static final Integer UNKNOWN_INT = -1;
    private static final Integer SCAN_STATUS_NEW = 1;
    private static final Integer SCAN_STATUS_PRESCAN = 2;
    private static final Integer SCAN_STATUS_QUEUED = 3;
    private static final Integer SCAN_STATUS_SCANNING = 4;
    private static final Integer SCAN_STATUS_POST_SCAN = 6;
    private static final Integer SCAN_STATUS_FINISHED = 7;
    private static final Integer SCAN_STATUS_CANCELED = 8;
    private static final Integer SCAN_STATUS_FAILED = 9;
    private static final Integer SCAN_STATUS_SOURCE_PULLING = 10;
    private static final Integer SCAN_STATUS_NONE = 1001;
    /*report statuses TODO*/
    public static final Integer REPORT_STATUS_CREATED = 2;
    public static final Integer REPORT_STATUS_FINISHED = 7;
    private static final Map<String, Integer> STATUS_MAP = ImmutableMap.of(
            "TO VERIFY", 1,
            "CONFIRMED", 2,
            "URGENT", 3
    );
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxService.class);
    private static final String TEAMS = "/auth/teams";
    private static final String TEAM = "/auth/teams/{id}";
    private static final String TEAM_LDAP = "/auth/LDAPServers/{id}/TeamMappings";
    private static final String TEAM_LDAP_MAPPINGS = "/auth/LDAPTeamMappings?ldapServerId={id}";
    private static final String TEAM_LDAP_MAPPING = "/auth/LDAPTeamMappings/{id}";
    private static final String ROLE = "/auth/Roles";
    private static final String ROLE_MAPPING = "/auth/LDAPServers/{id}/RoleMappings";
    private static final String ROLE_MAPPINGS = "/auth/LDAPRoleMappings?ldapServerId={id}";
    private static final String LDAP_SERVER = "/auth/LDAPServers";
    private static final String PROJECTS = "/projects";
    private static final String PROJECT = "/projects/{id}";
    private static final String PROJECT_SOURCE = "/projects/{id}/sourceCode/remoteSettings/git";
    private static final String PROJECT_SOURCE_FILE = "/projects/{id}/sourceCode/attachments";
    private static final String PROJECT_EXCLUDE = "/projects/{id}/sourceCode/excludeSettings";
    private static final String PRESETS = "/sast/presets";
    private static final String SCAN_CONFIGURATIONS = "/sast/engineConfigurations";
    private static final String SCAN_SETTINGS = "/sast/scanSettings";
    private static final String SCAN = "/sast/scans";
    private static final String SCAN_SUMMARY = "/sast/scans/{id}/resultsStatistics";
    private static final String PROJECT_SCANS = "/sast/scans?projectId={pid}";
    private static final String SCAN_STATUS = "/sast/scans/{id}";
    private static final String REPORT = "/reports/sastScan";
    private static final String REPORT_DOWNLOAD = "/reports/sastScan/{id}";
    private static final String REPORT_STATUS = "/reports/sastScan/{id}/status";
    private static final String OSA_VULN = "Vulnerable_Library";
    private final CxProperties cxProperties;
    private final CxLegacyService cxLegacyService;
    private final CxAuthClient authClient;
    private final RestTemplate restTemplate;

    public CxService(CxAuthClient authClient, CxProperties cxProperties, CxLegacyService cxLegacyService, @Qualifier("cxRestTemplate") RestTemplate restTemplate) {
        this.authClient = authClient;
        this.cxProperties = cxProperties;
        this.cxLegacyService = cxLegacyService;
        this.restTemplate = restTemplate;
    }

    /**
     * Create Scan for a projectId
     *
     * @param projectId
     * @param incremental
     * @param isPublic
     * @param forceScan
     * @param comment
     * @return
     */
    public Integer createScan(Integer projectId, boolean incremental, boolean isPublic, boolean forceScan, String comment) {
        CxScan scan = CxScan.builder()
                .projectId(projectId)
                .isIncremental(incremental)
                .forceScan(forceScan)
                .isPublic(isPublic)
                .comment(comment)
                .build();
        HttpEntity<CxScan> requestEntity = new HttpEntity<>(scan, authClient.createAuthHeaders());

        log.info("Creating Scan for project Id {}", projectId);
        try {
            String response = restTemplate.postForObject(cxProperties.getUrl().concat(SCAN), requestEntity, String.class);
            JSONObject obj = new JSONObject(response);
            String id = obj.get("id").toString();
            log.info("Scan created with Id {} for project Id {}", id, projectId);
            return Integer.parseInt(id);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating Scan for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    @Override
    public Integer getLastScanId(Integer projectId) {
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());

        log.info("Finding last Scan Id for project Id {}", projectId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(SCAN)
                            .concat("?projectId=").concat(projectId.toString().concat("&scanStatus=")
                                    .concat(SCAN_STATUS_FINISHED.toString()).concat("&last=1")),
                    HttpMethod.GET, requestEntity, String.class);

            JSONArray arr = new JSONArray(response.getBody());
            if (arr.length() < 1) {
                return UNKNOWN_INT;
            }
            JSONObject obj = arr.getJSONObject(0);
            String id = obj.get("id").toString();
            log.info("Scan found with Id {} for project Id {}", id, projectId);
            return Integer.parseInt(id);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating Scan for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    /**
     * Fetches scan data based on given scan identifier, as a {@link JSONObject}.
     * @param scanId scan ID to use
     * @return  populated {@link JSONObject} if scan data was fetched; empty otherwise.
     */
    @Override
    public JSONObject getScanData(String scanId) {
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());
        JSONObject scanData = new JSONObject();
        log.info("Fetching Scan data for Id {}", scanId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    cxProperties.getUrl().concat(SCAN).concat("/").concat(scanId),
                    HttpMethod.GET, requestEntity, String.class);

            scanData = new JSONObject(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while fetching Scan data for scan Id {}, http error {}", scanId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return scanData;
    }

    @Override
    public LocalDateTime getLastScanDate(Integer projectId) {
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        log.info("Finding last Scan Id for project Id {}", projectId);
        try {
            ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(SCAN)
                            .concat("?projectId=").concat(projectId.toString().concat("&scanStatus=").concat(SCAN_STATUS_FINISHED.toString())
                                    .concat("&last=").concat(cxProperties.getIncrementalNumScans().toString())),
                    HttpMethod.GET, requestEntity, String.class);

            JSONArray arr = new JSONArray(response.getBody());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject scan = arr.getJSONObject(i);
                if (!scan.getBoolean("isIncremental")) {
                    JSONObject dateAndTime = scan.getJSONObject("dateAndTime");
                    log.debug("Last full scan was {}", dateAndTime);
                    //example: "finishedOn": "2018-06-18T01:09:12.707", Grab only first 19 digits due to inconsistency of checkmarx results
                    LocalDateTime d;
                    try {
                        String finishedOn = dateAndTime.getString("finishedOn");
                        finishedOn = finishedOn.substring(0, 19);
                        log.debug("finishedOn: {}", finishedOn);
                        d = LocalDateTime.parse(finishedOn, formatter);
                        return d;
                    } catch (DateTimeParseException e) {
                        log.warn("Error Parsing last finished scan time {}", e.getParsedString());
                        log.error(ExceptionUtils.getStackTrace(e));
                        return null;
                    }
                }
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating Scan for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (NullPointerException e) {
            log.error("Error parsing JSON response for dateAndTime status. {}", ExceptionUtils.getMessage(e));
        }
        return null;
    }


    /**
     * Get the status of a given scanId
     *
     * @param scanId
     * @return
     */
    @Override
    public Integer getScanStatus(Integer scanId) {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        log.debug("Retrieving xml status of xml Id {}", scanId);
        try {
            ResponseEntity<String> projects = restTemplate.exchange(cxProperties.getUrl().concat(SCAN_STATUS), HttpMethod.GET, httpEntity, String.class, scanId);
            JSONObject obj = new JSONObject(projects.getBody());
            JSONObject status = obj.getJSONObject("status");
            log.debug("status id {}, status name {}", status.getInt("id"), status.getString("name"));
            return status.getInt("id");
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Status Code of {} while getting xml status for xml Id {}", e.getStatusCode(), scanId);
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    /**
     * Generate a scan report request (xml) based on ScanId
     *
     * @param scanId
     * @return
     */
    @Override
    public Integer createScanReport(Integer scanId) {
        String strJSON = "{'reportType':'XML', 'scanId':%d}";
        strJSON = String.format(strJSON, scanId);
        HttpEntity requestEntity = new HttpEntity<>(strJSON, authClient.createAuthHeaders());

        try {
            log.info("Creating report for xml Id {}", scanId);
            ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(REPORT), HttpMethod.POST, requestEntity, String.class);
            JSONObject obj = new JSONObject(response.getBody());
            Integer id = obj.getInt("reportId");
            log.info("Report with Id {} created", id);
            return id;
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Status Code of {} while creating xml report for xml Id {}", e.getStatusCode(), scanId);
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    /**
     * Get the status of a report being generated by reportId
     *
     * @param reportId
     * @return
     */
    @Override
    public Integer getReportStatus(Integer reportId) {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        log.info("Retrieving report status of report Id {}", reportId);
        try {
            ResponseEntity<String> projects = restTemplate.exchange(cxProperties.getUrl().concat(REPORT_STATUS), HttpMethod.GET, httpEntity, String.class, reportId);
            JSONObject obj = new JSONObject(projects.getBody());
            JSONObject status = obj.getJSONObject("status");
            log.debug("Report status is {} - {} for report Id {}", status.getInt("id"), status.getString("value"), reportId);
            return status.getInt("id");
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Status Code of {} while getting report status for report Id {}", e.getStatusCode(), reportId);
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    /**
     * Retrieve the report by reportId, mapped to ScanResults DTO, applying filtering as requested
     *
     * @param scanId
     * @param filter
     * @return
     * @throws CheckmarxException
     */
    public ScanResults getReportContentByScanId(Integer scanId, List<Filter> filter) throws CheckmarxException{
        Integer reportId = createScanReport(scanId);
        try {
            Thread.sleep(cxProperties.getReportPolling());
            int timer = 0;
            while (getReportStatus(reportId).equals(CxService.REPORT_STATUS_FINISHED)) {
                Thread.sleep(cxProperties.getReportPolling());
                timer += cxProperties.getReportPolling();
                if (timer >= cxProperties.getReportTimeout()) {
                    log.error("Report Generation timeout.  {}", cxProperties.getReportTimeout());
                    throw new CheckmarxException("Timeout exceeded during report generation");
                }
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
            throw new CheckmarxException("Interrupted Exception Occurred");
        }
        return getReportContent(reportId, filter);
    }
    /**
     * Retrieve the report by reportId, mapped to ScanResults DTO, applying filtering as requested
     *
     * @param reportId
     * @param filter
     * @return
     * @throws CheckmarxException
     */
    @Override
    public ScanResults getReportContent(Integer reportId, List<Filter> filter) throws CheckmarxException {
        HttpHeaders headers = authClient.createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity httpEntity = new HttpEntity<>(headers);
        String session = null;
        try {
            /* login to legacy SOAP CX Client to retrieve description */
            session = cxLegacyService.login(cxProperties.getUsername(), cxProperties.getPassword());
        } catch (CheckmarxLegacyException e) {
            log.error("Error occurring while logging into Legacy SOAP based WebService - issue description will remain blank");
        }
        log.info("Retrieving report contents of report Id {} in XML format", reportId);
        try {
            ResponseEntity<String> resultsXML = restTemplate.exchange(cxProperties.getUrl().concat(REPORT_DOWNLOAD), HttpMethod.GET, httpEntity, String.class, reportId);
            String xml = resultsXML.getBody();
            log.debug("Report length: {}", xml.length());
            log.debug("Headers: {}", resultsXML.getHeaders().toSingleValueMap().toString());
            log.info("Report downloaded for report Id {}", reportId);
            log.debug("XML String Output: {}", xml);
            log.debug("Base64:", Base64.getEncoder().encodeToString(resultsXML.toString().getBytes()));
            /*Remove any chars before the start xml tag*/
            xml = xml.trim().replaceFirst("^([\\W]+)<", "<");
            log.debug("Report length: {}", xml.length());
            String xml2 = ScanUtils.cleanStringUTF8_2(xml);
            log.trace("XML2: {}", xml2);
            InputStream xmlStream = new ByteArrayInputStream(Objects.requireNonNull(xml2.getBytes()));

            /* protect against XXE */
            JAXBContext jc = JAXBContext.newInstance(CxXMLResultsType.class);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            List<ScanResults.XIssue> xIssueList = new ArrayList<>();
            CxXMLResultsType cxResults;
            try {
                XMLStreamReader xsr = xif.createXMLStreamReader(xmlStream);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                cxResults = (CxXMLResultsType) unmarshaller.unmarshal(xsr);
            }catch (UnmarshalException e){
                log.warn("Issue occurred performing unmashall step - trying again {}", ExceptionUtils.getMessage(e));
                if(resultsXML.getBody() != null) {
                    log.error("Writing raw response from CX to {}", "CX_".concat(String.valueOf(reportId)));
                    ScanUtils.writeByte("CX_".concat(String.valueOf(reportId)), resultsXML.getBody().getBytes());
                    xml2 = ScanUtils.cleanStringUTF8(xml);
                    xmlStream = new ByteArrayInputStream(Objects.requireNonNull(xml2.getBytes()));
                    XMLStreamReader xsr = xif.createXMLStreamReader(xmlStream);
                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    cxResults = (CxXMLResultsType) unmarshaller.unmarshal(xsr);
                }
                else{
                    log.error("CX Response for report {} was null", reportId);
                    throw new CheckmarxException("CX report was empty (null)");
                }
            }

            ScanResults.ScanResultsBuilder cxScanBuilder = ScanResults.builder();
            cxScanBuilder.projectId(cxResults.getProjectId());
            cxScanBuilder.team(cxResults.getTeam());
            cxScanBuilder.project(cxResults.getProjectName());
            cxScanBuilder.link(cxResults.getDeepLink());
            cxScanBuilder.files(cxResults.getFilesScanned());
            cxScanBuilder.loc(cxResults.getLinesOfCodeScanned());
            cxScanBuilder.scanType(cxResults.getScanType());
            Map<String, Integer> summary = getIssues(filter, session, xIssueList, cxResults);
            cxScanBuilder.xIssues(xIssueList);
            cxScanBuilder.additionalDetails(getAdditionalScanDetails(cxResults));
            CxScanSummary scanSummary = getScanSummaryByScanId(Integer.valueOf(cxResults.getScanId()));
            cxScanBuilder.scanSummary(scanSummary);
            ScanResults results = cxScanBuilder.build();
            //Add the summary map (severity, count)
            results.getAdditionalDetails().put(Constants.SUMMARY_KEY, summary);
            if (cxProperties.getPreserveXml()) {
                results.setOutput(xml);
            }
            return results;
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Status Code of {} while getting downloading report contents of report Id {}", e.getStatusCode(), reportId);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results for report Id ".concat(reportId.toString()));
        } catch (XMLStreamException | JAXBException e) {
            log.error("Error with XML report");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results for report Id ".concat(reportId.toString()));
        } catch (NullPointerException e) {
            log.info("Null Error");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results for report Id ".concat(reportId.toString()));
        }
    }

    /**
     * Retrieve the report by reportId, mapped to ScanResults DTO, applying filtering as requested
     *
     * @param reportId
     * @return
     * @throws CheckmarxException
     */
    @Override
    public CxXMLResultsType getXmlReportContent(Integer reportId) throws CheckmarxException {
        HttpHeaders headers = authClient.createAuthHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity httpEntity = new HttpEntity<>(headers);
        String session = null;
        try {
            /* login to legacy SOAP CX Client to retrieve description */
            session = cxLegacyService.login(cxProperties.getUsername(), cxProperties.getPassword());
        } catch (CheckmarxLegacyException e) {
            log.error("Error occurring while logging into Legacy SOAP based WebService - issue description will remain blank");
        }
        log.info("Retrieving report contents of report Id {} in XML format", reportId);
        try {
            ResponseEntity<String> resultsXML = restTemplate.exchange(cxProperties.getUrl().concat(REPORT_DOWNLOAD), HttpMethod.GET, httpEntity, String.class, reportId);
            String xml = resultsXML.getBody();
            log.debug("Report length: {}", xml.length());
            log.debug("Headers: {}", resultsXML.getHeaders().toSingleValueMap().toString());
            log.info("Report downloaded for report Id {}", reportId);
            log.debug("XML String Output: {}", xml);
            log.debug("Base64:", Base64.getEncoder().encodeToString(resultsXML.toString().getBytes()));
            /*Remove any chars before the start xml tag*/
            xml = xml.trim().replaceFirst("^([\\W]+)<", "<");
            log.debug("Report length: {}", xml.length());
            String xml2 = ScanUtils.cleanStringUTF8_2(xml);
            log.trace("XML2: {}", xml2);
            InputStream xmlStream = new ByteArrayInputStream(Objects.requireNonNull(xml2.getBytes()));

            /* protect against XXE */
            JAXBContext jc = JAXBContext.newInstance(CxXMLResultsType.class);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            List<ScanResults.XIssue> xIssueList = new ArrayList<>();
            CxXMLResultsType cxResults;
            try {
                XMLStreamReader xsr = xif.createXMLStreamReader(xmlStream);
                Unmarshaller unmarshaller = jc.createUnmarshaller();
                return (CxXMLResultsType) unmarshaller.unmarshal(xsr);
            }catch (UnmarshalException e){
                log.warn("Issue occurred performing unmashall step - trying again {}", ExceptionUtils.getMessage(e));
                if(resultsXML.getBody() != null) {
                    log.error("Writing raw response from CX to {}", "CX_".concat(String.valueOf(reportId)));
                    ScanUtils.writeByte("CX_".concat(String.valueOf(reportId)), resultsXML.getBody().getBytes());
                    xml2 = ScanUtils.cleanStringUTF8(xml);
                    xmlStream = new ByteArrayInputStream(Objects.requireNonNull(xml2.getBytes()));
                    XMLStreamReader xsr = xif.createXMLStreamReader(xmlStream);
                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    return (CxXMLResultsType) unmarshaller.unmarshal(xsr);
                }
                else{
                    log.error("CX Response for report {} was null", reportId);
                    throw new CheckmarxException("CX report was empty (null)");
                }
            }
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Status Code of {} while getting downloading report contents of report Id {}", e.getStatusCode(), reportId);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results for report Id ".concat(reportId.toString()));
        } catch (XMLStreamException | JAXBException e) {
            log.error("Error with XML report");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results for report Id ".concat(reportId.toString()));
        } catch (NullPointerException e) {
            log.info("Null Error");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results for report Id ".concat(reportId.toString()));
        }
    }


    /**
     * Creates a map of additional scan details, such as scanId, scan start date, scan risk,
     * scan risk severity, number of failed LOC, etc.
     *
     * @param cxResults the source to use
     * @return  a map of additional scan details
     */
    protected Map<String, Object> getAdditionalScanDetails(CxXMLResultsType cxResults) {
        // Add additional data from the results
        Map<String, Object> additionalDetails = new HashMap<String, Object>();
        additionalDetails.put("scanId", cxResults.getScanId());
        additionalDetails.put("scanStartDate", cxResults.getScanStart());
        JSONObject jsonObject = getScanData(cxResults.getScanId());
        if (jsonObject != null) {
            additionalDetails.put("scanRisk", String.valueOf(jsonObject.getInt("scanRisk")));
            additionalDetails.put("scanRiskSeverity", String.valueOf(jsonObject.getInt("scanRiskSeverity")));
            JSONObject scanState = jsonObject.getJSONObject("scanState");
            if (scanState != null) {
                additionalDetails.put("numFailedLoc", String.valueOf(scanState.getInt("failedLinesOfCode")));
            }
        }
        // Add custom field values if requested
        Map<String, String> customFields = getCustomFields(Integer.valueOf(cxResults.getProjectId()));
        additionalDetails.put("customFields", customFields);
        return additionalDetails;
    }

    /**
     * Returns custom field values read from a Checkmarx project, based on given projectId.
     *
     * @param projectId ID of project to lookup from Checkmarx
     * @return Map of custom field names to values
     */
    public Map<String, String> getCustomFields(Integer projectId) {
        Map<String, String> customFields = new HashMap<String, String>();
        log.info("Fetching custom fields from project ID ".concat(projectId.toString()));
        CxProject cxProject = getProject(Integer.valueOf(projectId));
        if (cxProject != null) {
            for (CxProject.CustomField customField : cxProject.getCustomFields()) {
                customFields.put(customField.getName(), customField.getValue());
            }
        } else {
            log.error("Could not find project with ID ".concat(projectId.toString()));
        }
        return customFields;
    }

    /**
     * Parse CX report file, mapped to ScanResults DTO, applying filtering as requested
     *
     * @param file
     * @param filter
     * @return
     * @throws CheckmarxException
     */
    public ScanResults getReportContent(File file, List<Filter> filter) throws CheckmarxException {

        if (file == null) {
            throw new CheckmarxException("File not provided for processing of results");
        }
        String session = null;
        try {
            if (!cxProperties.getOffline()) {
                session = cxLegacyService.login(cxProperties.getUsername(), cxProperties.getPassword());
            }
        } catch (CheckmarxLegacyException e) {
            log.error("Error occurring while logging into Legacy SOAP based WebService - issue description will remain blank");
        }
        try {

            /* protect against XXE */
            JAXBContext jc = JAXBContext.newInstance(CxXMLResultsType.class);
            XMLInputFactory xif = XMLInputFactory.newInstance();
            xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            xif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            List<ScanResults.XIssue> issueList = new ArrayList<>();
            CxXMLResultsType cxResults = (CxXMLResultsType) unmarshaller.unmarshal(file);
            ScanResults.ScanResultsBuilder cxScanBuilder = ScanResults.builder();
            cxScanBuilder.projectId(cxResults.getProjectId());
            cxScanBuilder.team(cxResults.getTeam());
            cxScanBuilder.project(cxResults.getProjectName());
            cxScanBuilder.link(cxResults.getDeepLink());
            cxScanBuilder.files(cxResults.getFilesScanned());
            cxScanBuilder.loc(cxResults.getLinesOfCodeScanned());
            cxScanBuilder.scanType(cxResults.getScanType());
            getIssues(filter, session, issueList, cxResults);
            cxScanBuilder.xIssues(issueList);
            cxScanBuilder.additionalDetails(getAdditionalScanDetails(cxResults));
            if (!cxProperties.getOffline() && !ScanUtils.empty(cxResults.getScanId())) {
                CxScanSummary scanSummary = getScanSummary(Integer.valueOf(cxResults.getScanId()));
                cxScanBuilder.scanSummary(scanSummary);
            }
            return cxScanBuilder.build();

        } catch (JAXBException e) {
            log.error("Error with XML report");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results");
        } catch (NullPointerException e) {
            log.info("Null error");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results");
        }
    }

    /**
     * @param vulnsFile
     * @param libsFile
     * @param filter
     * @return
     * @throws CheckmarxException
     */
    public ScanResults getOsaReportContent(File vulnsFile, File libsFile, List<Filter> filter) throws CheckmarxException {
        if (vulnsFile == null || libsFile == null) {
            throw new CheckmarxException("Files not provided for processing of OSA results");
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<ScanResults.XIssue> issueList = new ArrayList<>();

            //convert json string to object
            List<CxOsa> osaVulns = objectMapper.readValue(vulnsFile, new TypeReference<List<CxOsa>>() {
            });
            List<CxOsaLib> osaLibs = objectMapper.readValue(libsFile, new TypeReference<List<CxOsaLib>>() {
            });
            Map<String, CxOsaLib> libsMap = getOsaLibsMap(osaLibs);
            Map<String, Integer> severityMap = ImmutableMap.of(
                    "LOW", 1,
                    "MEDIUM", 2,
                    "HIGH", 3
            );

            for (CxOsa o : osaVulns) {

                if (filterOsa(filter, o) && libsMap.containsKey(o.getLibraryId())) {
                    CxOsaLib lib = libsMap.get(o.getLibraryId());
                    String filename = lib.getName();

                    ScanResults.XIssue issue = ScanResults.XIssue.builder()
                            .file(filename)
                            .vulnerability(OSA_VULN)
                            .severity(o.getSeverity().getName())
                            .cve(o.getCveName())
                            .build();
                    ScanResults.OsaDetails details = ScanResults.OsaDetails.builder()
                            .severity(o.getSeverity().getName())
                            .cve(o.getCveName())
                            .description(o.getDescription())
                            .recommendation(o.getRecommendations())
                            .url(o.getUrl())
                            .version(lib.getVersion())
                            .build();
                    //update
                    if (issueList.contains(issue)) {
                        issue = issueList.get(issueList.indexOf(issue));
                        //bump up the severity if required
                        if (severityMap.get(issue.getSeverity().toUpperCase(Locale.ROOT)) < severityMap.get(o.getSeverity().getName().toUpperCase(Locale.ROOT))) {
                            issue.setSeverity(o.getSeverity().getName());
                        }
                        issue.setCve(issue.getCve().concat(",").concat(o.getCveName()));
                        issue.getOsaDetails().add(details);
                    } else {//new
                        List<ScanResults.OsaDetails> dList = new ArrayList<>();
                        dList.add(details);
                        issue.setOsaDetails(dList);
                        issueList.add(issue);
                    }
                }
            }

            return ScanResults.builder()
                    .osa(true)
                    .xIssues(issueList)
                    .build();

        } catch (IOException e) {
            log.error("Error parsing JSON OSA report");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results");
        } catch (NullPointerException e) {
            log.info("Null error");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error while processing scan results");
        }
    }

    @Override
    public String getIssueDescription(Long scanId, Long pathId) {
        return null;
    }

    private boolean filterOsa(List<Filter> filters, CxOsa osa) {
        boolean all = true;
        for (Filter f : filters) {
            if (f.getType().equals(Filter.Type.SEVERITY)) {
                all = false;  //if no SEVERITY filters, everything is applied
                if (f.getValue().equalsIgnoreCase(osa.getSeverity().getName())) {
                    return true;
                }
            }
        }
        return all;
    }

    private Map<String, CxOsaLib> getOsaLibsMap(List<CxOsaLib> libs) {
        Map<String, CxOsaLib> libMap = new HashMap<>();
        for (CxOsaLib o : libs) {
            libMap.put(o.getId(), o);
        }
        return libMap;
    }


    private List<CxOsa> getOSAVulnsByLibId(List<CxOsa> osaVulns, String libId) {
        List<CxOsa> vulns = new ArrayList<>();
        for (CxOsa v : osaVulns) {
            if (v.getLibraryId().equals(libId)) {
                vulns.add(v);
            }
        }
        return vulns;
    }


    /**
     * @param filter
     * @param session
     * @param cxIssueList
     * @param cxResults
     */
    private Map<String, Integer> getIssues(List<Filter> filter, String session, List<ScanResults.XIssue> cxIssueList, CxXMLResultsType cxResults) {
        Map<String, Integer> summary = new HashMap<>();
        for (QueryType q : cxResults.getQuery()) {
            if (checkFilter(q, filter)) {
                ScanResults.XIssue.XIssueBuilder xIssueBuilder = ScanResults.XIssue.builder();
                /*Top node of each issue*/
                for (ResultType r : q.getResult()) {
                    if (r.getFalsePositive().equalsIgnoreCase("FALSE") && checkFilter(r, filter)) {
                        /*Map issue details*/
                        xIssueBuilder.cwe(q.getCweId());
                        xIssueBuilder.language(q.getLanguage());
                        xIssueBuilder.severity(q.getSeverity());
                        xIssueBuilder.vulnerability(q.getName());
                        xIssueBuilder.file(r.getFileName());
                        xIssueBuilder.severity(r.getSeverity());
                        xIssueBuilder.link(r.getDeepLink());

                        // Add additional details
                        Map<String, Object> additionalDetails = getAdditionalIssueDetails(q, r);
                        xIssueBuilder.additionalDetails(additionalDetails);

                        Map<Integer, String> details = new HashMap<>();
                        try {
                            /* Call the CX SOAP Service to get Issue Description*/
                            if (session != null) {
                                try {
                                    xIssueBuilder.description(this.getIssueDescription(session, Long.parseLong(cxResults.getScanId()), Long.parseLong(r.getPath().getPathId())));
                                } catch (HttpStatusCodeException e) {
                                    xIssueBuilder.description("");
                                }
                            } else {
                                xIssueBuilder.description("");
                            }
                            details.put(Integer.parseInt(r.getPath().getPathNode().get(0).getLine()),
                                    r.getPath().getPathNode().get(0).getSnippet().getLine().getCode());
                            xIssueBuilder.similarityId(r.getPath().getSimilarityId());
                        } catch (NullPointerException e) {
                            log.warn("Problem grabbing snippet.  Snippet may not exist for finding for Node ID");
                            /*Defaulting to initial line number with no snippet*/
                            details.put(Integer.parseInt(r.getLine()), null);
                        }
                        xIssueBuilder.details(details);
                        ScanResults.XIssue issue = xIssueBuilder.build();
                        checkForDuplicateIssue(cxIssueList, r, details, issue, summary);
                    }
                }
            }
        }
        return summary;
    }

    private Map<String, Object> getAdditionalIssueDetails(QueryType q, ResultType r) {
        Map<String, Object> additionalDetails = new HashMap<String, Object>();
        additionalDetails.put("categories", q.getCategories());
        String descUrl = ScanUtils.getHostWithProtocol(r.getDeepLink()) +
                "/CxWebClient/ScanQueryDescription.aspx?queryID=" + q.getId() +
                "&queryVersionCode=" + q.getQueryVersionCode() +
                "&queryTitle=" + q.getName();
        additionalDetails.put("recommendedFix", descUrl);

        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        // Source / Sink data
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("state", r.getState());
        PathType path = r.getPath();
        if (path != null) {
            List<PathNodeType> nodes = path.getPathNode();
            if (!nodes.isEmpty()) {
                result.put("source", getNodeData(nodes, 0));
                result.put("sink", getNodeData(nodes, nodes.size() - 1)); // Last node in dataFlow
            } else {
                log.debug(String.format("Result %s%s did not have node paths to process.", q.getName(), r.getNodeId()));
            }
        }
        results.add(result);
        additionalDetails.put("results", results);
        return additionalDetails;
    }

    /**
     * Creates a {@link Map} of data values - file, line, column and object,
     * based on the node index in the given dataflow path.
     *
     * @param nodes List of nodes representing the data flow from source to sink
     * @param nodeIndex index of node to fetch data from
     * @return  Map of data values - specifically file, line, column and object.
     */
    private Map<String, String> getNodeData(List<PathNodeType> nodes, int nodeIndex) {
        // Node data: file/line/object
        Map<String, String> nodeData = new HashMap<>();
        PathNodeType node = nodes.get(nodeIndex);
        nodeData.put("file", node.getFileName());
        nodeData.put("line", node.getLine());
        nodeData.put("column", node.getColumn());
        nodeData.put("object", node.getName());
        return nodeData;
    }


    /**
     * Check if the highlevel Query resultset meets the filter criteria
     *
     * @param q
     * @param filters
     * @return
     */
    private boolean checkFilter(QueryType q, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        List<String> severity = new ArrayList<>();
        List<String> cwe = new ArrayList<>();
        List<String> category = new ArrayList<>();

        for(Filter f: filters){
            Filter.Type type = f.getType();
            String value = f.getValue();
            if(type.equals(Filter.Type.SEVERITY)){
                severity.add(value.toUpperCase(Locale.ROOT));
            }
            else if(type.equals(Filter.Type.TYPE)){
                category.add(value.toUpperCase(Locale.ROOT));
            }
            else if(type.equals(Filter.Type.CWE)){
                cwe.add(value.toUpperCase(Locale.ROOT));
            }
        }
        if (!severity.isEmpty() && !severity.contains(q.getSeverity().toUpperCase(Locale.ROOT))) {
            return false;
        }
        if (!cwe.isEmpty() && !cwe.contains(q.getCweId())) {
            return false;
        }

        return category.isEmpty() || category.contains(q.getName().toUpperCase(Locale.ROOT));
    }

    private boolean checkFilter(ResultType r, List<Filter> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        List<Integer> status = new ArrayList<>();

        for (Filter f : filters) {
            if (f.getType().equals(Filter.Type.STATUS)) {
                //handle New Status separately (this field is Status as opposed to State for the others
                if(f.getValue().equalsIgnoreCase("New")){
                    if(r.getStatus().equalsIgnoreCase("New")){
                        return true;
                    }
                }
                status.add(STATUS_MAP.get(f.getValue().toUpperCase(Locale.ROOT)));
            }
        }
        return status.isEmpty() || status.contains(Integer.parseInt(r.getState()));
    }

    private void checkForDuplicateIssue(List<ScanResults.XIssue> cxIssueList, ResultType r, Map<Integer, String> details, ScanResults.XIssue issue, Map<String, Integer> summary) {
        if (cxIssueList.contains(issue)) {
            /*Get existing issue of same vuln+filename*/
            ScanResults.XIssue existingIssue = cxIssueList.get(cxIssueList.indexOf(issue));
            /*If no reference exists for this particular line, append it to the details (line+snippet)*/
            if (!existingIssue.getDetails().containsKey(Integer.parseInt(r.getLine()))) {
                try {
                    existingIssue.getDetails().put(Integer.parseInt(r.getPath().getPathNode().get(0).getLine()),
                            r.getPath().getPathNode().get(0).getSnippet().getLine().getCode());
                } catch (NullPointerException e) {
                    details.put(Integer.parseInt(r.getLine()), null);
                }finally {
                    if(!summary.containsKey(r.getSeverity())){
                        summary.put(r.getSeverity(), 0);
                    }
                    int x = summary.get(r.getSeverity());
                    x++;
                    summary.put(r.getSeverity(), x);
                }
            }
            // Copy additionalData.results from issue to existingIssue
            List<Map<String, Object>> results = (List<Map<String, Object>>) existingIssue.getAdditionalDetails().get("results");
            results.addAll((List<Map<String, Object>>)issue.getAdditionalDetails().get("results"));

        } else {
            cxIssueList.add(issue);
            if(!summary.containsKey(r.getSeverity())){
                summary.put(r.getSeverity(), 0);
            }
            int x = summary.get(r.getSeverity());
            x++;
            summary.put(r.getSeverity(), x);
        }
    }

    private String getIssueDescription(String session, Long scanId, Long pathId) {
        return cxLegacyService.getDescription(session, scanId, pathId);
    }

    /**
     * Creates a CX Project.
     * <p>
     * Naming convention is namespace-repo-branch
     */
    public Integer createProject(String ownerId, String name) {
        CxCreateProject project = CxCreateProject.builder()
                .name(name)
                .owningTeam(ownerId)
                .isPublic(true)
                .build();
        HttpEntity<CxCreateProject> requestEntity = new HttpEntity<>(project, authClient.createAuthHeaders());

        log.info("Creating Project {} for ownerId {}", name, ownerId);
        try {
            String response = restTemplate.postForObject(cxProperties.getUrl().concat(PROJECTS), requestEntity, String.class);
            JSONObject obj = new JSONObject(response);
            String id = obj.get("id").toString();
            return Integer.parseInt(id);
        } catch (HttpStatusCodeException e) {
            log.error("HTTP error code {} while creating project with name {} under owner id {}", e.getStatusCode(), name, ownerId);
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    @Override
    public void deleteProject(Integer projectId) {
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());

        log.info("Deleting Project id {}", projectId);
        try {
            restTemplate.exchange(cxProperties.getUrl().concat(PROJECT), HttpMethod.DELETE, requestEntity, String.class, projectId);
        } catch (HttpStatusCodeException e) {
            log.error("HTTP error code {} while deleting project with id {}", e.getStatusCode(), projectId);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Get All Projects in Checkmarx
     *
     * @return
     */
    public List<CxProject> getProjects() throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            ResponseEntity<CxProject[]> projects = restTemplate.exchange(cxProperties.getUrl().concat(PROJECTS), HttpMethod.GET, httpEntity, CxProject[].class);
            if(projects.getBody() != null){
                return Arrays.asList(projects.getBody());
            }
            return Collections.emptyList();
        } catch (HttpStatusCodeException e) {
            log.warn("Error occurred while retrieving projects, http error {}", e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error retrieving Projects");
        }
    }

    /**
     * Get All Projects in Checkmarx
     *
     * @return
     */
    public List<CxProject> getProjects(String teamId) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        List<CxProject> teamProjects = new ArrayList<>();
        try {
            ResponseEntity<CxProject[]> projects = restTemplate.exchange(cxProperties.getUrl().concat(PROJECTS), HttpMethod.GET, httpEntity, CxProject[].class);

            if (projects.getBody() != null) {
                for (CxProject p : projects.getBody()) {
                    if (p.getTeamId().equals(teamId)) {
                        teamProjects.add(p);
                    }
                }
            }
            return teamProjects;
        } catch (HttpStatusCodeException e) {
            log.warn("Error occurred while retrieving projects, http error {}", e.getStatusCode());
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error retrieving Projects");
        }
    }

    public Integer getProjectId(String ownerId, String name) {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            ResponseEntity<String> projects = restTemplate.exchange(cxProperties.getUrl().concat(PROJECTS)
                    .concat("?projectName=").concat(name).concat("&teamId=").concat(ownerId), HttpMethod.GET, httpEntity, String.class);
            JSONArray arr = new JSONArray(projects.getBody());
            if (arr.length() > 1) {
                return UNKNOWN_INT;
            }
            JSONObject obj = arr.getJSONObject(0);
            return obj.getInt("id");
        } catch (HttpStatusCodeException e) {
            if (!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.warn("Error occurred while retrieving project with name {}, http error {}", name, e.getStatusCode());
                log.error(ExceptionUtils.getStackTrace(e));
            }
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    /**
     * Return Project based on projectId
     *
     * @return
     */
    public CxProject getProject(Integer projectId) {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            ResponseEntity<CxProject> project = restTemplate.exchange(cxProperties.getUrl().concat(PROJECT), HttpMethod.GET, httpEntity, CxProject.class, projectId);
            return project.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving project with id {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return null;
    }


    /**
     * Check if a scan exists for a projectId
     *
     * @param projectId
     * @return
     */
    public boolean scanExists(Integer projectId) {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {

            ResponseEntity<String> scans = restTemplate.exchange(cxProperties.getUrl().concat(PROJECT_SCANS), HttpMethod.GET, httpEntity, String.class, projectId);
            JSONArray jsonArray = new JSONArray(scans.getBody());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject scan = jsonArray.getJSONObject(i);
                JSONObject status = scan.getJSONObject("status");
                int statusId = status.getInt("id");
                if (SCAN_STATUS_QUEUED.equals(statusId) || SCAN_STATUS_NEW.equals(statusId) || SCAN_STATUS_SCANNING.equals(statusId) ||
                        SCAN_STATUS_PRESCAN.equals(statusId) || SCAN_STATUS_SOURCE_PULLING.equals(statusId)) {
                    log.debug("Scan status is {}", statusId);
                    return true;
                }
            }
            log.debug("No scans in the queue that are in progress");
            return false;

        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving project with id {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    /**
     * Create Scan Settings
     *
     * @param projectId
     * @param presetId
     * @param engineConfigId
     * @return
     */
    public Integer createScanSetting(Integer projectId, Integer presetId, Integer engineConfigId) {
        CxScanSettings scanSettings = CxScanSettings.builder()
                .projectId(projectId)
                .engineConfigurationId(engineConfigId)
                .presetId(presetId)
                .build();
        HttpEntity<CxScanSettings> requestEntity = new HttpEntity<>(scanSettings, authClient.createAuthHeaders());

        log.info("Creating ScanSettings for project Id {}", projectId);
        try {
            String response = restTemplate.postForObject(cxProperties.getUrl().concat(SCAN_SETTINGS), requestEntity, String.class);
            JSONObject obj = new JSONObject(response);
            String id = obj.get("id").toString();
            return Integer.parseInt(id);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating ScanSettings for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        } catch (JSONException e) {
            log.error("Error processing JSON Response");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    /**
     * Set Repository details for a project
     *
     * @param projectId
     * @param gitUrl
     * @param branch
     * @throws CheckmarxException
     */
    public void setProjectRepositoryDetails(Integer projectId, String gitUrl, String branch) throws CheckmarxException {
        CxProjectSource projectSource = CxProjectSource.builder()
                .url(gitUrl)
                .branch(branch)
                .build();
        log.debug("branch {}", branch);
        log.debug("project {}", projectId);
        HttpEntity<CxProjectSource> requestEntity = new HttpEntity<>(projectSource, authClient.createAuthHeaders());

        try {
            log.info("Updating Source details for project Id {}", projectId);
            restTemplate.exchange(cxProperties.getUrl().concat(PROJECT_SOURCE), HttpMethod.POST, requestEntity, String.class, projectId);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while updating Project source info for project {}.", projectId);
            throw new CheckmarxException("Error occurred while adding source details to project.  Please ensure GIT is defined within Checkmarx");
        }
    }

    /**
     * Upload file (zip of source) for a project
     *
     * @param projectId
     * @param file
     * @throws CheckmarxException
     */
    public void uploadProjectSource(Integer projectId, File file) throws CheckmarxException {
        HttpHeaders headers = authClient.createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        FileSystemResource value = new FileSystemResource(file);
        map.add("zippedSource", value);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        try {
            log.info("Updating Source details for project Id {}", projectId);
            restTemplate.exchange(cxProperties.getUrl().concat(PROJECT_SOURCE_FILE), HttpMethod.POST, requestEntity, String.class, projectId);
        } catch (HttpStatusCodeException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            log.error("Error occurred while uploading Project source for project id {}.", projectId);
            throw new CheckmarxException("Error occurred while uploading source");
        }
    }

    public void setProjectExcludeDetails(Integer projectId, List<String> excludeFolders, List<String> excludeFiles) {
        String excludeFilesStr = "";
        String excludeFolderStr = "";

        if (excludeFiles != null && !excludeFiles.isEmpty()) {
            excludeFilesStr = String.join(",", excludeFiles);
        }
        if (excludeFolders != null && !excludeFolders.isEmpty()) {
            excludeFolderStr = String.join(",", excludeFolders);
        }

        String strJSON = "{'excludeFoldersPattern':'%s', 'excludeFilesPattern':'%s'}";
        strJSON = String.format(strJSON, excludeFolderStr, excludeFilesStr);
        HttpEntity requestEntity = new HttpEntity<>(strJSON, authClient.createAuthHeaders());

        try {
            log.info("Updating Project folder and file exclusion details for project Id {}", projectId);
            restTemplate.exchange(cxProperties.getUrl().concat(PROJECT_EXCLUDE), HttpMethod.PUT, requestEntity, String.class, projectId);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while updating Project source info for project {}.", projectId);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Get teamId for given path
     *
     * @param teamPath
     * @return
     * @throws CheckmarxException
     */
    public String getTeamId(String teamPath) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.info("Retrieving Cx teams");
            ResponseEntity<CxTeam[]> response = restTemplate.exchange(cxProperties.getUrl().concat(TEAMS), HttpMethod.GET, httpEntity, CxTeam[].class);
            CxTeam[] teams = response.getBody();
            if (teams == null) {
                throw new CheckmarxException("Error obtaining Team Id");
            }
            for (CxTeam team : teams) {
                if (team.getFullName().equals(teamPath)) {
                    log.info("Found team {} with ID {}", teamPath, team.getId());
                    return team.getId();
                }
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving Teams");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        log.info("No team was found for {}", teamPath);
        return UNKNOWN;
    }


    /**
     * Get a team Id based on the name and the Parent Team Id
     * @param parentTeamId
     * @param teamName
     * @return
     * @throws CheckmarxException
     */
    @Override
    public String getTeamId(String parentTeamId, String teamName) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            if(cxProperties.getVersion() < 9.0){
                log.error("Unsupported function for this version of Checkmarx");
                return null;
            }
            log.info("Retrieving Cx teams");
            ResponseEntity<CxTeam[]> response = restTemplate.exchange(cxProperties.getUrl().concat(TEAMS), HttpMethod.GET, httpEntity, CxTeam[].class);
            CxTeam[] teams = response.getBody();
            if (teams == null) {
                throw new CheckmarxException("Error obtaining Team Id");
            }
            for (CxTeam team : teams) {
                if (team.getName().equals(teamName) && team.getParentId().equals(parentTeamId)) {
                    log.info("Found team {} with ID {}", teamName, team.getId());
                    return team.getId();
                }
            }
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving Teams");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        log.info("No team was found for {} with parentId {}", teamName, parentTeamId);
        return UNKNOWN;
    }


    @Override
    public String createTeam(String parentTeamId, String teamName) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0){
            return createTeamWS(parentTeamId, teamName);
        }
        else {
            JSONObject json = new JSONObject();
            json.put("name", teamName);
            json.put("parentId", Long.parseLong(parentTeamId));
            log.info("Creating team with name {} under parent Id {}", teamName, parentTeamId);
            try {
                HttpEntity requestEntity = new HttpEntity<>(json.toString(), authClient.createAuthHeaders());
                restTemplate.postForObject(cxProperties.getUrl().concat(TEAMS), requestEntity, String.class);
                return getTeamId(parentTeamId, teamName);
            } catch (HttpStatusCodeException e) {
                log.error("Error occurred while creating team and retrieving new Id");
                log.error(ExceptionUtils.getStackTrace(e));
            }
            return UNKNOWN;
        }
    }

    @Override
    public void deleteTeam(String teamId) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0){
            deleteTeamWS(teamId);
        }
        else {
            HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
            log.debug("Deleting team with id {}", teamId);
            try {
                ResponseEntity<String> projects = restTemplate.exchange(cxProperties.getUrl().concat(TEAM), HttpMethod.DELETE, httpEntity, String.class, teamId);
            } catch (HttpStatusCodeException e) {
                log.error("HTTP Status Code of {} while deleting team Id {}", e.getStatusCode(), teamId);
                log.error(ExceptionUtils.getStackTrace(e));
                throw new CheckmarxException("Error occurred deleting team with id ".concat(teamId));
            }
        }
    }

    /**
     * Create team under given parentId
     *
     * @param parentTeamId
     * @param teamName
     * @return
     * @throws CheckmarxException
     */
    public String createTeamWS(String parentTeamId, String teamName) throws CheckmarxException {
        String session = authClient.getLegacySession();

        if(authClient.getLegacySession() == null){
            session = authClient.legacyLogin(cxProperties.getUsername(), cxProperties.getPassword());
        }
        cxLegacyService.createTeam(session, parentTeamId, teamName);
        return getTeamId(cxProperties.getTeam().concat(cxProperties.getTeamPathSeparator()).concat(teamName));
    }


    /**
     * Delete team based on Id
      * @param teamId
     * @throws CheckmarxException
     */
    public void deleteTeamWS(String teamId) throws CheckmarxException {
        String session = authClient.getLegacySession();
        if(session == null){
            session = authClient.legacyLogin(cxProperties.getUsername(), cxProperties.getPassword());
        }
        cxLegacyService.deleteTeam(authClient.getLegacySession(), teamId);
    }

    /**
     * Get scan configuration Id
     *
     * @param configuration
     * @return
     * @throws CheckmarxException
     */
    public Integer getScanConfiguration(String configuration) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        int defaultConfigId = UNKNOWN_INT;
        try {
            log.info("Retrieving Cx engineConfigurations");
            ResponseEntity<CxScanEngine[]> response = restTemplate.exchange(cxProperties.getUrl().concat(SCAN_CONFIGURATIONS), HttpMethod.GET, httpEntity, CxScanEngine[].class);
            CxScanEngine[] engines = response.getBody();
            if (engines == null) {
                throw new CheckmarxException("Error obtaining Scan configurations");
            }
            for(CxScanEngine engine: engines){
                String engineName = engine.getName();
                int engineId = engine.getId();
                if(engineName.equalsIgnoreCase(configuration)){
                    log.info("Found xml/engine configuration {} with ID {}", configuration, engineId);
                    return engineId;
                }
            }
            log.warn("No scan configuration found for {}", configuration);
            log.warn("Scan Configuration {} with ID {} will be used instead", Constants.CX_DEFAULT_CONFIGURATION, defaultConfigId);
            return defaultConfigId;
        }   catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving engine configurations");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error obtaining Configuration Id");
        }
    }

    public Integer getPresetId(String preset) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        int defaultPresetId = UNKNOWN_INT;
        try {
            log.info("Retrieving Cx presets");
            ResponseEntity<CxPreset[]> response = restTemplate.exchange(cxProperties.getUrl().concat(PRESETS), HttpMethod.GET, httpEntity, CxPreset[].class);
            CxPreset[] cxPresets = response.getBody();
            if (cxPresets == null) {
                throw new CheckmarxException("Error obtaining Team Id");
            }
            for(CxPreset cxPreset: cxPresets){
                String presetName = cxPreset.getName();
                int presetId = cxPreset.getId();
                if(presetName.equalsIgnoreCase(preset)){
                    log.info("Found preset {} with ID {}", preset, presetId);
                    return cxPreset.getId();
                }
                if(presetName.equalsIgnoreCase(Constants.CX_DEFAULT_PRESET)){
                    defaultPresetId =  presetId;
                }
            }
            log.warn("No Preset was found for {}", preset);
            log.warn("Default Preset {} with ID {} will be used instead", Constants.CX_DEFAULT_PRESET, defaultPresetId);
            return defaultPresetId;
        }   catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving presets");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error obtaining Preset Id");
        }
    }

    /**
     * Get scan summary for given scanId
     *
     * @param scanId
     * @return
     * @throws CheckmarxException
     */
    public CxScanSummary getScanSummaryByScanId(Integer scanId) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.debug("Retrieving scan summary for scan id: {}", scanId);
            ResponseEntity<CxScanSummary> response = restTemplate.exchange(cxProperties.getUrl().concat(SCAN_SUMMARY), HttpMethod.GET, httpEntity, CxScanSummary.class, scanId);
            CxScanSummary scanSummary = response.getBody();
            if (scanSummary == null) {
                log.warn("No scan summary was available for scan id: {}", scanId);
            }
            return scanSummary;
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving scan summary for scan id: {}", scanId);
            log.error(ExceptionUtils.getStackTrace(e));
        }
        log.warn("No scan summary was available for scan id: {}", scanId);
        return null;
    }

    /**
     * Get the scan summary for the latest scan of a given project Id
     *
     * @param projectId project Id to retrieve the latest scan summary for
     * @return
     * @throws CheckmarxException
     */
    @Override
    public CxScanSummary getScanSummary(Integer projectId) throws CheckmarxException {
        Integer scanId = getLastScanId(projectId);
        return getScanSummaryByScanId(scanId);
    }

    /**
     * Get the scan summary for the latest scan of a given team and project name
     *
     * @param teamName
     * @param projectName
     * @return
     * @throws CheckmarxException
     */
    @Override
    public CxScanSummary getScanSummary(String teamName, String projectName) throws CheckmarxException {
        String teamId = getTeamId(teamName);
        Integer projectId = getProjectId(teamId, projectName);
        Integer scanId = getLastScanId(projectId);
        return getScanSummaryByScanId(scanId);
    }

    @Override
    public Integer createScan(CxScanParams params, String comment) throws CheckmarxException{
        log.info("Creating scan...");
        validateScanParams(params);
        String teamId = getTeamId(params.getTeamName());
        Integer projectId = getProjectId(teamId, params.getProjectName());
        if(projectId.equals(UNKNOWN_INT)){
            projectId = createProject(teamId, params.getProjectName());
        }

        Integer presetId = getPresetId(params.getScanPreset());
        Integer engineId = getScanConfiguration(params.getScanConfiguration());
        createScanSetting(projectId, presetId, engineId);

        switch (params.getSourceType()){
            case GIT:
                setProjectRepositoryDetails(projectId, params.getGitUrl(), params.getBranch());
                break;
            case FILE:
                uploadProjectSource(projectId, new File(params.getFilePath()));
                break;
        }
        if(params.isIncremental() && !projectId.equals(UNKNOWN_INT)) {
            LocalDateTime scanDate = getLastScanDate(projectId);
            if(scanDate == null || LocalDateTime.now().isAfter(scanDate.plusDays(cxProperties.getIncrementalThreshold()))){
                log.debug("Last scanDate: {}", scanDate);
                log.info("Last scanDate does not meet the threshold for an incremental scan.");
                params.setIncremental(false);
            }
            else{
                log.info("Scan will be incremental");
            }
        }
        else {
            log.info("Scan will be Full Scan");
            params.setIncremental(false);
        }

        setProjectExcludeDetails(projectId, params.getFolderExclude(), params.getFileExclude());
        CxScan scan = CxScan.builder()
                .projectId(projectId)
                .isIncremental(params.isIncremental())
                .forceScan(params.isForceScan())
                .isPublic(params.isPublic())
                .comment(comment)
                .build();

        HttpHeaders headers = authClient.createAuthHeaders();
        headers.add("cxOrigin","CxFlow");
        HttpEntity<CxScan> requestEntity = new HttpEntity<>(scan, headers);

        log.info("Creating Scan for project Id {}", projectId);
        try {
            String response = restTemplate.postForObject(cxProperties.getUrl().concat(SCAN), requestEntity, String.class);
            JSONObject obj = new JSONObject(response);
            String id = obj.get("id").toString();
            log.info("Scan created with Id {} for project Id {}", id, projectId);
            return Integer.parseInt(id);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating Scan for project {}, http error {}", projectId, e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        log.info("...Finished creating scan");
        return UNKNOWN_INT;
    }

    /**
     *
     * @param params attributes used to define the project
     * @param comment
     * @return
     * @throws CheckmarxException
     */
    @Override
    public CxXMLResultsType createScanAndReport(CxScanParams params, String comment) throws CheckmarxException{
        Integer scanId = createScan(params, comment);
        waitForScanCompletion(scanId);

        try {
            Integer reportId = createScanReport(scanId);
            Thread.sleep(cxProperties.getReportPolling());
            int timer = 0;
            while (getReportStatus(reportId).equals(CxService.REPORT_STATUS_FINISHED)) {
                Thread.sleep(cxProperties.getReportPolling());
                timer += cxProperties.getReportPolling();
                if (timer >= cxProperties.getReportTimeout()) {
                    log.error("Report Generation timeout.  {}", cxProperties.getReportTimeout());
                    throw new CheckmarxException("Timeout exceeded during report generation");
                }
            }
            Thread.sleep(1000);
            return getXmlReportContent(reportId);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
            throw new CheckmarxException("Interrupted Exception Occurred");
        }
    }

    /**
     *
     * @param params attributes used to define the project
     * @param comment
     * @param filters filters to apply to the scan result set (severity, category, cwe)
     * @return
     * @throws CheckmarxException
     */
    @Override
    public ScanResults createScanAndReport(CxScanParams params, String comment, List<Filter> filters) throws CheckmarxException{
        Integer scanId = createScan(params, comment);
        waitForScanCompletion(scanId);

        try {
            Integer reportId = createScanReport(scanId);
            Thread.sleep(cxProperties.getScanPolling());
            int timer = 0;
            while (getReportStatus(reportId).equals(CxService.REPORT_STATUS_FINISHED)) {
                Thread.sleep(cxProperties.getScanPolling());
                timer += cxProperties.getScanPolling();
                if (timer >= cxProperties.getReportTimeout()) {
                    log.error("Report Generation timeout.  {}", cxProperties.getReportTimeout());
                    throw new CheckmarxException("Timeout exceeded during report generation");
                }
            }
            Thread.sleep(cxProperties.getScanPolling());
            return getReportContent(reportId, filters);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
            throw new CheckmarxException("Interrupted Exception Occurred");
        }
    }

    /**
     *
     * @param scanId
     * @return
     * @throws CheckmarxException
     */
    @Override
    public void deleteScan(Integer scanId) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        log.debug("Deleting scan with id {}", scanId);
        try {
            ResponseEntity<String> projects = restTemplate.exchange(cxProperties.getUrl().concat(SCAN_STATUS), HttpMethod.DELETE, httpEntity, String.class, scanId);
        } catch (HttpStatusCodeException e) {
            log.error("HTTP Status Code of {} while deleting scan Id {}", e.getStatusCode(), scanId);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     *
     * @param teamName
     * @param projectName
     * @return
     * @throws CheckmarxException
     */
    @Override
    public CxXMLResultsType getLatestScanReport(String teamName, String projectName) throws CheckmarxException {
        String teamId = getTeamId(teamName);
        Integer projectId = getProjectId(teamId, projectName);
        Integer scanId = getLastScanId(projectId);
        try {
            Integer reportId = createScanReport(scanId);
            Thread.sleep(cxProperties.getScanPolling());
            int timer = 0;
            while (getReportStatus(reportId).equals(CxService.REPORT_STATUS_FINISHED)) {
                Thread.sleep(cxProperties.getScanPolling());
                timer += cxProperties.getScanPolling();
                if (timer >= cxProperties.getReportTimeout()) {
                    log.error("Report Generation timeout.  {}", cxProperties.getReportTimeout());
                    throw new CheckmarxException("Timeout exceeded during report generation");
                }
            }
            Thread.sleep(cxProperties.getScanPolling());
            return getXmlReportContent(reportId);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
            throw new CheckmarxException("Interrupted Exception Occurred");
        }
    }

    /**
     * Get the latest scan results by teamName and projectName with filtered results
     *
     * @param teamName
     * @param projectName
     * @param filters
     * @return
     * @throws CheckmarxException
     */
    @Override
    public ScanResults getLatestScanResults(String teamName, String projectName, List<Filter> filters) throws CheckmarxException {
        String teamId = getTeamId(teamName);
        Integer projectId = getProjectId(teamId, projectName);
        Integer scanId = getLastScanId(projectId);
        try {
            Integer reportId = createScanReport(scanId);
            Thread.sleep(cxProperties.getScanPolling());
            int timer = 0;
            while (getReportStatus(reportId).equals(CxService.REPORT_STATUS_FINISHED)) {
                Thread.sleep(cxProperties.getScanPolling());
                timer += cxProperties.getScanPolling();
                if (timer >= cxProperties.getReportTimeout()) {
                    log.error("Report Generation timeout.  {}", cxProperties.getReportTimeout());
                    throw new CheckmarxException("Timeout exceeded during report generation");
                }
            }
            Thread.sleep(cxProperties.getScanPolling());
            return getReportContent(reportId, filters);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            Thread.currentThread().interrupt();
            throw new CheckmarxException("Interrupted Exception Occurred");
        }
    }

    @Override
    public List<CxTeam> getTeams() throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.info("Retrieving Cx teams");
            ResponseEntity<CxTeam[]> response = restTemplate.exchange(cxProperties.getUrl().concat(TEAMS), HttpMethod.GET, httpEntity, CxTeam[].class);
            CxTeam[] teams = response.getBody();
            if (teams == null) {
                throw new CheckmarxException("Error retrieving teams");
            }
            return Arrays.asList(teams);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving Teams");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error occurred while retrieving teams");
        }
    }

    /**
     *
     * @param ldapServerId
     * @param teamId
     * @param teamName not used in 9.0+
     * @param ldapGroupDn
     * @throws CheckmarxException
     */
    @Override
    public void mapTeamLdap(Integer ldapServerId, String teamId, String teamName, String ldapGroupDn) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0){
            log.debug("Calling legacy mapTeamLdapWS");
            mapTeamLdapWS(ldapServerId, teamId, teamName, ldapGroupDn);
        }
        else{
            log.debug("Calling Access Control REST method for Team LDAP Mapping");
            try {
                String name = getNameFromLDAP(ldapGroupDn);
                JSONObject body = new JSONObject();
                body.put("teamId", teamId);
                body.put("ldapGroupDn", ldapGroupDn);
                body.put("ldapGroupDisplayName", name);

                HttpEntity<String> requestEntity = new HttpEntity<>(body.toString(), authClient.createAuthHeaders());
                restTemplate.exchange(cxProperties.getUrl().concat(TEAM_LDAP),  HttpMethod.POST, requestEntity, String.class, ldapServerId);
            }catch (HttpStatusCodeException e) {
                log.error("Error occurred while creating Team Ldap mapping: {}", ExceptionUtils.getMessage(e));
            }
        }
    }

    /**
     *
     * @param ldapServerId
     * @return
     * @throws CheckmarxException
     */
    @Override
    public List<CxTeamLdap> getTeamLdap(Integer ldapServerId) throws CheckmarxException {
        HttpEntity httpEntity = new HttpEntity<>(createAuthHeaders());
        try {
            ResponseEntity<CxTeamLdap[]> projects = restTemplate.exchange(cxProperties.getUrl().concat(PROJECTS),
                    HttpMethod.GET, httpEntity, CxTeamLdap[].class);
            if(projects.getBody() != null){
                return Arrays.asList(projects.getBody());
            }
            return Collections.emptyList();
        } catch (HttpStatusCodeException e) {
            log.warn("Error occurred while retrieving Team LDAP Mappings, http error {}", e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error retrieving Team LDAP Mappings");
        }

    }

    static String getNameFromLDAP(String ldapGroupDn) {
        try {
            LdapName ldapName = new LdapName(ldapGroupDn);
            List<Rdn> rdns = ldapName.getRdns();
            Rdn r = rdns.get(rdns.size() - 1);
            String cn = r.getValue().toString();
            cn = cn.replace("CN=", "");
            cn = cn.replace("cn=", "");
            return cn;
        }catch(InvalidNameException e){
            log.warn("Could not determine name from CN, defaulting to full CN {}", ldapGroupDn);
            return ldapGroupDn;
        }
    }

    @Override
    public void removeTeamLdap(Integer ldapServerId, String teamId, String teamName, String ldapGroupDn) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0){
            log.debug("Calling legacy removeTeamLdapWS");
            removeTeamLdapWS(ldapServerId, teamId, teamName, ldapGroupDn);
        }
        else{
            Integer mapId = getLdapTeamMapId(ldapServerId, ldapGroupDn);
            HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());

            log.info("Deleting ldap team mapping id {}", mapId);
            try {
                restTemplate.exchange(cxProperties.getUrl().concat(TEAM_LDAP_MAPPING), HttpMethod.DELETE, requestEntity, String.class, mapId);
            } catch (HttpStatusCodeException e) {
                log.error("HTTP error code {} while deleting project with id {}", e.getStatusCode(), mapId);
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    @Override
    public Integer getLdapTeamMapId(Integer ldapServerId, String ldapGroupDn) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        try{
            HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());
            ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(TEAM_LDAP_MAPPINGS),
                    HttpMethod.GET, requestEntity, String.class, ldapServerId);
            JSONArray objs = new JSONArray(response.getBody());
            for(int i=0; i < objs.length(); i++){
                JSONObject obj = objs.getJSONObject(i);
                String cn = obj.getString("ldapGroupDn");
                if(cn.equals(ldapGroupDn)){
                    return obj.getInt("id");
                }
            }
            log.info("No mapping found for {} with Server id {}", ldapGroupDn, ldapServerId);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving ldap server mappings, http error {}", e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    @Override
    public void mapTeamLdapWS(Integer ldapServerId, String teamId, String teamName, String ldapGroupDn) throws CheckmarxException {
        String session = authClient.getLegacySession();
        if(session == null){
            session = authClient.legacyLogin(cxProperties.getUsername(), cxProperties.getPassword());
        }
        cxLegacyService.createLdapTeamMapping(session, ldapServerId, teamId, teamName, ldapGroupDn);
    }

    @Override
    public void removeTeamLdapWS(Integer ldapServerId, String teamId, String teamName, String ldapGroupDn) throws CheckmarxException {
        String session = authClient.getLegacySession();
        if(session == null){
            session = authClient.legacyLogin(cxProperties.getUsername(), cxProperties.getPassword());
        }
        cxLegacyService.removeLdapTeamMapping(session, ldapServerId, teamId, teamName, ldapGroupDn);
    }

    @Override
    public List<CxRole> getRoles() throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        HttpEntity httpEntity = new HttpEntity<>(authClient.createAuthHeaders());
        try {
            log.info("Retrieving Cx Roles");
            ResponseEntity<CxRole[]> response = restTemplate.exchange(cxProperties.getUrl().concat(ROLE), HttpMethod.GET, httpEntity, CxRole[].class);
            CxRole[] roles = response.getBody();
            if (roles == null) {
                throw new CheckmarxException("Error retrieving roles");
            }
            return Arrays.asList(roles);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving Roles");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException("Error occurred while retrieving teams");
        }
    }

    @Override
    public Integer getRoleId(String roleName) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        List<CxRole> roles = getRoles();
        for(CxRole role: roles){
            if(role.getName().equalsIgnoreCase(roleName)){
                log.debug("role found with id {}", role.getId());
                return role.getId();
            }
        }
        return UNKNOWN_INT;
    }

    @Override
    public void mapRoleLdap(Integer ldapServerId, Integer roleId, String ldapGroupDn) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        try {
            log.info("Creating LDAP role mapping for role id {} with LDAP DN {}", roleId, ldapGroupDn);
            String name = getNameFromLDAP(ldapGroupDn);
            JSONObject body = new JSONObject();
            body.put("roleId", roleId);
            body.put("ldapGroupDn", ldapGroupDn);
            body.put("ldapGroupDisplayName", name);
            log.debug(body.toString());
            HttpEntity<String> requestEntity = new HttpEntity<>(body.toString(), authClient.createAuthHeaders());
            restTemplate.exchange(cxProperties.getUrl().concat(ROLE_MAPPING),  HttpMethod.PUT, requestEntity, String.class, ldapServerId);
        }catch (HttpStatusCodeException e) {
            log.error("Error occurred while creating Team Ldap mapping: {}", ExceptionUtils.getMessage(e));
        }
    }

    @Override
    public void getRoleLdap(Integer ldapServerId) throws CheckmarxException {

    }

    @Override
    public void removeRoleLdap(Integer roleMapId) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());

        log.info("Deleting ldap role mapping id {}", roleMapId);
        try {
            restTemplate.exchange(cxProperties.getUrl().concat(ROLE_MAPPING), HttpMethod.DELETE, requestEntity, String.class, roleMapId);
        } catch (HttpStatusCodeException e) {
            log.error("HTTP error code {} while deleting role mapping with id {}", e.getStatusCode(), roleMapId);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public Integer getLdapRoleMapId(Integer ldapServerId, String ldapGroupDn) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            throw new CheckmarxException("Operation only support in 9.0+");
        }
        try{
            HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());
            ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(ROLE_MAPPINGS),
                    HttpMethod.GET, requestEntity, String.class, ldapServerId);
            JSONArray objs = new JSONArray(response.getBody());
            for(int i=0; i < objs.length(); i++){
                JSONObject obj = objs.getJSONObject(i);
                String cn = obj.getString("ldapGroupDn");
                if(cn.equals(ldapGroupDn)){
                    return obj.getInt("id");
                }
            }
            log.info("No mapping found for {} with Server id {}", ldapGroupDn, ldapServerId);
        } catch (HttpStatusCodeException e) {
            log.error("Error occurred while retrieving ldap server mappings, http error {}", e.getStatusCode());
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return UNKNOWN_INT;
    }

    @Override
    public Integer getLdapServerId(String serverName) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0) {
            String session = authClient.getLegacySession();
            if (session == null) {
                session = authClient.legacyLogin(cxProperties.getUsername(), cxProperties.getPassword());
            }
            return cxLegacyService.getLdapServerId(session, serverName);
        }
        else{
            try{
                HttpEntity requestEntity = new HttpEntity<>(authClient.createAuthHeaders());
                ResponseEntity<String> response = restTemplate.exchange(cxProperties.getUrl().concat(LDAP_SERVER),
                        HttpMethod.GET, requestEntity, String.class);
                JSONArray objs = new JSONArray(response.getBody());
                for(int i=0; i < objs.length(); i++){
                    JSONObject obj = objs.getJSONObject(i);
                    String name = obj.getString("name");
                    if(name.equals(serverName)){
                        return obj.getInt("id");
                    }
                }
                log.info("No LDAP Server found for name {}", serverName);
            } catch (HttpStatusCodeException e) {
                log.error("Error occurred while retrieving ldap servers, http error {}", e.getStatusCode());
                log.error(ExceptionUtils.getStackTrace(e));
            }
            return UNKNOWN_INT;
        }
    }
    
    private void validateScanParams(CxScanParams params) throws CheckmarxException {
        log.debug(params.toString());
        if(ScanUtils.empty(params.getProjectName())){
            throw new CheckmarxException("No project name was provided for the scan");
        }
        if(ScanUtils.empty(params.getTeamName())){
            if(ScanUtils.empty(cxProperties.getTeam())){
                throw new CheckmarxException("No team was provided for the scan");
            }
            params.setTeamName(cxProperties.getTeam());
        }
        if(ScanUtils.empty(params.getFileExclude()) && !ScanUtils.empty(cxProperties.getExcludeFiles())){
            params.setFileExclude(Arrays.asList(cxProperties.getExcludeFiles().split(",")));
        }
        if(ScanUtils.empty(params.getFolderExclude()) && !ScanUtils.empty(cxProperties.getExcludeFolders())){
            params.setFolderExclude(Arrays.asList(cxProperties.getExcludeFolders().split(",")));
        }
        if(ScanUtils.empty(params.getScanPreset())){
            if(ScanUtils.empty(cxProperties.getScanPreset())){
                throw new CheckmarxException("No scan preset was provided for the scan");
            }
            params.setScanPreset(cxProperties.getScanPreset());
        }
        if(ScanUtils.empty(params.getScanConfiguration())){
            if(ScanUtils.empty(cxProperties.getConfiguration())){
                throw new CheckmarxException("No scan preset was provided for the scan");
            }
            params.setScanConfiguration(cxProperties.getConfiguration());
        }
        if(params.getSourceType().equals(CxScanParams.Type.GIT)){
            if(ScanUtils.empty(params.getGitUrl()) || ScanUtils.empty(params.getBranch())){
                throw new CheckmarxException("No git url or branch was was missing for the scan");
            }
        }
        else if(params.getSourceType().equals(CxScanParams.Type.FILE)){
            if(ScanUtils.empty(params.getFilePath())){
                throw new CheckmarxException("No file path was provided for the scan");
            }
        }else{
            throw new CheckmarxException("No source type was provided for the scan");
        }
    }

    /**
     * Wait for a for a scan with a given scan Id to complete with a finished or failure state
     *
     * @param scanId
     * @throws CheckmarxException
     */
    public void waitForScanCompletion(Integer scanId) throws CheckmarxException{
        Integer status = getScanStatus(scanId);
        long timer = 0;
        try {

            while (!status.equals(CxService.SCAN_STATUS_FINISHED) && !status.equals(CxService.SCAN_STATUS_CANCELED) &&
                    !status.equals(CxService.SCAN_STATUS_FAILED)) {
                Thread.sleep(cxProperties.getScanPolling());
                status = getScanStatus(scanId);
                timer += cxProperties.getScanPolling();
                if (timer >= (cxProperties.getScanTimeout() * 60000)) {
                    log.error("Scan timeout exceeded.  {} minutes", cxProperties.getScanTimeout());
                    throw new CheckmarxException("Timeout exceeded during scan");
                }
            }
            if (status.equals(CxService.SCAN_STATUS_FAILED)) {
                throw new CheckmarxException("Scan failed");
            }
        }catch (InterruptedException e){
            throw new CheckmarxException("Thread interrupted");
        }
    }
}
