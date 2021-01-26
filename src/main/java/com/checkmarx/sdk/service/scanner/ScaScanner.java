package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.config.ScaConfig;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.sast.Filter;
import com.checkmarx.sdk.dto.AstScaResults;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.filtering.EngineFilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.checkmarx.sdk.service.FilterInputFactory;
import com.checkmarx.sdk.service.FilterValidator;
import com.checkmarx.sdk.utils.scanner.client.IScanClientHelper;
import com.checkmarx.sdk.utils.scanner.client.ScaClientHelper;
import com.checkmarx.sdk.dto.sca.AstScaConfig;
import com.checkmarx.sdk.dto.sca.report.AstScaSummaryResults;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.ResultsBase;
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
public class ScaScanner extends AbstractScanner {
    private final ScaProperties scaProperties;
    private final FilterInputFactory filterInputFactory;
    private final FilterValidator filterValidator;

    @Override
    protected void applyFilterToResults(AstScaResults combinedResults, ScanParams scanParams) {
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
    protected AstScaResults toResults(ResultsBase scanResults) {
        
        com.checkmarx.sdk.dto.sca.AstScaResults scaResults = (com.checkmarx.sdk.dto.sca.AstScaResults)scanResults;
        validateNotNull(scaResults);

        AstScaSummaryResults summary = scaResults.getSummary();
        Map<Filter.Severity, Integer> findingCountsPerSeverity = getFindingCountMap(summary);

        ModelMapper mapper = new ModelMapper();
        SCAResults result = mapper.map(scaResults, SCAResults.class);
        result.getSummary().setFindingCounts(findingCountsPerSeverity);

        AstScaResults results = new AstScaResults();
        results.setScaResults(result);
        return results;
    }

    @Override
    protected IScanClientHelper allocateClient(RestClientConfig restClientConfig) {
        return new ScaClientHelper(restClientConfig, log);
    }
    
    public AstScaResults getLatestScanResults(ScanParams scanParams) {
        RestClientConfig config = getScanConfig(scanParams);
        try {
            IScanClientHelper client = allocateClient(config);
            client.init();
            ResultsBase results = client.getLatestScanResults();
            AstScaResults result;
            if (results != null) {
                result = toResults(results);
                applyFilterToResults(result, scanParams);
            } else {
                result = new AstScaResults();
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
    protected RestClientConfig getScanConfig(ScanParams scanParams) {
        RestClientConfig restClientConfig = new RestClientConfig();
        restClientConfig.setProjectName(scanParams.getProjectName());

        AstScaConfig scaConfig = getScaSpecificConfig(scanParams);
        setSourceLocation(scanParams, restClientConfig, scaConfig);

        restClientConfig.setAstScaConfig(scaConfig);

        return restClientConfig;
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

    private void validateNotNull(com.checkmarx.sdk.dto.sca.AstScaResults scaResults) {
        if (scaResults == null) {
            throw new ASTRuntimeException("SCA results are missing.");
        }

        AstScaSummaryResults summary = scaResults.getSummary();
        if (summary == null) {
            throw new ASTRuntimeException("SCA results don't contain a summary.");
        }
    }

}