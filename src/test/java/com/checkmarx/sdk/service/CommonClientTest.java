package com.checkmarx.sdk.service;


import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.configuration.PropertyFileLoader;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.ProxyConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;


@Slf4j
public abstract class CommonClientTest {
    
    protected void failOnException(Exception e) {
        log.error("Unexpected exception during test.", e);
        Assert.fail(e.getMessage());
    }
    
    protected CommonScanResults runScan(CxScanConfig config) throws MalformedURLException, ASTRuntimeException {
        CxClientDelegator client = new CxClientDelegator(config, log);
        try {
            client.init();
            log.info("Initiate scan for the following scanners: " + config.getScannerTypes());
            client.initiateScan();
            log.info("Waiting for results of " + config.getScannerTypes());
            CommonScanResults results =  client.waitForScanResults();
            Assert.assertNotNull(results);
            log.info("Results retrieved" );
            return results;
        } catch (Exception e) {
            failOnException(e);
            throw new ASTRuntimeException(e);
        }
    }

}
