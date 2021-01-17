package com.checkmarx.sdk.service.ast;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.service.CommonClientTest;
import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Slf4j
public class AstSastTest extends CommonClientTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        CxScanConfig config = getScanConfig();

        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            CommonScanResults initialResults = client.initiateScan();
            validateInitialResults(initialResults);

            CommonScanResults finalResults = client.waitForScanResults();
            validateFinalResults(finalResults);
        } catch (Exception e) {
            failOnException(e);
        }
    }

    private void validateFinalResults(CommonScanResults finalResults) {
        Assert.assertNotNull("Final scan results are null.", finalResults);

        AstSastResults astSastResults = finalResults.getAstResults();
        Assert.assertNotNull("AST-SAST results are null.", astSastResults);
        Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(astSastResults.getScanId()));
        Assert.assertTrue("Web report link is missing.", StringUtils.isNotEmpty(astSastResults.getWebReportLink()));

        validateFindings(astSastResults);
        validateSummary(astSastResults);
    }

    private void validateSummary(AstSastResults astSastResults) {
        AstSastSummaryResults summary = astSastResults.getSummary();
        Assert.assertNotNull("Summary is null.", summary);
        Assert.assertTrue("No medium-severity vulnerabilities.",
                summary.getMediumVulnerabilityCount() > 0);

        Assert.assertNotNull("Status counter list is null.", summary.getStatusCounters());
        Assert.assertFalse("No status counters.", summary.getStatusCounters().isEmpty());

        Assert.assertTrue("Expected total counter to be a positive value.", summary.getTotalCounter() > 0);

        int actualFindingCount = astSastResults.getFindings().size();
        Assert.assertEquals("Total finding count from summary doesn't correspond to the actual count.",
                actualFindingCount,
                summary.getTotalCounter());

        long actualFindingCountExceptInfo = astSastResults.getFindings()
                .stream()
                .filter(finding -> !StringUtils.equalsIgnoreCase(finding.getSeverity(), "info"))
                .count();

        int countFromSummaryExceptInfo = summary.getHighVulnerabilityCount()
                + summary.getMediumVulnerabilityCount()
                + summary.getLowVulnerabilityCount();

        Assert.assertEquals("Finding count from summary (excluding 'info') doesn't correspond to the actual count.",
                actualFindingCountExceptInfo,
                countFromSummaryExceptInfo);
    }

    private void validateFindings(AstSastResults astSastResults) {
        List<Finding> findings = astSastResults.getFindings();
        Assert.assertNotNull("Finding list is null.", findings);
        Assert.assertFalse("Finding list is empty.", findings.isEmpty());

        boolean someNodeListsAreEmpty = findings.stream().anyMatch(finding -> finding.getNodes().isEmpty());
        Assert.assertFalse("Some of the finding node lists are empty.", someNodeListsAreEmpty);

                
        log.info("Validating each finding.");

        findings.forEach(this::validateFinding);

        validateDescriptions(findings);
    }

    private void validateDescriptions(List<Finding> findings) {

        Map<String, Set<String>> mapDescriptions = new HashMap<>();
        
        findings.forEach(finding -> {
            Set<String> listDescriptions = mapDescriptions.get(finding.getQueryID());
            if(listDescriptions == null){
                listDescriptions = new HashSet<>();
            }
            listDescriptions.add(finding.getDescription());
            mapDescriptions.put(finding.getQueryID(), listDescriptions);
        });

        Set<String> uniqueDescriptions = new HashSet<>();
        
        //validate for each queryId there is exactly one corresponding description
        for( Map.Entry<String, Set<String>> entry :mapDescriptions.entrySet()){
            Assert.assertEquals( 1, entry.getValue().size());
            uniqueDescriptions.add((String)entry.getValue().toArray()[0]);
        }
        
        //validate all descriptions are unique
        Assert.assertEquals(uniqueDescriptions.size(),mapDescriptions.size() );
    }

    private void validateFinding(Finding finding) {
        logFinding(finding);
        Assert.assertTrue("State is missing.", StringUtils.isNotEmpty(finding.getState()));
        Assert.assertTrue("Status is missing.", StringUtils.isNotEmpty(finding.getStatus()));
        Assert.assertTrue("Severity is missing.", StringUtils.isNotEmpty(finding.getSeverity()));
        Assert.assertTrue("Query name is missing.", StringUtils.isNotEmpty(finding.getQueryName()));
        Assert.assertTrue("Description is missing. ", StringUtils.isNotEmpty(finding.getDescription()));
    }

    private void logFinding(Finding finding) {
        try {
            log.info("Validating finding: {}", objectMapper.writeValueAsString(finding));
        } catch (JsonProcessingException e) {
            Assert.fail("Error serializing finding to JSON.");
        }
    }


    private void validateInitialResults(CommonScanResults initialResults) {
        Assert.assertNotNull("Initial scan results are null.", initialResults);
        Assert.assertNotNull("AST-SAST results are null.", initialResults.getAstResults());
        Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(initialResults.getAstResults().getScanId()));
    }

    private CxScanConfig getScanConfig() throws MalformedURLException {
        AstSastConfig astConfig = AstSastConfig.builder()
                .apiUrl(prop("astSast.apiUrl"))
                .webAppUrl(prop("astSast.webAppUrl"))
                .clientSecret(prop("astSast.clientSecret"))
                .clientId("CxFlow")
                .sourceLocationType(SourceLocationType.REMOTE_REPOSITORY)
                .build();

        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();
        URL repoUrl = new URL(prop("astSast.remoteRepoUrl.public"));
        repoInfo.setUrl(repoUrl);
        astConfig.setRemoteRepositoryInfo(repoInfo);
        astConfig.setResultsPageSize(10);
        astConfig.setPresetName("Checkmarx Default");

        CxScanConfig config = new CxScanConfig();
        config.setAstSastConfig(astConfig);
        config.setProjectName(prop("astSast.projectName"));
        config.addScannerType(ScannerType.AST_SAST);
        config.setOsaProgressInterval(5);
        return config;
    }
}
