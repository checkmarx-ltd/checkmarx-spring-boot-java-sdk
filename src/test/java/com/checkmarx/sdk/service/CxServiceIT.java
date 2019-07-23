package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.exception.CheckmarxException;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CxServiceIT {

    @Autowired
    private CxProperties properties;
    @Autowired
    private CxService service;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

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
        try{
            String teamId = service.getTeamId(properties.getTeam());
            assertNotNull(teamId);
            assertNotEquals("-1", teamId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getTeamIdFail() {
        try{
            String teamId = service.getTeamId("\\Not\\Valid");
            assertNotNull(teamId);
            assertEquals("-1", teamId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void createTeam() {
    }

    @Test
    public void getScanConfiguration() {
    }

    @Test
    public void getPresetId() {
        try {
            Integer id = service.getPresetId(Constants.CX_DEFAULT_PRESET);
            assertNotNull(id);
            assertTrue(id > 0);
        }catch (CheckmarxException e){
            fail("Unexpected Checkmarx Exception thrown");
        }
    }

    @Test
    public void getScanSummaryByScanId() {
    }

    @Test
    public void getScanSummary() {
    }

    @Test
    public void getScanSummary1() {
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
    public void deleteScan() {
    }

    @Test
    public void getLatestScanReport() {
    }

    @Test
    public void getLatestScanResults() {
    }

    @Test
    public void waitForScanCompletion() {
    }

    @Test
    public void getLdapServerId(){
        try {
            Integer id = service.getLdapServerId("checkmarx.local");
            assertNotNull(id);
            assertTrue(id > 0);
        }catch (CheckmarxException e){
            fail("Unexpected Checkmarx Exception");
        }
    }

    @Test
    public void updateLdapMapping(){
        try {
            String teamId = service.getTeamId(properties.getTeam());
            service.mapTeamLdap(1, teamId, "CN=CX_USERS,CN=Users,DC=checkmarx,DC=local");
        }catch (CheckmarxException e){
            fail("Unexpected Checkmarx Exception");
        }
    }
}