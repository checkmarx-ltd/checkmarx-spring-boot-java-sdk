package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxConfig;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.exception.CheckmarxException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Import(CxConfig.class)
@SpringBootTest
public class CxServiceTest {

    @Autowired
    private CxProperties properties;
    @Autowired
    private CxService service;
    @Autowired
    private CxAuthService authService;


    @Test
    public void createScan() {
    }

    @Test
    public void getLastScanId() {
    }

    @Test
    public void getScanData() {
    }

    @Test
    public void getLastScanDate() {
    }

    @Test
    public void getScanStatus() {
    }

    @Test
    public void createScanReport() {
    }

    @Test
    public void getReportStatus() {
    }

    @Test
    public void getReportContent() {
        properties.setOffline(true);
        File file = new File(
                getClass().getClassLoader().getResource("ScanReport.xml").getFile()
        );
        try {
            ScanResults results = service.getReportContent(file, null);
            assertNotNull(results);
            List<ScanResults.XIssue> issues = results.getXIssues()
                    .stream()
                    .filter(x -> x.getFalsePositiveCount() > 0)
                    .collect(Collectors.toList());
            assertEquals(2, issues.size());
            assertEquals("Command_Injection", issues.get(0).getVulnerability());
            List<ScanResults.XIssue> sqlIssues = results.getXIssues()
                    .stream()
                    .filter(x -> x.getVulnerability().equalsIgnoreCase("SQL_INJECTION") &&
                    x.getSeverity().equalsIgnoreCase("HIGH"))
                    .collect(Collectors.toList());
            assertEquals(3, sqlIssues.size());
        } catch (CheckmarxException e) {
            fail("Unexpected Exception");
        }
    }

    @Test
    public void getXmlReportContent() {
    }

    @Test
    public void getAdditionalScanDetails() {
    }

    @Test
    public void getCustomFields() {
    }

    @Test
    public void getReportContent1() {
    }

    @Test
    public void getOsaReportContent() {
    }

    @Test
    public void getIssueDescription() {
    }

    @Test
    public void createProject() {
    }

    @Test
    public void getProjects() {
    }

    @Test
    public void getProjects1() {
    }

    @Test
    public void getProjectId() {
    }

    @Test
    public void getProject() {
    }

    @Test
    public void scanExists() {
    }

    @Test
    public void createScanSetting() {
    }

    @Test
    public void setProjectRepositoryDetails() {
    }

    @Test
    public void uploadProjectSource() {
    }

    @Test
    public void setProjectExcludeDetails() {
    }

    @Test
    public void getTeamId() {
    }

    @Test
    public void createTeam() {
    }

    @Test
    public void getScanConfiguration() {
    }

    @Test
    public void getPresetId() {
    }

    @Test
    public void getScanSummary() {
    }

    @Test
    public void getScanSummary1() {
    }

    @Test
    public void getScanSummary2() {
    }

    @Test
    public void createScan1() {
    }

    @Test
    public void createScanAndReport() {
    }

    @Test
    public void createScanAndReport1() {
    }

    @Test
    public void getLatestScanReport() {
    }

    @Test
    public void getLatestScanResults() {
    }
}