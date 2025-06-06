package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.dto.*;
import com.checkmarx.sdk.dto.sca.Summary;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.dto.filtering.EngineFilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterConfiguration;
import com.checkmarx.sdk.dto.filtering.FilterInput;
import com.checkmarx.sdk.dto.sca.SCAResults;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.service.FilterInputFactory;
import com.checkmarx.sdk.service.FilterValidator;
import com.checkmarx.sdk.utils.scanner.client.IScanClientHelper;
import com.checkmarx.sdk.utils.scanner.client.ScaClientHelper;
import com.checkmarx.sdk.dto.sca.ScaConfig;
import com.checkmarx.sdk.dto.sca.report.Finding;
import com.checkmarx.sdk.config.RestClientConfig;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class ScaScanner extends AbstractScanner {
    private final ScaProperties scaProperties;
    private final FilterInputFactory filterInputFactory;
    private final FilterValidator filterValidator;
    private final CxProperties cxProperties;

    @Override
    protected void applyFilterToResults(AstScaResults combinedResults, ScanParams scanParams) {
        EngineFilterConfiguration filterConfig = extractFilterConfigFrom(scanParams);

        List<Finding> findingsToRetain = new ArrayList<>();
        if (scaProperties.isFilterOutDevdependency() && !scaProperties.isFilterOutInDirectDependency()) {
            List<String> packageIds = new ArrayList<>();
            combinedResults.getScaResults()
                    .getPackages().forEach(packages -> {
                        if (packages.isIsDevelopmentDependency() || packages.isIsTestDependency()) {
                            packageIds.add(packages.getId());
                        }
                    });

            combinedResults.getScaResults()
                    .getFindings().forEach(finding -> {
                        if (passesFilter(finding, filterConfig) && !packageIds.contains(finding.getPackageId())) {
                            findingsToRetain.add(finding);
                        }
                    });

        }else if(scaProperties.isFilterOutInDirectDependency() && !scaProperties.isFilterOutDevdependency()){
            List<String> packageIds = new ArrayList<>();
            combinedResults.getScaResults()
                    .getPackages().forEach(packages -> {
                        if (packages.isIsDirectDependency()) {
                            packageIds.add(packages.getId());
                        }
                    });
            //Adding condition for only directdependency.
            combinedResults.getScaResults()
                    .getFindings().forEach(finding -> {
                        if (passesFilter(finding, filterConfig) && packageIds.contains(finding.getPackageId())) {
                            findingsToRetain.add(finding);
                        }
                    });
        }else if(scaProperties.isFilterOutInDirectDependency() && scaProperties.isFilterOutDevdependency()){
            List<String> packageIds = new ArrayList<>();
            combinedResults.getScaResults()
                    .getPackages().forEach(packages -> {
//                        if (packages.isIsDirectDependency() && !(packages.isIsDevelopmentDependency() || packages.isIsTestDependency())) {
//                            packageIds.add(packages.getId());
//                        }
                        if (!packages.isIsDirectDependency() || (packages.isIsDevelopmentDependency() || packages.isIsTestDependency())) {
                            packageIds.add(packages.getId());
                        }
                    });

            combinedResults.getScaResults()
                    .getFindings().forEach(finding -> {
                        if (passesFilter(finding, filterConfig) && !packageIds.contains(finding.getPackageId())) {
                            findingsToRetain.add(finding);
                        }
                    });
        }else{
            combinedResults.getScaResults()
                    .getFindings().forEach(finding -> {
                        if(passesFilter(finding, filterConfig)){
                            findingsToRetain.add(finding);
                        }
                    });
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(findingsToRetain);
        log.debug(jsonString);
        // Write JSON string to file
        try (FileWriter writer = new FileWriter("findingsToRetain.json")) {
            writer.write(jsonString);
            writer.flush();
            System.out.println("JSON written to file successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        combinedResults.getScaResults().setFindings(findingsToRetain);

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

        SCAResults scaResults = (SCAResults)scanResults;
        validateNotNull(scaResults);

        AstScaResults results = new AstScaResults();
        results.setScaResults(scaResults);
        return results;
    }

    @Override
    protected IScanClientHelper allocateClient(RestClientConfig restClientConfig) {
        return new ScaClientHelper(restClientConfig, log, scaProperties, cxProperties);
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
            throw new ScannerRuntimeException("Error getting latest scan results.", e);
        }
    }

    /**
     * Convert scanParams to an object that is used by underlying logic in Common Client.
     */
    @Override
    public RestClientConfig getScanConfig(ScanParams scanParams) {
        RestClientConfig restClientConfig = new RestClientConfig();
        restClientConfig.setProjectName(scanParams.getProjectName());
        restClientConfig.setDisableCertificateValidation(scanParams.isDisableCertificateValidation());

        ScaConfig scaConfig = getScaSpecificConfig(scanParams);
        setSourceLocation(scanParams, restClientConfig, scaConfig);
        if(scanParams.getRemoteRepoUrl() != null){
            restClientConfig.setClonedRepo(true);
        }
        restClientConfig.setScaConfig(scaConfig);

        return restClientConfig;
    }

    private ScaConfig getScaSpecificConfig(ScanParams scanParams) {
        ScaConfig scaConfig = new ScaConfig();
        com.checkmarx.sdk.config.ScaConfig sdkScaConfig = scanParams.getScaConfig();
        if (sdkScaConfig != null) {
            scaConfig.setWebAppUrl(sdkScaConfig.getAppUrl());
            scaConfig.setApiUrl(sdkScaConfig.getApiUrl());
            scaConfig.setAccessControlUrl(sdkScaConfig.getAccessControlUrl());
            scaConfig.setTenant(sdkScaConfig.getTenant());
            scaConfig.setIncludeSources(sdkScaConfig.isIncludeSources());
            scaConfig.setExcludeFiles(sdkScaConfig.getExcludeFiles());
            scaConfig.setUsername(scaProperties.getUsername());
            scaConfig.setPassword(scaProperties.getPassword());
            if(sdkScaConfig.getFingerprintsIncludePattern()!=null)
            {
                scaConfig.setFingerprintsIncludePattern(sdkScaConfig.getFingerprintsIncludePattern());
            }
            else{
                scaConfig.setFingerprintsIncludePattern(scaProperties.getFingerprintsIncludePattern());
            }
            if(sdkScaConfig.getManifestsIncludePattern()!=null)
            {
                scaConfig.setManifestsIncludePattern(sdkScaConfig.getManifestsIncludePattern());
            }
            else{
                scaConfig.setManifestsIncludePattern(scaProperties.getManifestsIncludePattern());
            }
            scaConfig.setProjectTags(sdkScaConfig.getProjectTags());
            scaConfig.setScanTags(sdkScaConfig.getScanTags());
            scaConfig.setTeam(sdkScaConfig.getTeam());
            scaConfig.setScanTimeout(sdkScaConfig.getScanTimeout());
            scaConfig.setExpPathSastProjectName(sdkScaConfig.getExpPathSastProjectName());
            String zipPath = scanParams.getZipPath();
            if (StringUtils.isNotEmpty(zipPath)) {
                scaConfig.setZipFilePath(zipPath);
            }

        } else {
            log.warn("Unable to map SCA configuration to an internal object.");
        }
        return scaConfig;
    }

    @Override
    protected void validateScanParams(ScanParams scanParams) {
        validateNotNull(scanParams);

        com.checkmarx.sdk.config.ScaConfig scaConfig = scanParams.getScaConfig();
        if (scaConfig != null) {
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
            throw new ScannerRuntimeException(String.format("%s SCA parameters weren't provided.", ERROR_PREFIX));
        }

        if (scanParams.getRemoteRepoUrl() == null && !localSourcesAreSpecified(scanParams)) {
            String message = String.format("%s Source location is not specified. Please specify either " +
                    "repository URL, zip file path or source directory path.", ERROR_PREFIX);

            throw new ScannerRuntimeException(message);
        }

        validateSpecifiedPathExists(scanParams.getZipPath());
        validateSpecifiedPathExists(scanParams.getSourceDir());
    }

    private static void validateSpecifiedPathExists(String path) {
        if (StringUtils.isNotEmpty(path) && !new File(path).exists()) {
            throw new ScannerRuntimeException(String.format("%s Source location (%s) does not exist.", ERROR_PREFIX, path));
        }
    }

    private void validateNotNull(SCAResults scaResults) {
        if (scaResults == null) {
            throw new ScannerRuntimeException("SCA results are missing.");
        }

        Summary summary = scaResults.getSummary();
        if (summary == null) {
            throw new ScannerRuntimeException("SCA results don't contain a summary.");
        }
    }

    protected void setRemoteBranch(ScanParams scanParams, RemoteRepositoryInfo remoteRepoInfo) {
        //branches are supported for SCA
        remoteRepoInfo.setBranch(scanParams.getBranch());
    }
}