package com.checkmarx.sdk.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HostWithProtocolTest {
    private static String testGoodURLWithPort = "http://foo.com:80/bar/baz";
    private static String testGoodURLWithoutPort = "http://foo.com/bar/baz";

    private static String testBadURLWithPort = "http//foo.com:80/bar/baz";
    private static String testBadURLWithoutPort = "http//foo.com/bar/baz";


    @Test
    public void Canary() {
        assertTrue(true);
    }

    @Test
    public void testGoodURLWithPortURIValidate() {
        execURIValidatedGoodTest(testGoodURLWithPort);
    }

    @Test
    public void testGoodURLWithoutPortURIValidate() {
        execURIValidatedGoodTest(testGoodURLWithoutPort);
    }

    @Test
    public void testBadURLWithPortURIValidate() {
        execURIValidatedBadTest(testBadURLWithPort);
    }

    @Test
    public void testBadURLWithoutPortURIValidate() {
        execURIValidatedBadTest(testBadURLWithoutPort);
    }

    private void execURIValidatedGoodTest(String url) {
        String frag = ScanUtils.getHostWithProtocol(url);
        URI testURI = null;

        try {
            testURI = new URI(frag);
        } catch (URISyntaxException e) {

            fail();
            return;
        }

        assertTrue(
                testURI != null && testURI.getScheme() != null && testURI.getHost() != null);

    }

    private void execURIValidatedBadTest(String url) {
        String frag = ScanUtils.getHostWithProtocol(url);
        URI testURI = null;

        try {
            testURI = new URI(frag);
        } catch (URISyntaxException e) {

            fail();
            return;
        }

        assertTrue(testURI != null && testURI.getPort() < 0 && testURI.getScheme() != null && testURI.getHost() != null);
    }

}