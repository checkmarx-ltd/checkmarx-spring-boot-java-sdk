package com.cx.restclient;

import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.sca.SCAParams;
import com.checkmarx.sdk.exception.SCARuntimeException;
import com.checkmarx.sdk.service.ScaClient;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.DependencyScanResults;
import com.cx.restclient.dto.DependencyScannerType;
import com.cx.restclient.sca.dto.RemoteRepositoryInfo;
import com.cx.restclient.sca.dto.SCAConfig;
import com.cx.restclient.sca.dto.SourceLocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScaClientImpl implements ScaClient {

    private final ScaProperties scaProperties;

    @Override
    public DependencyScanResults createScanFromRemoteRepo(SCAParams scaParams) throws IOException {
        String projectName = scaParams.getProjectName();
        String remoteRepoUrl = scaParams.getRemoteRepoUrl();

        validateScaParameters(projectName, remoteRepoUrl);
        String remoteRepoSafeUrl = getRemoteRepoSafeUrl(remoteRepoUrl);

        log.info("Creating new SCA scan for project '{}' with remote repository URL: {}", projectName, remoteRepoSafeUrl);

        CxScanConfig cxScanConfig = initScaConfig(projectName);
        cxScanConfig.getScaConfig().setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
        RemoteRepositoryInfo remoteRepoInfo = createRemoteRepoInfo(remoteRepoUrl);
        cxScanConfig.getScaConfig().setRemoteRepositoryInfo(remoteRepoInfo);

        return createScanAndGetResults(cxScanConfig);
    }

    private CxScanConfig initScaConfig(String projectName) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.setDependencyScannerType(DependencyScannerType.SCA);
        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(projectName);
        cxScanConfig.setScaConfig(getSCAConfig());

        return cxScanConfig;
    }

    private SCAConfig getSCAConfig() {
        SCAConfig scaConfig = new SCAConfig();
        scaConfig.setWebAppUrl(scaProperties.getAppUrl());
        scaConfig.setApiUrl(scaProperties.getApiUrl());
        scaConfig.setAccessControlUrl(scaProperties.getAccessControlUrl());
        scaConfig.setTenant(scaProperties.getTenant());
        scaConfig.setUsername(scaProperties.getUsername());
        scaConfig.setPassword(scaProperties.getPassword());

        return scaConfig;
    }

    private RemoteRepositoryInfo createRemoteRepoInfo(String remoteRepoUrl) throws MalformedURLException {
        RemoteRepositoryInfo remoteRepositoryInfo = new RemoteRepositoryInfo();
        remoteRepositoryInfo.setUrl(new URL(remoteRepoUrl));

        return remoteRepositoryInfo;
    }

    private DependencyScanResults createScanAndGetResults(CxScanConfig cxScanConfig) throws IOException {
        CxShragaClient client = new CxShragaClient(cxScanConfig, log);
        client.init();
        client.createDependencyScan();

        return client.waitForDependencyScanResults();
    }

    private void validateScaParameters(String projectName, String remoteRepoUrl) {
        String scanCreationError = "SCA scan cannot be initiated";
        isEmptyParameterValidation(scaProperties.getAppUrl(), String.format("SCA application URL wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(scaProperties.getApiUrl(), String.format("SCA API URL wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(scaProperties.getAccessControlUrl(), String.format("SCA Access Control URL wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(projectName, String.format("Project name wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(remoteRepoUrl, String.format("Remote Repository URL wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(scaProperties.getTenant(), String.format("SCA tenant wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(scaProperties.getUsername(), String.format("Username wasn't provided. %s", scanCreationError));
        isEmptyParameterValidation(scaProperties.getPassword(), String.format("password wasn't provided. %s", scanCreationError));
    }

    private void isEmptyParameterValidation(String parameter, String messageError) {
        if (StringUtils.isEmpty(parameter)) {
            throw new SCARuntimeException(messageError);
        }
    }

    private String getRemoteRepoSafeUrl(String remoteRepoUrl) {
        /*
            Removes the attached personal access token in case it's a private remote repo case
         */
        String separator = "@";

        if (remoteRepoUrl.contains(separator)) {
            return "https://" + remoteRepoUrl.split(separator)[1];
        } else {
            return remoteRepoUrl;
        }
    }
}