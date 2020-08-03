package com.cx.restclient;

import com.checkmarx.sdk.config.AstProperties;
import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.SCAResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ASTRuntimeException;


import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.common.SummaryResults;
import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sast.AstSastResults;
import com.cx.restclient.ast.dto.sast.report.AstSastSummaryResults;
import com.cx.restclient.configuration.CxScanConfig;
import com.checkmarx.sdk.dto.ast.ASTResults;

import com.cx.restclient.dto.ScanResults;
import com.cx.restclient.dto.ScannerType;


import com.cx.restclient.dto.SourceLocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AstClientImpl extends AbstractClientImpl {
    private static final String ERROR_PREFIX = "SCA scan cannot be initiated.";

    private final AstProperties astProperties;


    protected void applyScaResultsFilters(ASTResultsWrapper combinedResults, ScanParams scanParams) {
      //currently do nothing
    }

  
    /**
     * Convert Common Client representation of SCA results into an object from this SDK.
     */

    @Override
    protected ASTResultsWrapper toResults(ScanResults scanResults) {
        validateNotNull(scanResults.getAstResults());

        AstSastSummaryResults summary = scanResults.getAstResults().getSummary();
 
        ModelMapper mapper = new ModelMapper();
        ASTResults astResults = mapper.map(scanResults.getAstResults(), ASTResults.class);
        
        return new ASTResultsWrapper(new SCAResults(), astResults);
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
     * Convert scaParams to an object that is used by underlying logic in Common Client.
     */
    protected CxScanConfig getScanConfig(ScanParams scanParams) {
        CxScanConfig cxScanConfig = new CxScanConfig();
        cxScanConfig.addScannerType(ScannerType.AST_SAST);
        cxScanConfig.setSastEnabled(false);
        cxScanConfig.setProjectName(scanParams.getProjectName());
        cxScanConfig.setAstSastConfig(getAstConfig(scanParams));

        return cxScanConfig;
    }

    private AstSastConfig getAstConfig(ScanParams scanParams) {
        AstSastConfig astConfig = new AstSastConfig();
        astConfig.setApiUrl(astProperties.getApiUrl());
        astConfig.setAccessToken(astProperties.getToken());
        astConfig.setPresetName(astProperties.getPreset());
        astConfig.setIncremental(StringUtils.isEmpty(astProperties.getIncremental()) ? false : Boolean.parseBoolean(astProperties.getIncremental()));

        if(scanParams.getZipPath() != null){
            astConfig.setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);
        }
        else{
            astConfig.setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
            RemoteRepositoryInfo remoteRepoInfo = new RemoteRepositoryInfo();
            remoteRepoInfo.setUrl(scanParams.getRemoteRepoUrl());
            astConfig.setRemoteRepositoryInfo(remoteRepoInfo);
        }
        
        return astConfig;
    }



    protected void validate(ScanParams scanParams) {
        if (scanParams == null) {
            throw new ASTRuntimeException(String.format("%s SCA parameters weren't provided.", ERROR_PREFIX));
        }
         validateNotEmpty(astProperties.getApiUrl(), "AST API URL");
        validateNotEmpty(astProperties.getToken(), "AST Access Token");
        validateNotEmpty(astProperties.getPreset(), "AST preset");
        validateNotEmpty(astProperties.getIncremental(), "Is Incremental flag");
    }






}