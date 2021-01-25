package com.checkmarx.sdk.service.ast;

import com.checkmarx.sdk.GithubProperties;
import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.config.SpringConfiguration;
import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.service.CommonClientTest;
import com.cx.restclient.AstScanner;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.ast.dto.sast.report.Finding;
import com.cx.restclient.configuration.RestClientConfig;
import com.cx.restclient.dto.SourceLocationType;
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
public class AstSastTest extends CommonClientTest {

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
        ASTResultsWrapper scanResults = scanner.scan(toSdkScanParams(config));
        validateFinalResults(scanResults);
 
    }

    private ScanParams toSdkScanParams(RestClientConfig config) throws MalformedURLException {
        
        ScanParams scanParams = ScanParams.builder()
                .projectName(config.getProjectName())
                .remoteRepoUrl(config.getAstSastConfig().getRemoteRepositoryInfo().getUrl())
                .filterConfiguration(FilterConfiguration.builder().build())
                .build();
        return scanParams;
    }

//    
//    protected AstSastConfig getScanParams(RestClientConfig config) throws MalformedURLException {
//        AstSastConfig astSastConfig = new AstSastConfig();
//        astSastConfig.setWebAppUrl(astProperties.getWebAppUrl());
//        astSastConfig.setApiUrl(astProperties.getApiUrl());
//        astSastConfig.setPresetName(astProperties.getPreset());
//        astSastConfig.setClientId(astProperties.getClientId());
//        astSastConfig.setClientSecret(astProperties.getClientSecret());
//        astSastConfig.setRemoteRepositoryInfo(new RemoteRepositoryInfo());
//
//        astSastConfig.getRemoteRepositoryInfo().setUrl(new URL(config.getUrl()));
//        astSastConfig.getRemoteRepositoryInfo().setBranch("master");
//        
//        new ScanParams()
//        return astSastConfig;
//    }

    protected AstScanner getScanner() {
            return new AstScanner(astProperties);
    }
    private void validateFinalResults(ASTResultsWrapper finalResults) {
        Assert.assertNotNull("Final scan results are null.", finalResults);

        ASTResults astSastResults = finalResults.getAstResults();
        Assert.assertNotNull("AST-SAST results are null.", astSastResults);
        Assert.assertTrue("Scan ID is missing.", StringUtils.isNotEmpty(astSastResults.getResults().getScanId()));
        Assert.assertTrue("Web report link is missing.", StringUtils.isNotEmpty(astSastResults.getResults().getWebReportLink()));

        validateFindings(astSastResults);
        validateSummary(astSastResults);
    }

    private void validateSummary(ASTResults astSastResults) {
        AstSastSummaryResults summary = astSastResults.getResults().getSummary();
        Assert.assertNotNull("Summary is null.", summary);
        Assert.assertTrue("No medium-severity vulnerabilities.",
                summary.getMediumVulnerabilityCount() > 0);

        Assert.assertNotNull("Status counter list is null.", summary.getStatusCounters());
        Assert.assertFalse("No status counters.", summary.getStatusCounters().isEmpty());

        Assert.assertTrue("Expected total counter to be a positive value.", summary.getTotalCounter() > 0);

        int actualFindingCount = astSastResults.getResults().getFindings().size();
        Assert.assertEquals("Total finding count from summary doesn't correspond to the actual count.",
                actualFindingCount,
                summary.getTotalCounter());

        long actualFindingCountExceptInfo = astSastResults.getResults().getFindings()
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

    private void validateFindings(ASTResults astSastResults) {
        List<Finding> findings = astSastResults.getResults().getFindings();
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
        AstSastConfig astConfig = AstSastConfig.builder()
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
        config.setAstSastConfig(astConfig);
        config.setProjectName("sdkAstProject");
        config.setOsaProgressInterval(5);
        return config;
    }
}
