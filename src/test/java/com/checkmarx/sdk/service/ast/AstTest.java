package com.checkmarx.sdk.service.ast;

import com.checkmarx.sdk.GithubProperties;
import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.config.SpringConfiguration;
import com.checkmarx.sdk.dto.AstScaResults;
import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.service.CommonClientTest;
import com.checkmarx.sdk.service.scanner.AstScanner;
import com.checkmarx.sdk.dto.RemoteRepositoryInfo;
import com.checkmarx.sdk.config.AstConfig;
import com.checkmarx.sdk.dto.ast.report.AstSummaryResults;
import com.checkmarx.sdk.dto.ast.report.Finding;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.SourceLocationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@RunWith(SpringRunner.class)
@Import(SpringConfiguration.class)
@SpringBootTest
@Slf4j
public class AstTest extends CommonClientTest {

    @Autowired
    AstProperties astProperties;
    
    @Autowired 
    CxProperties cxProperties;
    
    @Autowired
    GithubProperties githubProperties;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        RestClientConfig config = getScanConfig();

        AstScanner scanner = getScanner();
        AstScaResults scanResults = scanner.scan(toSdkScanParams(config));
        validateFinalResults(scanResults);
 
    }

    private ScanParams toSdkScanParams(RestClientConfig config) throws MalformedURLException {
        
        ScanParams scanParams = ScanParams.builder()
                .projectName(config.getProjectName())
                .remoteRepoUrl(config.getAstConfig().getRemoteRepositoryInfo().getUrl())
                .filterConfiguration(FilterConfiguration.builder().build())
                .build();
        return scanParams;
    }

    protected AstScanner getScanner() {
            return new AstScanner(astProperties);
    }
    private void validateFinalResults(AstScaResults finalResults) {
        Assert.assertNotNull("Final scan results are null.", finalResults);

        ASTResults ASTResults = finalResults.getAstResults();
        Assert.assertNotNull("AST-SAST results are null.", ASTResults);
        Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(ASTResults.getScanId()));
        Assert.assertTrue("Web report link is missing.", StringUtils.isNotEmpty(ASTResults.getWebReportLink()));

        validateFindings(ASTResults);
        validateSummary(ASTResults);
    }

    private void validateSummary(ASTResults ASTResults) {
        AstSummaryResults summary = ASTResults.getSummary();
        Assert.assertNotNull("Summary is null.", summary);
        Assert.assertTrue("No medium-severity vulnerabilities.",
                summary.getMediumVulnerabilityCount() > 0);

        Assert.assertNotNull("Status counter list is null.", summary.getStatusCounters());
        Assert.assertFalse("No status counters.", summary.getStatusCounters().isEmpty());

        Assert.assertTrue("Expected total counter to be a positive value.", summary.getTotalCounter() > 0);

        int actualFindingCount = ASTResults.getFindings().size();
        Assert.assertEquals("Total finding count from summary doesn't correspond to the actual count.",
                actualFindingCount,
                summary.getTotalCounter());

        long actualFindingCountExceptInfo = ASTResults.getFindings()
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

    private void validateFindings(ASTResults ASTResults) {
        List<Finding> findings = ASTResults.getFindings();
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
    
    private RestClientConfig getScanConfig() throws MalformedURLException {
        AstConfig astConfig = AstConfig.builder()
                .apiUrl(astProperties.getApiUrl())
                .webAppUrl(astProperties.getWebAppUrl())
                .clientSecret(astProperties.getClientSecret())
                .clientId("CxFlow")
                .sourceLocationType(SourceLocationType.REMOTE_REPOSITORY)
                .build();

        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();
        URL repoUrl = new URL(githubProperties.getUrl());
        repoInfo.setUrl(repoUrl);
        astConfig.setRemoteRepositoryInfo(repoInfo);
        astConfig.setResultsPageSize(10);
        astConfig.setPresetName("Checkmarx Default");

        RestClientConfig config = new RestClientConfig();
        config.setAstConfig(astConfig);
        config.setProjectName("sdkAstProject");
        config.setProgressInterval(5);
        return config;
    }
}
