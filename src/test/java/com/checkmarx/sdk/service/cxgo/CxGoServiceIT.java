package com.checkmarx.sdk.service.cxgo;

import com.checkmarx.sdk.config.CxConfig;
import com.checkmarx.sdk.config.CxGoProperties;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ScanResults;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.cxgo.Scan;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.InvalidCredentialsException;

import com.checkmarx.sdk.service.CxGoAuthService;
import com.checkmarx.sdk.service.CxRepoFileService;

import com.cx.restclient.CxGoClientImpl;
import org.apache.commons.lang3.StringUtils;
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
@Import(CxConfig.class)
@SpringBootTest
public class CxGoServiceIT {

    @Autowired
    private CxGoProperties properties;
    @Autowired
    private CxGoClientImpl service;
    @Autowired
    private CxRepoFileService repoFileService;
    @Autowired
    private CxGoAuthService authService;

    @Test
    public void login() {
        try {
            HttpHeaders token = authService.createAuthHeaders();
            assertNotNull(token);
        }catch (InvalidCredentialsException e){
            fail("Unexpected InvalidCredentialsException");
        }
    }

    @Test
    public void getTeams() {
        try {
            String teamId = service.getTeamId(properties.getTeam());
            assertNotNull(teamId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void getProject() {
        try {
            String teamId = service.getTeamId(properties.getTeam());
            Integer projId = service.getProjectId(teamId, "CircleCI");
            if(projId == -1){
                String projIdStr = service.createCxGoProject(teamId, "CircleCI", "1,2,3,4,5,9");
                projId = Integer.parseInt(projIdStr);
            }
            assertNotNull(projId);
        }catch (CheckmarxException e){
            fail("Unexpected CheckmarxException");
        }
    }

    @Test
    public void gitClone() throws CheckmarxException {
        CxScanParams params = new CxScanParams();
        params.setProjectName("CircleCI");
        params.setTeamId("1");
        params.setGitUrl("https://github.com/Custodela/Riches.git");
        params.setBranch("refs/heads/master");
        params.setSourceType(CxScanParams.Type.GIT);
        String zipFilePath = repoFileService.prepareRepoFile(params);
        assertTrue("Zip file path is empty.", StringUtils.isNotEmpty(zipFilePath));
    }

//    @Test
//    public void getResults(){
//        Login();
//        FilterConfiguration filterConfiguration = FilterConfiguration.builder()
//                .simpleFilters(Collections.singletonList(new Filter(Filter.Type.SEVERITY, "High")))
//                .build();
//        //generate the results
//        try {
//            ScanResults results = service.getReportContentByScanId(92, filterConfiguration);
//        } catch (CheckmarxException e) {
//            e.printStackTrace();
//        }
//
//    }
    
    @Test
    public void completeScanFlow() throws CheckmarxException {
        String teamId = service.getTeamId(properties.getTeam());
        Integer projectId = service.getProjectId(teamId, "CircleCI");
        CxScanParams params = new CxScanParams();
        params.setProjectName("CircleCI");
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