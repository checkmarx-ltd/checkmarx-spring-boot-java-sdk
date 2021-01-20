package com.cx.restclient;

import com.cx.restclient.ast.AstSastClient;
import com.cx.restclient.ast.AstScaClient;
import com.cx.restclient.common.Scanner;
import com.cx.restclient.common.State;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.configuration.PropertyFileLoader;
import com.cx.restclient.dto.Results;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.ScannerType;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Galn on 05/02/2018.
 */

public class CxClientDelegator implements Scanner {
    private static final PropertyFileLoader properties = PropertyFileLoader.getDefaultInstance();

    private static final String PRINT_LINE = "-----------------------------------------------------------------------------------------";

    private Logger log;
    private CxScanConfig config;

    Map<ScannerType, Scanner> scannersMap = new EnumMap<>(ScannerType.class);

    public CxClientDelegator(CxScanConfig config, Logger log) throws MalformedURLException {

        this.config = config;
        this.log = log;
        if (config.isAstSastEnabled()) {
            scannersMap.put(ScannerType.AST_SAST, new AstSastClient(config, log));
        }
        
        if (config.isAstScaEnabled()) {
            scannersMap.put(ScannerType.AST_SCA, new AstScaClient(config, log));
        }
    }


//    public CxClientDelegator(String serverUrl, String username, String password, String origin, boolean disableCertificateValidation, Logger log) throws MalformedURLException {
//        this(new CxScanConfig(serverUrl, username, password, origin, disableCertificateValidation), log);
//    }

    @Override
    public CommonScanResults init() {
        log.info("Initializing Cx client [{}]", properties.get("version"));
        CommonScanResults scanResultsCombined = new CommonScanResults();

        scannersMap.forEach((key, scanner) -> {
            Results scanResults = scanner.init();
            scanResultsCombined.put(key, scanResults);
        });

        return scanResultsCombined;
    }


    @Override
    public CommonScanResults initiateScan() {

        CommonScanResults scanResultsCombined = new CommonScanResults();

        scannersMap.forEach((key, scanner) -> {
            if (scanner.getState() == State.SUCCESS) {
                Results scanResults = scanner.initiateScan();
                scanResultsCombined.put(key, scanResults);
            }
        });

        return scanResultsCombined;
    }


    @Override
    public CommonScanResults waitForScanResults() {

        CommonScanResults scanResultsCombined = new CommonScanResults();

        scannersMap.forEach((key, scanner) -> {
            if (scanner.getState() == State.SUCCESS) {
                Results scanResults = scanner.waitForScanResults();
                scanResultsCombined.put(key, scanResults);
            }
        });

        return scanResultsCombined;
    }

    @Override
    public CommonScanResults getLatestScanResults() {

        CommonScanResults scanResultsCombined = new CommonScanResults();

        scannersMap.forEach((key, scanner) -> {
            if (scanner.getState() == State.SUCCESS) {
                Results scanResults = scanner.getLatestScanResults();
                scanResultsCombined.put(key, scanResults);
            }
        });

        return scanResultsCombined;

    }
    
    
    
//
//    public AstScaClient getScaClient() {
//        return (AstScaClient) scannersMap.get(ScannerType.AST_SCA);
//    }

    public void close() {
        scannersMap.values().forEach(Scanner::close);
    }


}