package com.cx.restclient;

import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.AstRestClient;
import com.cx.restclient.ast.dto.common.SummaryResults;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.configuration.RestClientConfig;
import com.cx.restclient.dto.IResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AstScanner extends AbstractScanner {
    private final AstProperties astProperties;

    @Override
    protected void applyFilterToResults(ASTResultsWrapper combinedResults, ScanParams scanParams) {
        //currently do nothing
    }

    /**
     * Convert Common Client representation of AST results into an object from this SDK.
     */
    @Override
    protected ASTResultsWrapper toResults(IResults scanResults) {
        ASTResults astResults = new ASTResults((AstSastResults) scanResults);
        
        validateNotNull(astResults.getResults());

        return new ASTResultsWrapper(new SCAResults(), astResults);
    }

    @Override
    protected IRestClient allocateClient(RestClientConfig restClientConfig) {
        return new AstRestClient(restClientConfig, log);
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

        AstSastConfig astConfig = getAstSpecificConfig();
        setSourceLocation(scanParams, restClientConfig, astConfig);
        restClientConfig.setAstSastConfig(astConfig);

        return restClientConfig;
    }

    private AstSastConfig getAstSpecificConfig() {
        return AstSastConfig.builder()
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
}