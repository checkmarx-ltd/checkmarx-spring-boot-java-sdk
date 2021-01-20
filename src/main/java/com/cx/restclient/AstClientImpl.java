package com.cx.restclient;

import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.dto.ast.ASTResults;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.ast.dto.common.SummaryResults;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.ScannerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AstClientImpl extends AbstractAstClient {
    private final AstProperties astProperties;

    @Override
    protected void applyFilterToResults(ASTResultsWrapper combinedResults, ScanParams scanParams) {
        //currently do nothing
    }

    /**
     * Convert Common Client representation of AST results into an object from this SDK.
     */
    @Override
    protected ASTResultsWrapper toResults(CommonScanResults scanResults) {
        validateNotNull(scanResults.getAstResults());

        ModelMapper mapper = new ModelMapper();
        ASTResults astResults = mapper.map(scanResults.getAstResults(), ASTResults.class);

        return new ASTResultsWrapper(new SCAResults(), astResults);
    }

    @Override
    public ASTResultsWrapper getLatestScanResults(ScanParams scanParams) {
        log.warn("Getting latest AST scan results is not implemented yet.");
        return new ASTResultsWrapper();
    }

    @Override
    protected void validateResults(CommonScanResults results) {
        if (results != null && results.getAstResults() != null && results.getAstResults().getException() != null) {
            throw new ASTRuntimeException(results.getAstResults().getException().getMessage());
        }
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
    protected CxScanConfig getScanConfig(ScanParams scanParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.addScannerType(ScannerType.AST_SAST);
        //cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scanParams.getProjectName());

        AstSastConfig astConfig = getAstSpecificConfig();
        setSourceLocation(scanParams, cxScanConfig, astConfig);
        cxScanConfig.setAstSastConfig(astConfig);

        return cxScanConfig;
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