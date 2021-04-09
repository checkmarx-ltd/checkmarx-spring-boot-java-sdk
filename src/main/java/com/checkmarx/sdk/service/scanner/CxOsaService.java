package com.checkmarx.sdk.service.scanner;


import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.exception.CheckmarxException;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Class used to orchestrate submitting scans and retrieving results
 */
@Service
public class CxOsaService implements CxOsaClient {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxOsaService.class);
    private final CxProperties cxProperties;
    //private final CxHttpClient cxHttpClient;
    private static final String OSA_VULN = "Vulnerable_Library";

    /*public CxOsaService(CxProperties cxProperties, CxHttpClient cxHttpClient) {
        this.cxProperties = cxProperties;
        this.cxHttpClient = cxHttpClient;
    }*/

    public CxOsaService(CxProperties cxProperties) {
        this.cxProperties = cxProperties;
    }

    public ScanResults createScanAndReport(Integer projectId, String sourceDir, ScanResults results, List<Filter> filter) throws CheckmarxException{
        /*CxScanConfig config = getOsaScanConfig();
        //---------------------------------------------
        //config.setOsaFolderExclusions("");
        //config.setOsaFilterPattern("");
        //config.setOsaArchiveIncludePatterns("");
        //config.getOsaRunInstall());
        config.setSourceDir(sourceDir);
        config.setOsaRunInstall(true);
        CxOSAClient osaClient = new CxOSAClient(cxHttpClient, log, config);
        try {
            String scanId = osaClient.createOSAScan(projectId);
            OSAResults osaResults = osaClient.getOSAResults(scanId, projectId);
            return mapOsaResults(osaResults, results, filter);
        }catch (CxClientException | InterruptedException | IOException e){
            log.error(ExceptionUtils.getMessage(e));
            log.error(ExceptionUtils.getRootCauseMessage(e));
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException();
        }*/
        return null;
    }

    public String createScan(Integer projectId, String sourceDir) throws CheckmarxException{
        /*CxScanConfig config = getOsaScanConfig();
        //---------------------------------------------
        //config.setOsaFolderExclusions("");
        //config.setOsaFilterPattern("");
        //config.setOsaArchiveIncludePatterns("");
        //config.getOsaRunInstall());
        config.setSourceDir(sourceDir);
        config.setOsaRunInstall(true);
        CxOSAClient osaClient = new CxOSAClient(cxHttpClient, log, config);
        try {
            return osaClient.createOSAScan(projectId);
        }catch (CxClientException | IOException e){
            log.error(ExceptionUtils.getMessage(e));
            log.error(ExceptionUtils.getRootCauseMessage(e));
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException();
        }*/
        return null;
    }

    public ScanResults waitForOsaScan(String scanId, Integer projectId, ScanResults results, List<Filter> filter) throws CheckmarxException {
        /*CxScanConfig config = getOsaScanConfig();
        //---------------------------------------------
        //config.setOsaFolderExclusions("");
        //config.setOsaFilterPattern("");
        //config.setOsaArchiveIncludePatterns("");
        CxOSAClient osaClient = new CxOSAClient(cxHttpClient, log, config);
        try {
            OSAResults osaResults =  osaClient.getOSAResults(scanId, projectId);
            return mapOsaResults(osaResults, results, filter);
        }catch (CxClientException | InterruptedException | IOException e){
            log.error(ExceptionUtils.getMessage(e));
            log.error(ExceptionUtils.getRootCauseMessage(e));
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException();
        }*/
        return null;
    }

    public ScanResults getLatestOsaResults(Integer projectId, ScanResults results, List<Filter> filter) throws CheckmarxException {
        /*CxScanConfig config = getOsaScanConfig();
        //---------------------------------------------
        //config.setOsaFolderExclusions("");
        //config.setOsaFilterPattern("");
        //config.setOsaArchiveIncludePatterns("");
        CxOSAClient osaClient = new CxOSAClient(cxHttpClient, log, config);
        try {
            OSAResults osaResults = osaClient.getLatestOSAResults(projectId);
            return mapOsaResults(osaResults, results, filter);
        }catch (CxClientException | InterruptedException | IOException e){
            log.error(ExceptionUtils.getMessage(e));
            log.error(ExceptionUtils.getRootCauseMessage(e));
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new CheckmarxException();
        }*/
        return null;
    }

    /*public ScanResults mapOsaResults(OSAResults osaResults, ScanResults results, List<Filter> filter) {
        if (osaResults.getOsaLibraries() == null || osaResults.getOsaVulnerabilities() == null) {
            return results;
        }
        if(results == null) {
            results = new ScanResults();
        }
        List<ScanResults.XIssue> issueList = new ArrayList<>();

        Map<String, Integer> severityMap = ImmutableMap.of(
                "LOW", 1,
                "MEDIUM", 2,
                "HIGH", 3
        );
        Map<String, Integer> summary = (Map<String, Integer>) results.getAdditionalDetails().get(Constants.SUMMARY_KEY);
        if(summary == null) {
            summary = new HashMap<>();
        }
        Map<String, Library> libsMap = getOsaLibsMap(osaResults.getOsaLibraries());
        for (CVE o : osaResults.getOsaVulnerabilities()) {
            if (filterOsa(filter, o) && libsMap.containsKey(o.getLibraryId())) {
                Library lib = libsMap.get(o.getLibraryId());
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

                    if(!summary.containsKey(issue.getSeverity())){
                        summary.put(issue.getSeverity(), 0);
                    }
                    int x = summary.get(issue.getSeverity());
                    x++;
                    summary.put(issue.getSeverity(), x);
                }
            }
        }
        results.getAdditionalDetails().put(Constants.SUMMARY_KEY, summary);
        results.setOsa(true);
        results.getXIssues().addAll(issueList);
        return results;
    }*/

    /*private boolean filterOsa(List<Filter> filters, CVE osa) {
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
    }*/

    /*private Map<String, Library> getOsaLibsMap(List<Library> libs) {
        Map<String, Library> libMap = new HashMap<>();
        for (Library o : libs) {
            libMap.put(o.getId(), o);
        }
        return libMap;
    }*/

    /*private CxScanConfig getOsaScanConfig(){
        CxScanConfig config = new CxScanConfig();
        config.setUrl(cxProperties.getBaseUrl());
        config.setUsername(cxProperties.getUsername());
        config.setPassword(cxProperties.getPassword());
        config.setCxOrigin("CxFlow");
        config.setDisableCertificateValidation(false);
        return config;
    }*/
}
