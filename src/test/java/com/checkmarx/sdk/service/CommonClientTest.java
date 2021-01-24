package com.checkmarx.sdk.service;


import com.checkmarx.sdk.dto.ast.ASTResultsWrapper;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.cx.restclient.AbstractScanner;
import com.cx.restclient.AstScanner;
import com.cx.restclient.ScaScanner;
import com.cx.restclient.configuration.RestClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.fail;


@Slf4j
public abstract class CommonClientTest {

    protected static void deleteDir(Path directory) {
        if (directory == null) {
            return;
        }

        log.info("Deleting '{}'", directory);
        try {
            FileUtils.deleteDirectory(directory.toFile());
        } catch (IOException e) {
            log.warn("Failed to delete temp dir.", e);
        }
    }
    
    protected void failOnException(Exception e) {
        log.error("Unexpected exception during test.", e);
        Assert.fail(e.getMessage());
    }

    protected static Path createTempDirectory() {
        String systemTempDir = FileUtils.getTempDirectoryPath();
        String subdir = String.format("common-client-tests-%s", UUID.randomUUID());
        Path result = Paths.get(systemTempDir, subdir);

        log.info("Creating a temp dir: {}", result);
        boolean success = result.toFile().mkdir();
        if (!success) {
            fail("Failed to create temp dir.");
        }
        return result;
    }

//    protected CommonScanResults runScan(CxScanConfig config, AbstractScanner scanner) throws MalformedURLException, ASTRuntimeException {
//        CxClientDelegator client = new CxClientDelegator(config, log);
//        try {
//            client.init();
//            log.info("Initiate scan for the following scanners: " + config.getScannerTypes());
//            client.initiateScan();
//            log.info("Waiting for results of " + config.getScannerTypes());
//            CommonScanResults results =  client.waitForScanResults();
//            Assert.assertNotNull(results);
//            log.info("Results retrieved" );
//            return results;
//        } catch (Exception e) {
//            failOnException(e);
//            throw new ASTRuntimeException(e);
//        }
//    }

}
