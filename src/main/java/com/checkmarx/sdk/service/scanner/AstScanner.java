package com.checkmarx.sdk.service.scanner;

import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.AstScaResults;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.checkmarx.sdk.utils.scanner.client.AstClientHelper;
import com.checkmarx.sdk.dto.SummaryResults;
import com.checkmarx.sdk.dto.ast.AstConfig;
import com.checkmarx.sdk.dto.ast.AstSastResults;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.ResultsBase;
import com.checkmarx.sdk.utils.scanner.client.IScanClientHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AstScanner extends AbstractScanner {
    private final AstProperties astProperties;

    @Override
    protected void applyFilterToResults(AstScaResults combinedResults, ScanParams scanParams) {
        //currently do nothing
    }

    /**
     * Convert Common Client representation of AST results into an object from this SDK.
     */
    @Override
    protected AstScaResults toResults(ResultsBase scanResults) {
        ASTResults astResults = new ASTResults((AstSastResults) scanResults);
        
        validateNotNull(astResults.getResults());

        return new AstScaResults(new SCAResults(), astResults);
    }

    @Override
    protected IScanClientHelper allocateClient(RestClientConfig restClientConfig) {
        return new AstClientHelper(restClientConfig, log);
    }
    
    
    private void validateNotNull(AstSastResults astResults) {
        if (astResults == null) {
            throw new ASTRuntimeException("AST results are missing.");
        }

        SummaryResults summary = astResults.getSummary();
        if (summary == null) {
            throw new ASTRuntimeException("AST results don't contain a summary.");
        }
    }

    /**
     * Convert scanParams to an object that is used by underlying logic in Common Client.
     */
    @Override
    protected RestClientConfig getScanConfig(ScanParams scanParams) {
        RestClientConfig restClientConfig = new RestClientConfig();

        restClientConfig.setProjectName(scanParams.getProjectName());

        AstConfig astConfig = getAstSpecificConfig();
        setSourceLocation(scanParams, restClientConfig, astConfig);
        restClientConfig.setAstConfig(astConfig);

        return restClientConfig;
    }

    private AstConfig getAstSpecificConfig() {
        return AstConfig.builder()
                .apiUrl(astProperties.getApiUrl())
                .webAppUrl(astProperties.getWebAppUrl())
                .clientId(astProperties.getClientId())
                .clientSecret(astProperties.getClientSecret())
                .presetName(astProperties.getPreset())
                .incremental(Boolean.parseBoolean(astProperties.getIncremental()))
                .build();
    }

    @Override
    protected void validateScanParams(ScanParams scanParams) {
        if (scanParams == null) {
            throw new ASTRuntimeException(String.format("%s Scan parameters weren't provided.", ERROR_PREFIX));
        }
        validateNotEmpty(astProperties.getApiUrl(), "AST API URL");
        validateNotEmpty(astProperties.getClientId(), "AST client ID");
        validateNotEmpty(astProperties.getClientSecret(), "AST client secret");
        validateNotEmpty(astProperties.getPreset(), "AST preset");
        validateNotEmpty(astProperties.getIncremental(), "Is Incremental flag");
    }

    @Override
    public AstScaResults getLatestScanResults(ScanParams scanParams) {
        log.warn("Getting latest AST scan results is not implemented yet.");
        return new AstScaResults();
    }
}