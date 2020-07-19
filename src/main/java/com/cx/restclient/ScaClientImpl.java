package com.cx.restclient;

import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.ast.dto.sca.AstScaResults;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.configuration.CxScanConfig;

import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Iterator;
import java.util.EnumSet;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScaClientImpl extends AbstractClientImpl {


    private final ScaProperties scaProperties;


    protected void applyScaResultsFilters(ASTResultsWrapper combinedResults) {

        SCAResults scaResults = combinedResults.getScaResults();
        if (scaProperties.getFilterSeverity() != null && !Objects.requireNonNull(scaProperties.getFilterSeverity()).isEmpty()) {
            filterResultsBySeverity(scaResults, scaProperties.getFilterSeverity());
        }

        Double filterScore = scaProperties.getFilterScore();
        if (filterScore != null && filterScore >= 0.0) {
            filterResultsByScore(scaResults, filterScore);
        } else  {
            log.info("Cx-SCA filter score is not defined", filterScore); ;
        }
    }

    private void filterResultsBySeverity(SCAResults scaResults, List<String> filerSeverity) {
        List<String> validateFilterSeverity = validateFilterSeverity(filerSeverity);
        log.info("Applying Cx-SCA results filter severities: [{}]", validateFilterSeverity.toString());
        scaResults.getFindings().removeIf(finding -> (
                !StringUtils.containsIgnoreCase(validateFilterSeverity.toString(), finding.getSeverity().name())
                ));
    }

    private void filterResultsByScore(SCAResults scaResults, double score) {
        log.info("Applying Cx-SCA results filter score: [{}]", score);
        scaResults.getFindings().removeIf(finding -> (
                finding.getScore() < score
        ));
    }

    private List<String> validateFilterSeverity(List<String> filerSeverity) {
        Iterator<String> iterator = filerSeverity.iterator();
        while (iterator.hasNext()) {
            String nextFilter = iterator.next();
            if (!StringUtils.containsIgnoreCase(EnumSet.range(Filter.Severity.HIGH, Filter.Severity.LOW).toString(), nextFilter)) {
                log.warn("Severity: [{}] is not a supported filter", nextFilter);
                iterator.remove();
            }
        }
        return filerSeverity;
    }

    /**
     * Convert Common Client representation of SCA results into an object from this SDK.
     */

    @Override
    protected ASTResultsWrapper toResults(ScanResults scaResultsFromCommonClient) {
        validateNotNull(scaResultsFromCommonClient.getScaResults());

        AstScaSummaryResults summary = scaResultsFromCommonClient.getScaResults().getSummary();
        Map<Filter.Severity, Integer> findingCountsPerSeverity = getFindingCountMap(summary);

        ModelMapper mapper = new ModelMapper();
        SCAResults result = mapper.map(scaResultsFromCommonClient.getScaResults(), SCAResults.class);
        result.getSummary().setFindingCounts(findingCountsPerSeverity);

        ASTResultsWrapper results = new ASTResultsWrapper();
        results.setScaResults(result);
        return results;
    }



    /**
     * Convert scaParams to an object that is used by underlying logic in Common Client.
     */
    protected CxScanConfig getScanConfig(ScanParams scaParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.addScannerType(ScannerType.AST_SCA);
        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scaParams.getProjectName());
        cxScanConfig.setAstScaConfig(getSCAConfig(scaParams));

        return cxScanConfig;
    }

    private AstScaConfig getSCAConfig(ScanParams scanParams) {
        AstScaConfig scaConfig = new AstScaConfig();
        scaConfig.setWebAppUrl(scaProperties.getAppUrl());
        scaConfig.setApiUrl(scaProperties.getApiUrl());
        scaConfig.setAccessControlUrl(scaProperties.getAccessControlUrl());
        scaConfig.setTenant(scaProperties.getTenant());
        scaConfig.setUsername(scaProperties.getUsername());
        scaConfig.setPassword(scaProperties.getPassword());
        if(scanParams.getZipPath() != null){
            scaConfig.setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);
        }
        else{
            scaConfig.setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
            RemoteRepositoryInfo remoteRepoInfo = new RemoteRepositoryInfo();
            remoteRepoInfo.setUrl(scanParams.getRemoteRepoUrl());
            scaConfig.setRemoteRepositoryInfo(remoteRepoInfo);
        }

        return scaConfig;
    }



    protected void validate(ScanParams scaParams) {
        validateNotNull(scaParams);
        validateNotEmpty(scaProperties.getAppUrl(), "SCA application URL");
        validateNotEmpty(scaProperties.getApiUrl(), "SCA API URL");
        validateNotEmpty(scaProperties.getAccessControlUrl(), "SCA Access Control URL");
        validateNotEmpty(scaParams.getProjectName(), "Project name");
        validateNotEmpty(scaProperties.getTenant(), "SCA tenant");
        validateNotEmpty(scaProperties.getUsername(), "Username");
        validateNotEmpty(scaProperties.getPassword(), "Password");
    }

    private void validateNotNull(ScanParams scanParams) {
        if (scanParams == null) {
            throw new ASTRuntimeException(String.format("%s SCA parameters weren't provided.", ERROR_PREFIX));
        }

        if (scanParams.getRemoteRepoUrl() == null && scanParams.getZipPath() == null) {
            throw new ASTRuntimeException(String.format("%s Repository URL or Zip path wasn't provided.", ERROR_PREFIX));
        }

        if(!(new File(scanParams.getZipPath()).exists())){
            throw new ASTRuntimeException(String.format("%s file (%s) does not exist.", ERROR_PREFIX, scanParams.getZipPath()));
        }
    }

    private void validateNotNull(AstScaResults scaResults) {
        if (scaResults == null) {
            throw new ASTRuntimeException("SCA results are missing.");
        }

        AstScaSummaryResults summary = scaResults.getSummary();
        if (summary == null) {
            throw new ASTRuntimeException("SCA results don't contain a summary.");
        }
    }


}