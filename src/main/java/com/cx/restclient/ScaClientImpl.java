package com.cx.restclient;

import com.checkmarx.sdk.config.ScaConfig;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.Filter;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.filtering.EngineFilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.checkmarx.sdk.service.FilterInputFactory;
import com.checkmarx.sdk.service.FilterValidator;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.ast.dto.sca.AstScaResults;
import com.cx.restclient.ast.dto.sca.report.AstScaSummaryResults;
import com.cx.restclient.ast.dto.sca.report.Finding;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.ScannerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScaClientImpl extends AbstractAstClient {
    private final ScaProperties scaProperties;
    private final FilterInputFactory filterInputFactory;
    private final FilterValidator filterValidator;

    @Override
    protected void applyFilterToResults(ASTResultsWrapper combinedResults, ScanParams scanParams) {
        EngineFilterConfiguration filterConfig = extractFilterConfigFrom(scanParams);

        combinedResults.getScaResults()
                .getFindings()
                .removeIf(finding -> !passesFilter(finding, filterConfig));
    }

    private static EngineFilterConfiguration extractFilterConfigFrom(ScanParams scanParams) {
        EngineFilterConfiguration result = Optional.ofNullable(scanParams)
                .map(ScanParams::getFilterConfiguration)
                .map(FilterConfiguration::getScaFilters)
                .orElse(null);

        String message = (result == null ? "No SCA filter configuration was found in {}"
                : "Found SCA filter configuration in {}");

        log.debug(message, ScanParams.class.getSimpleName());

        return result;
    }

    private boolean passesFilter(Finding finding, EngineFilterConfiguration filterConfig) {
        FilterInput filterInput = filterInputFactory.createFilterInputForSca(finding);
        return filterValidator.passesFilter(filterInput, filterConfig);
    }

    /**
     * Convert Common Client representation of SCA results into an object from this SDK.
     */
    @Override
    protected ASTResultsWrapper toResults(CommonScanResults scaResultsFromCommonClient) {
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
    protected void validateResults(CommonScanResults results) {
        if (results!= null && results.getScaResults()!= null && results.getScaResults().getException() != null){
            throw new ASTRuntimeException(results.getScaResults().getException().getMessage() );
        }
    }

    @Override
    public ASTResultsWrapper getLatestScanResults(ScanParams scanParams) {
        CxScanConfig commonClientScanConfig = getScanConfig(scanParams);
        try {
            CxClientDelegator client = new CxClientDelegator(commonClientScanConfig, log);
            client.init();
            CommonScanResults commonClientResults = client.getLatestScanResults();
            ASTResultsWrapper result;
            if (commonClientResults.getScaResults() != null) {
                result = toResults(commonClientResults);
                applyFilterToResults(result, scanParams);
            } else {
                result = new ASTResultsWrapper();
            }
            return result;
        } catch (Exception e) {
            throw new ASTRuntimeException("Error getting latest scan results.", e);
        }
    }

    /**
     * Convert scanParams to an object that is used by underlying logic in Common Client.
     */
    @Override
    protected CxScanConfig getScanConfig(ScanParams scanParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.addScannerType(ScannerType.AST_SCA);
//        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scanParams.getProjectName());

        AstScaConfig scaConfig = getScaSpecificConfig(scanParams);
        setSourceLocation(scanParams, cxScanConfig, scaConfig);

        cxScanConfig.setAstScaConfig(scaConfig);

        return cxScanConfig;
    }

    private AstScaConfig getScaSpecificConfig(ScanParams scanParams) {
        AstScaConfig commonClientScaConfig = new AstScaConfig();
        ScaConfig sdkScaConfig = scanParams.getScaConfig();
        if (sdkScaConfig != null) {
            commonClientScaConfig.setWebAppUrl(sdkScaConfig.getAppUrl());
            commonClientScaConfig.setApiUrl(sdkScaConfig.getApiUrl());
            commonClientScaConfig.setAccessControlUrl(sdkScaConfig.getAccessControlUrl());
            commonClientScaConfig.setTenant(sdkScaConfig.getTenant());
            commonClientScaConfig.setUsername(scaProperties.getUsername());
            commonClientScaConfig.setPassword(scaProperties.getPassword());

            String zipPath = scanParams.getZipPath();
            if (StringUtils.isNotEmpty(zipPath)) {
                commonClientScaConfig.setZipFilePath(zipPath);
                commonClientScaConfig.setIncludeSources(true);
            }

        } else {
            log.warn("Unable to map SCA configuration to an internal object.");
        }
        return commonClientScaConfig;
    }

    @Override
    protected void validateScanParams(ScanParams scanParams) {
        validateNotNull(scanParams);

        ScaConfig scaConfig = scanParams.getScaConfig();
        if (Optional.ofNullable(scaConfig).isPresent()) {
            validateNotEmpty(scaConfig.getAppUrl(), "SCA application URL");
            validateNotEmpty(scaConfig.getApiUrl(), "SCA API URL");
            validateNotEmpty(scaConfig.getAccessControlUrl(), "SCA Access Control URL");
            validateNotEmpty(scaConfig.getTenant(), "SCA tenant");
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

}