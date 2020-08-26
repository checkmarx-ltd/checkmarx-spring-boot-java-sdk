package com.cx.restclient;

import com.checkmarx.sdk.config.ScaConfig;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.ast.dto.sca.AstScaResults;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScaClientImpl extends AbstractAstClient {
    private final ScaProperties scaProperties;

    @Override
    protected void applyScaResultsFilters(ASTResultsWrapper combinedResults, ScanParams scanParams) {

        SCAResults scaResults = combinedResults.getScaResults();
        List<String> filterSeverityFromRequest = null;
        Double filterScoreFromRequest = null;

        if (Optional.ofNullable(scanParams.getScaConfig()).isPresent()) {
            filterSeverityFromRequest = scanParams.getScaConfig().getFilterSeverity();
            filterScoreFromRequest = scanParams.getScaConfig().getFilterScore();
        }

        List<String> appliedFilterSeverity;
        appliedFilterSeverity = getFilterSeverity(filterSeverityFromRequest);

        if (appliedFilterSeverity != null && !Objects.requireNonNull(appliedFilterSeverity).isEmpty()) {
            filterResultsBySeverity(scaResults, appliedFilterSeverity);
        }

        Double appliedFilterScore;
        appliedFilterScore = getFilterScore(filterScoreFromRequest);

        if (isNotEmptyDouble(appliedFilterScore)) {
            filterResultsByScore(scaResults, appliedFilterScore);
        } else  {
            log.info("CxSCA filter score is not defined");
        }
    }

    private Double getFilterScore(Double filterScoreFromRequest) {
        Double appliedFilterScore;

        if (isNotEmptyDouble(filterScoreFromRequest)) {
            appliedFilterScore = filterScoreFromRequest;
        } else {
            appliedFilterScore = scaProperties.getFilterScore();
        }
        return appliedFilterScore;
    }

    private List<String> getFilterSeverity(List<String> filterSeverityFromRequest) {
        List<String> appliedFilterSeverity;

        if (CollectionUtils.isNotEmpty(filterSeverityFromRequest)) {
            appliedFilterSeverity = filterSeverityFromRequest;
        } else {
            appliedFilterSeverity = scaProperties.getFilterSeverity();
        }
        return appliedFilterSeverity;
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

    @Override
    public ASTResultsWrapper getLatestScanResults(ScanParams scanParams) {
        CxScanConfig commonClientScanConfig = getScanConfig(scanParams);
        try {
            CxClientDelegator client = new CxClientDelegator(commonClientScanConfig, log);
            client.init();
            ScanResults commonClientResults = client.getLatestScanResults();
            ASTResultsWrapper result;
            if (commonClientResults.getScaResults() != null) {
                result = toResults(commonClientResults);
                applyScaResultsFilters(result, scanParams);
            } else {
                result = new ASTResultsWrapper();
            }
            return result;
        } catch (Exception e) {
            throw new ASTRuntimeException("Error getting latest scan results.", e);
        }
    }

    /**
     * Convert scaParams to an object that is used by underlying logic in Common Client.
     */
    @Override
    protected CxScanConfig getScanConfig(ScanParams scanParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.addScannerType(ScannerType.AST_SCA);
        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scanParams.getProjectName());

        AstScaConfig scaConfig = getScaSpecificConfig(scanParams);
        setSourceLocation(scanParams, cxScanConfig, scaConfig);

        cxScanConfig.setAstScaConfig(scaConfig);

        return cxScanConfig;
    }

    private AstScaConfig getScaSpecificConfig(ScanParams scanParams) {
        AstScaConfig scaConfig = new AstScaConfig();

        String appUrlFromRequest = null;
        String apiUrlFromRequest = null;
        String accessControlUrlFromRequest = null;
        String tenantFromRequest = null;

        if (Optional.ofNullable(scanParams.getScaConfig()).isPresent()) {
            appUrlFromRequest = scanParams.getScaConfig().getAppUrl();
            apiUrlFromRequest = scanParams.getScaConfig().getApiUrl();
            accessControlUrlFromRequest = scanParams.getScaConfig().getAccessControlUrl();
            tenantFromRequest = scanParams.getScaConfig().getTenant();
        }

        scaConfig.setWebAppUrl(Optional.ofNullable(appUrlFromRequest).orElse(scaProperties.getAppUrl()));
        scaConfig.setApiUrl(Optional.ofNullable(apiUrlFromRequest).orElse(scaProperties.getApiUrl()));
        scaConfig.setAccessControlUrl(Optional.ofNullable(accessControlUrlFromRequest).orElse(scaProperties.getAccessControlUrl()));
        scaConfig.setTenant(Optional.ofNullable(tenantFromRequest).orElse(scaProperties.getTenant()));

        scaConfig.setUsername(scaProperties.getUsername());
        scaConfig.setPassword(scaProperties.getPassword());

        return scaConfig;
    }

    @Override
    protected void validate(ScanParams scaParams) {
        validateNotNull(scaParams);

        ScaConfig scaConfig = scaParams.getScaConfig();
        if (Optional.ofNullable(scaConfig).isPresent()) {
            validateNotEmpty(scaConfig.getAppUrl(), "SCA application URL");
            validateNotEmpty(scaConfig.getApiUrl(), "SCA API URL");
            validateNotEmpty(scaConfig.getAccessControlUrl(), "SCA Access Control URL");
            validateNotEmpty(scaConfig.getTenant(), "SCA tenant");
        } else {
            validateNotEmpty(scaProperties.getAppUrl(), "SCA application URL");
            validateNotEmpty(scaProperties.getApiUrl(), "SCA API URL");
            validateNotEmpty(scaProperties.getAccessControlUrl(), "SCA Access Control URL");
            validateNotEmpty(scaParams.getProjectName(), "Project name");
            validateNotEmpty(scaProperties.getTenant(), "SCA tenant");
            validateNotEmpty(scaProperties.getUsername(), "Username");
            validateNotEmpty(scaProperties.getPassword(), "Password");
        }
    }

    private static void validateNotNull(ScanParams scanParams) {
        if (scanParams == null) {
            throw new ASTRuntimeException(String.format("%s SCA parameters weren't provided.", ERROR_PREFIX));
        }

        if (scanParams.getRemoteRepoUrl() == null && !localSourcesAreSpecified(scanParams)) {
            String message = String.format("%s Source location is not specified. Please specify either " +
                    "repository URL, zip file path or source directory path.", ERROR_PREFIX);

            throw new ASTRuntimeException(message);
        }

        validateSpecifiedPathExists(scanParams.getZipPath());
        validateSpecifiedPathExists(scanParams.getSourceDir());
    }

    private static void validateSpecifiedPathExists(String path) {
        if (StringUtils.isNotEmpty(path) && !new File(path).exists()) {
            throw new ASTRuntimeException(String.format("%s Source location (%s) does not exist.", ERROR_PREFIX, path));
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

    private boolean isNotEmptyDouble(Double d) {
        return (d != null && d >= 0.0);
    }
}