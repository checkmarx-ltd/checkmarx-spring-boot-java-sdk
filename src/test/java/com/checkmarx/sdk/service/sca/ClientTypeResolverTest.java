package com.checkmarx.sdk.service.sca;

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


@Slf4j
public class ClientTypeResolverTest  extends CommonClientTest {
    @Test
    public void determineClientType_cloudAccessControl() {
        testDetermineClientType("astSca.cloud.accessControlUrl");
    }

    //TODO : fix this test
    @Test
    @Ignore("this test fails and needs to be fixed")
    public void determineClientType_onPremAccessControl() {
        testDetermineClientType("astSca.onPremise.accessControlUrl");
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
        ClientTypeResolver resolver = new ClientTypeResolver(new CxScanConfig());
        try {
            resolver.determineClientType(url);
            Assert.fail("Expected exception, but didn't get any.");
        } catch (Exception e) {
            log.info("Got an exception", e);
            Assert.assertTrue("Unexpected exception type.", e instanceof ASTRuntimeException);
            Assert.assertTrue("Exception message is empty.", StringUtils.isNotEmpty(e.getMessage()));
        }
    }

    private void testDetermineClientType(String urlPropName) {
        ClientTypeResolver resolver = new ClientTypeResolver(new CxScanConfig());
        ClientType clientType = resolver.determineClientType(prop(urlPropName));
        Assert.assertNotNull("Client type is null.", clientType);
        Assert.assertTrue("Client ID is empty.", StringUtils.isNotEmpty(clientType.getClientId()));
        Assert.assertTrue("Scopes are empty.", StringUtils.isNotEmpty(clientType.getScopes()));
    }
}