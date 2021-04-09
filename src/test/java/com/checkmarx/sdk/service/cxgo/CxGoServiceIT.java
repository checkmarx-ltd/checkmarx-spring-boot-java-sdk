package com.checkmarx.sdk.service.cxgo;

import com.checkmarx.sdk.config.SpringConfiguration;
import com.checkmarx.sdk.config.CxGoProperties;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.InvalidCredentialsException;

import com.checkmarx.sdk.service.CxGoAuthService;
import com.checkmarx.sdk.utils.CxRepoFileHelper;

import com.checkmarx.sdk.service.scanner.GoScanner;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Import(SpringConfiguration.class)
@SpringBootTest
public class CxGoServiceIT {

    private static final String GO_PROJECT_NAME = "SdkCI";
    @Autowired
    private CxGoProperties properties;
    @Autowired
    private GoScanner service;

    CxRepoFileHelper repoFileService = new CxRepoFileHelper(properties);
    
    @Autowired
    private CxGoAuthService authService;
    private HttpHeaders token;

    @Test
    public void login() {
        if(token ==null) {
            try {
                if (StringUtils.isNotEmpty(properties.getClientSecret())) {
                    token = authService.createAuthHeaders();
                    assertNotNull(token);
                }
            } catch (InvalidCredentialsException e) {
                fail("Unexpected InvalidCredentialsException");
            }
        }
    }

    @Test
    public void getTeams() {
        login();
        try {
            if(StringUtils.isNotEmpty(properties.getClientSecret())) {
                String teamId = service.getTeamId(properties.getTeam());
                assertNotNull(teamId);
            }
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getProject() {
        login();
        try {
            if(StringUtils.isNotEmpty(properties.getClientSecret())) {
                String teamId = service.getTeamId(properties.getTeam());
                Integer projId = service.getProjectId(teamId, "CircleCI");
                if (projId == -1) {
                    String projIdStr = service.createCxGoProject(teamId, "CircleCI", properties.getScanPreset());
                    projId = Integer.parseInt(projIdStr);
                }
                assertNotNull(projId);
            }
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    @Ignore //works only on Windows
    public void gitClone() throws CheckmarxException {
        login();
        CxScanParams params = new CxScanParams();
        params.setProjectName("CircleCI");
        params.setTeamId("1");
        params.setGitUrl("https://github.com/Custodela/Riches.git");
        params.setBranch("refs/heads/master");
        params.setSourceType(CxScanParams.Type.GIT);
        String zipFilePath = repoFileService.prepareRepoFile(params);
        assertTrue("Zip file path is empty.", StringUtils.isNotEmpty(zipFilePath));
    }
    
    
    @Test
    @Ignore //works only on Windows
    public void completeScanFlow() throws CheckmarxException {
        login();
        if(StringUtils.isNotEmpty(properties.getClientSecret())) {
            String teamId = service.getTeamId(properties.getTeam());
            Integer projectId = service.getProjectId(teamId, GO_PROJECT_NAME);
            CxScanParams params = new CxScanParams();
            params.setProjectName(GO_PROJECT_NAME);
            params.setTeamId(teamId);
            params.setProjectId(projectId);
            params.setGitUrl("https://github.com/Custodela/Riches.git");
            params.setBranch("refs/heads/master");
            params.setSourceType(CxScanParams.Type.GIT);
            //run the scan and wait for it to finish
            Integer x = service.createScan(params, "CxFlow Scan");
            service.waitForScanCompletion(x);
            FilterConfiguration filterConfiguration = FilterConfiguration.fromSimpleFilters(
                    Collections.singletonList(new Filter(Filter.Type.SEVERITY, "High")));
            //generate the results
            ScanResults results = service.getReportContentByScanId(x, filterConfiguration);
            assertNotNull(results);
        }
    }

}