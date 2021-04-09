package com.checkmarx.sdk.service.sca;

import com.checkmarx.sdk.dto.AstScaResults;
import com.checkmarx.sdk.dto.sca.Summary;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.service.CommonClientTest;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.dto.sca.report.Package;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public  abstract class ScaTestsBase extends CommonClientTest {
    // Storing the test project as an archive to avoid cluttering the current project
    // and also to prevent false positives during a vulnerability scan of the current project.
    protected static final String PACKED_SOURCES_TO_SCAN = "sources-to-scan.zip";
    

    protected void verifyScanResults(AstScaResults results) {
        assertNotNull("Scan results are null.", results);

        SCAResults scaResults = results.getScaResults();
        assertNotNull("SCA results are null", scaResults);
        
        log.info("scanID " + scaResults.getScanId());
        assertTrue("Scan ID is empty", StringUtils.isNotEmpty(scaResults.getScanId()));
        assertTrue("Web report link is empty", StringUtils.isNotEmpty(scaResults.getWebReportLink()));

        verifySummary(scaResults.getSummary());
        verifyPackages(scaResults);
        verifyFindings(scaResults);
    }
    
    private void verifySummary(Summary summary) {

        assertNotNull("SCA summary is null", summary);
        assertTrue("SCA hasn't found any packages.", summary.getTotalPackages() > 0);

        boolean anyVulnerabilitiesDetected = 
                summary.getFindingCounts().get(Filter.Severity.HIGH) > 0 || 
                        summary.getFindingCounts().get(Filter.Severity.MEDIUM) > 0 ||
                        summary.getFindingCounts().get(Filter.Severity.LOW) > 0 ;
        
        assertTrue("Expected that at least one vulnerability would be detected.", anyVulnerabilitiesDetected);
    }

    private void verifyPackages(SCAResults scaResults) {
        List<Package> packages = scaResults.getPackages();

        assertNotNull("Packages are null.", packages);
        assertFalse("Response contains no packages.", packages.isEmpty());

        assertEquals("Actual package count differs from package count in summary.",
                scaResults.getSummary().getTotalPackages(),
                packages.size());
    }

    private void verifyFindings(SCAResults scaResults) {
        List<Finding> findings = scaResults.getFindings();
        assertNotNull("Findings are null", findings);
        assertFalse("Response contains no findings.", findings.isEmpty());

        // Special check due to a case-sensitivity issue.
        boolean allSeveritiesAreSpecified = findings.stream()
                .allMatch(finding -> finding.getSeverity() != null);

        assertTrue("Some of the findings have severity set to null.", allSeveritiesAreSpecified);
    }
}
