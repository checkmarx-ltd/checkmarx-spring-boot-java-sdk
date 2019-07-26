package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxProject;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.cx.CxScanSummary;
import com.checkmarx.sdk.dto.cx.xml.CxXMLResultsType;
import com.checkmarx.sdk.exception.CheckmarxException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CxServiceIT {

    @Autowired
    private CxProperties properties;
    @Autowired
    private CxService service;

    @Test
    public void getLastScanDate() {
        try {
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, "CxSBSDK-IT");
            LocalDateTime dateTime = service.getLastScanDate(projectId);
            assertNotNull(dateTime);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }

    }

    @Test
    public void getLastScanId() {
        try {
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, "CxSBSDK-IT");
            Integer id = service.getLastScanId(projectId);
            assertNotNull(id);
            assertTrue(id > 0);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getCustomFields() {
    }

    @Test
    public void createAndDeleteProject() {
        try {
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.createProject(teamId, "CxSDK-ToDelete-Test");
            assertNotNull(projectId);
            assertTrue(projectId > 0);
            service.deleteProject(projectId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getProject() {
        try{
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, "CxSBSDK-IT");
            CxProject project = service.getProject(projectId);
            assertNotNull(project);
            assertNotEquals("-1", project.getId());
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
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
    public void createAndDeleteTeam() {
        try {
            String id = service.getTeamId(properties.getTeam());
            String teamId = service.createTeamWS(id, "abcdef");
            service.deleteTeamWS(teamId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
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
    public void getScanSummary() {
        try{
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, "CxSBSDK-IT");
            CxScanSummary summary = service.getScanSummary(projectId);
            assertNotNull(summary);
            assertNotNull(summary.getStatisticsCalculationDate());
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void createScan() {
        try {
            CxScanParams params = new CxScanParams()
                    .withSourceType(CxScanParams.Type.GIT)
                    .withGitUrl("https://github.com/Custodela/Riches.git")
                    .withBranch("refs/heads/master")
                    .withProjectName("CxSBSDK-IT");
            //String teamId = service.getTeamId(properties.getTeam());
            Integer id = service.createScan(params, "Automated Comment");
            assertNotNull(id);
            assertTrue(id > 0);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getReportContent() {
        try {
            List<Filter> filters = new ArrayList<>();
            filters.add(new Filter(Filter.Type.SEVERITY, "High"));
            ScanResults results = service.getLatestScanResults(properties.getTeam(), "CxSBSDK-IT", filters);
            assertNotNull(results);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getXmlReportContent() {
        try {
            CxXMLResultsType results = service.getLatestScanReport(properties.getTeam(), "CxSBSDK-IT");
            assertNotNull(results);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    //TESTS FOR LDAP
    /*@Test
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
            service.mapTeamLdapWS(1, teamId, "Custodela","CN=CX_USERS,CN=Users,DC=checkmarx,DC=local");
        }catch (CheckmarxException e){
            fail("Unexpected Checkmarx Exception");
        }
    }

    @Test
    public void removeLdapMapping(){
        try {
            String teamId = service.getTeamId(properties.getTeam());
            service.removeTeamLdapWS(1, teamId, "Custodela","CN=CX_USERS,CN=Users,DC=checkmarx,DC=local");
        }catch (CheckmarxException e){
            fail("Unexpected Checkmarx Exception");
        }
    }*/
}