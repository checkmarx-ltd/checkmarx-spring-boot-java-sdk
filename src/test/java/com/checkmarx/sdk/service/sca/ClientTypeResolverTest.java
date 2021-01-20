package com.checkmarx.sdk.service.sca;

import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.config.SpringConfiguration;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.checkmarx.sdk.service.CommonClientTest;
import com.cx.restclient.ast.ClientTypeResolver;
import com.cx.restclient.ast.dto.sca.ClientType;
import com.cx.restclient.configuration.CxScanConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(SpringConfiguration.class)
@SpringBootTest
@Slf4j
public class ClientTypeResolverTest extends CommonClientTest {
    
    @Autowired
    ScaProperties scaProperties;
    
    @Test
    public void determineClientType_cloudAccessControl() {
        testDetermineClientType(scaProperties.getAccessControlUrl());
    }

    //TODO : fix this test
    @Test
    @Ignore("this test fails and needs to be fixed")
    public void determineClientType_onPremAccessControl() {
        testDetermineClientType(scaProperties.getAccessControlUrl());
    }

    @Test
    public void determineClientType_invalidServer() {
        checkThatExceptionIsThrown("https://example.com");
    }

    @Test
    public void determineClientType_invalidUrlFormat() {
        checkThatExceptionIsThrown("incorrect!url?format");
    }

    private static void checkThatExceptionIsThrown(String url) {
        com.cx.restclient.ast.ClientTypeResolver resolver = new com.cx.restclient.ast.ClientTypeResolver(new CxScanConfig());
        try {
            resolver.determineClientType(url);
            Assert.fail("Expected exception, but didn't get any.");
        } catch (Exception e) {
            Assert.assertTrue("Unexpected exception type.", e instanceof ASTRuntimeException);
            Assert.assertTrue("Exception message is empty.", StringUtils.isNotEmpty(e.getMessage()));
            log.info("Got an expected exception");
        }
    }

    private void testDetermineClientType(String urlPropName) {
        com.cx.restclient.ast.ClientTypeResolver resolver = new com.cx.restclient.ast.ClientTypeResolver(new CxScanConfig());
        ClientType clientType = resolver.determineClientType(urlPropName);
        Assert.assertNotNull("Client type is null.", clientType);
        Assert.assertTrue("Client ID is empty.", StringUtils.isNotEmpty(clientType.getClientId()));
        Assert.assertTrue("Scopes are empty.", StringUtils.isNotEmpty(clientType.getScopes()));
    }
}