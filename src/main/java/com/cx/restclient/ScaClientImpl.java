package com.cx.restclient;

import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.sca.SCAParams;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.exception.SCARuntimeException;
import com.checkmarx.sdk.service.ScaClient;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.DependencyScanResults;
import com.cx.restclient.dto.DependencyScannerType;
import com.cx.restclient.sca.dto.RemoteRepositoryInfo;
import com.cx.restclient.sca.dto.SCAConfig;
import com.cx.restclient.sca.dto.SCASummaryResults;
import com.cx.restclient.sca.dto.SourceLocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScaClientImpl implements ScaClient {
    private static final String ERROR_PREFIX = "SCA scan cannot be initiated.";

    private final ScaProperties scaProperties;

    @Override
    public SCAResults scanRemoteRepo(SCAParams scaParams) throws IOException {
        validate(scaParams);

        // remoteRepoUrl may contain a userinfo part (username:password). However, URL.toString() result doesn't include
        // userinfo, so this sensitive data won't be logged.
        log.info("Creating new SCA scan for project '{}' with remote repository URL: {}",
                scaParams.getProjectName(), getSafeRemoteRepoUrl(scaParams.getRemoteRepoUrl()));

        CxScanConfig scanConfig = getScanConfig(scaParams);

        DependencyScanResults scanResults = executeScan(scanConfig);

        return toScaResults(scanResults.getScaResults());
    }

    /**
     * Convert Common Client representation of SCA results into an object from this SDK.
     */
    private SCAResults toScaResults(com.cx.restclient.sca.dto.SCAResults scaResults) {
        validateNotNull(scaResults);

        SCASummaryResults summary = scaResults.getSummary();
        Map<Filter.Severity, Integer> findingCountsPerSeverity = getFindingCountMap(summary);

        return SCAResults.builder()
                .webReportLink(scaResults.getWebReportLink())
                .scanId(scaResults.getScanId())
                .totalPackages(summary.getTotalPackages())
                .directPackages(summary.getDirectPackages())
                .totalOutdatedPackages(summary.getTotalOutdatedPackages())
                .riskScore(summary.getRiskScore())
                .findingCounts(findingCountsPerSeverity)
                .build();
    }

    private Map<Filter.Severity, Integer> getFindingCountMap(SCASummaryResults summary) {
        EnumMap<Filter.Severity, Integer> result = new EnumMap<>(Filter.Severity.class);
        result.put(Filter.Severity.HIGH, summary.getHighVulnerabilityCount());
        result.put(Filter.Severity.MEDIUM, summary.getMediumVulnerabilityCount());
        result.put(Filter.Severity.LOW, summary.getLowVulnerabilityCount());
        return result;
    }

    /**
     * Convert scaParams to an object that is used by underlying logic in Common Client.
     */
    private CxScanConfig getScanConfig(SCAParams scaParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.setDependencyScannerType(DependencyScannerType.SCA);
        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scaParams.getProjectName());
        cxScanConfig.setScaConfig(getSCAConfig(scaParams));

        return cxScanConfig;
    }

    private SCAConfig getSCAConfig(SCAParams scaParams) {
        SCAConfig scaConfig = new SCAConfig();
        scaConfig.setWebAppUrl(scaProperties.getAppUrl());
        scaConfig.setApiUrl(scaProperties.getApiUrl());
        scaConfig.setAccessControlUrl(scaProperties.getAccessControlUrl());
        scaConfig.setTenant(scaProperties.getTenant());
        scaConfig.setUsername(scaProperties.getUsername());
        scaConfig.setPassword(scaProperties.getPassword());
        scaConfig.setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);

        RemoteRepositoryInfo remoteRepoInfo = new RemoteRepositoryInfo();
        remoteRepoInfo.setUrl(scaParams.getRemoteRepoUrl());
        scaConfig.setRemoteRepositoryInfo(remoteRepoInfo);

        return scaConfig;
    }

    private DependencyScanResults executeScan(CxScanConfig cxScanConfig) throws IOException {
        CxShragaClient client = new CxShragaClient(cxScanConfig, log);
        client.init();
        client.createDependencyScan();

        return client.waitForDependencyScanResults();
    }

    private void validate(SCAParams scaParams) {
        validateNotNull(scaParams);
        validateNotEmpty(scaProperties.getAppUrl(), "SCA application URL");
        validateNotEmpty(scaProperties.getApiUrl(), "SCA API URL");
        validateNotEmpty(scaProperties.getAccessControlUrl(), "SCA Access Control URL");
        validateNotEmpty(scaParams.getProjectName(), "Project name");
        validateNotEmpty(scaProperties.getTenant(), "SCA tenant");
        validateNotEmpty(scaProperties.getUsername(), "Username");
        validateNotEmpty(scaProperties.getPassword(), "Password");
    }

    private void validateNotNull(SCAParams scaParams) {
        if (scaParams == null) {
            throw new SCARuntimeException(String.format("%s SCA parameters weren't provided.", ERROR_PREFIX));
        }

        if (scaParams.getRemoteRepoUrl() == null) {
            throw new SCARuntimeException(String.format("%s Repository URL wasn't provided.", ERROR_PREFIX));
        }
    }

    private void validateNotNull(com.cx.restclient.sca.dto.SCAResults scaResults) {
        if (scaResults == null) {
            throw new SCARuntimeException("SCA results are missing.");
        }

        SCASummaryResults summary = scaResults.getSummary();
        if (summary == null) {
            throw new SCARuntimeException("SCA results don't contain a summary.");
        }
    }

    private void validateNotEmpty(String parameter, String parameterDescr) {
        if (StringUtils.isEmpty(parameter)) {
            String message = String.format("%s %s wasn't provided", ERROR_PREFIX, parameterDescr);
            throw new SCARuntimeException(message);
        }
    }

    private String getSafeRemoteRepoUrl(URL url) throws MalformedURLException {
        return new URL(url.getProtocol(), url.getHost(), url.getFile()).toString();
    }
}