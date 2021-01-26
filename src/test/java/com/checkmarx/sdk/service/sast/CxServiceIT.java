package com.checkmarx.sdk.service.sast;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.SpringConfiguration;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.sast.CxUser;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxProject;
import com.checkmarx.sdk.dto.cx.CxRole;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.cx.CxScanSummary;
import com.checkmarx.sdk.dto.cx.CxTeam;
import com.checkmarx.sdk.dto.cx.xml.CxXMLResultsType;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.InvalidCredentialsException;
import com.checkmarx.sdk.service.CxAuthService;
import com.checkmarx.sdk.service.CxService;
import com.checkmarx.sdk.service.CxUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Import(SpringConfiguration.class)
@SpringBootTest
@Slf4j
public class CxServiceIT {

    @Autowired
    private CxProperties properties;
    @Autowired
    private CxService service;
    @Autowired
    private CxAuthService authService;

    @Autowired
    private CxUserService userService;
    private String token=null;

    @Test
    @Ignore("Stable environment required")
    public void completeScanFlow() throws CheckmarxException {
        final String PROJECT_NAME = "my-project-name";
        final String GIT_REPO_URL = "https://github.com/my-organization/my-repo.git";
        final String BRANCH_NAME = "refs/heads/develop";

        String teamId = service.getTeamId(properties.getTeam());
        Integer projectId = service.getProjectId(teamId, PROJECT_NAME);
        CxScanParams params = new CxScanParams();
        params.setProjectName(PROJECT_NAME);
        params.setTeamId(teamId);
        params.setProjectId(projectId);
        params.setGitUrl(GIT_REPO_URL);
        params.setBranch(BRANCH_NAME);
        params.setSourceType(CxScanParams.Type.GIT);
        //run the scan and wait for it to finish
        Integer x = service.createScan(params, "CxSDK Scan");
        service.waitForScanCompletion(x);

        List<Filter> highSeverityOnly = Collections.singletonList(new Filter(Filter.Type.SEVERITY, "High"));
        FilterConfiguration filterConfiguration = FilterConfiguration.fromSimpleFilters(highSeverityOnly);

        //generate the results
        ScanResults results = service.getReportContentByScanId(x, filterConfiguration);
        assertNotNull(results);
    }
    
    @Test
    public void login() {
        if(token != null) {
            try {
                token = authService.getAuthToken(
                        properties.getUsername(),
                        properties.getPassword(),
                        properties.getClientId(),
                        properties.getClientSecret(),
                        properties.getScope()
                );

                assertNotNull(token);
                assertNotEquals("", token);
            } catch (InvalidCredentialsException e) {
                fail("Unexpected InvalidCredentialsException");
            }
        }
    }

 /*   @Test
    public void createOSAScanTest() {
        try {
            String id = osaService.createScan(2, "C:\\Users\\XXXX\\Documents\\projects\\samples\\JavaVulnerableLab");
            ScanResults results = new ScanResults();
            results = osaService.waitForOsaScan(id, 1, results, null);
            assertNotNull(id);
        } catch (CheckmarxException e) {
             fail("Unexpected CheckmarxException");
        }
    }*/

    @Test
    public void getRoles() {
        login();
        try {
            List<CxRole> roles = service.getRoles();
            assertNotNull(roles);
        } catch (CheckmarxException e) {
            if(properties.getVersion() >= 9.0 ) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void getRoleId() {
        try {
            Integer id = service.getRoleId("Admin");
            assertNotNull(id);
            assertTrue(id > 0);
        } catch (CheckmarxException e) {
            if(properties.getVersion() >= 9.0 ) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void createTeam() {
        try {
            String id = service.getTeamId(properties.getTeam());
            String newTeamId = service.createTeam(id, "Whatever");
            assertNotNull(newTeamId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void deleteTeam() {
        try {
            String id = service.getTeamId(properties.getTeam().concat(properties.getTeamPathSeparator()).concat("Whatever"));
            service.deleteTeam(id);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getLastScanDate() {
        try {
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, "Riches");
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
            Integer projectId = service.getProjectId(teamId, "Riches");
            Integer id = service.getLastScanId(projectId);
            assertNotNull(id);
            assertTrue(id > 0);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    //TODO enable once access to cxprivatecloud is fixed
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
            Integer projectId = service.getProjectId(teamId, "Riches");
            CxProject project = service.getProject(projectId);
            assertNotNull(project);
            assertNotEquals(Integer.valueOf(-1), project.getId());
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
            String teamId = service.createTeam(id, "abcdef");
            service.deleteTeam(teamId);
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
    public void getPresetName() {
        String name = service.getPresetName(1);
        assertNotNull(name);
        assertEquals("All", name);
    }


    @Test
    public void getScanSummary() {
        try{
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, "Riches");
            CxScanSummary summary = service.getScanSummary(projectId);
            assertNotNull(summary);
            assertNotNull(summary.getStatisticsCalculationDate());
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }
    
    @Test
    public void createScan() {
        log.info("Creating scan");
        login();
        try {
            CxScanParams params = new CxScanParams()
                    .withSourceType(CxScanParams.Type.GIT)
                    .withGitUrl("https://github.com/Custodela/Riches.git")
                    .withBranch("refs/heads/master")
                    .withProjectName("CxSBSDK-IT");
                   // .withProjectName("Riches");
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
            FilterConfiguration filterConfiguration = FilterConfiguration.fromSimpleFilters(filters);
            ScanResults results = service.getLatestScanResults(properties.getTeam(), "Riches", filterConfiguration);
            assertNotNull(results);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getXmlReportContent() {
        try {
            CxXMLResultsType results = service.getLatestScanReport(properties.getTeam(), "Riches");
            assertNotNull(results);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getTeams() {
        try {
            List<CxTeam> teams = service.getTeams();
            assertNotNull(teams);
            assertTrue(teams.size() > 0);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getUsers() {
        try {
            List<CxUser> users = userService.getUsers();
            assertNotNull(users);
        }catch (CheckmarxException e){
            assert(true);
        }
    }

    /*@Test
    public void getUser() {
        try {
            CxUser user = userService.getUser(2);
            assertNotNull(user);
            assertNotNull(user.getUserName());
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }*/

    /*
    @Test
    public void addUser() {
        try {
            CxUser user = new CxUser()
                    .withActive(true)
                    .withUserName("mytestuser4")
                    .withFirstName("My")
                    .withLastName("TestUser4")
                    .withEmail("my4@testuser.com")
                    .withPassword("XXXXXXX")
                    .withType8x(CxUserTypes.SAML)
                    //.withType8x(CxUserTypes.APPLICATION)
                    .withCompany8x("Checkmarx")
                    .withCompanyId8x("343e6316-58b6-4f32-bb18-8ec582f0b578")
                    .withRole8x(CxUser.Role8x.SCANNER);

            Map<String, String> teams = new HashMap<>();
            teams.put("9c4fc7ae-3e41-4c88-ad1d-59e8032e2923","Custodela");
            user.setTeams8x(teams);
            userService.addUser(user);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }
    */

    /*@Test
    public void getCompanyId(){
        try {
            String id = userService.getCompanyId("Checkmarx");
            assertNotNull(id);
            assertNotEquals(id, "");
        } catch (CheckmarxException e) {
            fail("Unexpected Exception");
        }
    }*/

    //TESTS FOR LDAP - test instance does not include LDAP
/* CxPrivateCloud does not have LDAP configuration to test with
     @Test
     public void getCxTeamLdap() {
         try {
             Integer ldapId = service.getLdapServerId("cx.local");
             List<CxTeamLdap> ldapTeams = service.getTeamLdap(ldapId);
             assertNotNull(ldapTeams);
         } catch (CheckmarxException e) {
             if (properties.getVersion() >= 9.0) {
                 fail("Unexpected CheckmarxException");
             }
         }
     }

    @Test
    public void addCxTeamLdap() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            String teamId = service.getTeamId("/CxServer/Checkmarx/CxFlow");
            service.mapTeamLdap(ldapId, teamId,"CxFlow", "CN=CX_ADMIN,CN=Users,DC=cx,DC=local");
        } catch (CheckmarxException e) {
            if (properties.getVersion() >= 9.0) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void deleteCxTeamLdap() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            String teamId = service.getTeamId("/CxServer/Checkmarx/CxFlow");
            service.removeTeamLdap(ldapId, teamId,"CxFlow", "CN=CX_ADMIN,CN=Users,DC=cx,DC=local");
        } catch (CheckmarxException e) {
            if (properties.getVersion() >= 9.0) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void getCxRoleLdap() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            List<CxRoleLdap> ldapRoles = service.getRoleLdap(ldapId);
            assertNotNull(ldapRoles);
        } catch (CheckmarxException e) {
            if (properties.getVersion() >= 9.0) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void createLdapRoleMap() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            Integer roleId = service.getRoleId("CX_USER");
            service.mapRoleLdap(ldapId, roleId, "CN=CX_USERS,CN=Users,DC=cx,DC=local");
            Integer id = service.getLdapRoleMapId(ldapId, roleId, "CN=CX_USERS,CN=Users,DC=cx,DC=local");
            assertNotNull(id);
        } catch (CheckmarxException e) {
            if (properties.getVersion() >= 9.0) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void deleteLdapRoleMap() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            Integer roleId = service.getRoleId("CX_USER");
            service.removeRoleLdap(ldapId, roleId, "CN=CX_USERS,CN=Users,DC=cx,DC=local");
        } catch (CheckmarxException e) {
            if (properties.getVersion() >= 9.0) {
                fail("Unexpected CheckmarxException");
            }
        }
    }


    @Test
    public void getLdapRoleMapId() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            Integer roleId = service.getRoleId("CX_USER");
            Integer id = service.getLdapRoleMapId(ldapId, roleId, "CN=CX_USERS,CN=Users,DC=cx,DC=local");
            assertNotNull(id);
        } catch (CheckmarxException e) {
            if(properties.getVersion() >= 9.0 ) {
                fail("Unexpected CheckmarxException");
            }
        }
    }

    @Test
    public void addTeamLdapMapping() {
        try {
            Integer ldapId = service.getLdapServerId("cx.local");
            String teamId = service.getTeamId(properties.getTeam());
            service.mapTeamLdap(ldapId, teamId, "", "CN=CX_USERS,CN=Users,DC=cx,DC=local");
            //Integer id = service.getLdapRoleMapId(ldapId, "CN=CX_USERS,CN=Users,DC=cx,DC=local");
        } catch (CheckmarxException e) {
            if (properties.getVersion() >= 9.0) {
                fail("Unexpected CheckmarxException");
            }
        }
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