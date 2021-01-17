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

import java.net.MalformedURLException;


@Slf4j
public abstract class CommonClientTest {
    private static final String MAIN_PROPERTIES_FILE = "test.properties";
    public static final String OVERRIDE_FILE = "test-secrets.properties";

    private PropertyFileLoader props = new PropertyFileLoader(MAIN_PROPERTIES_FILE,OVERRIDE_FILE);

    protected String prop(String key) {
        return props.get(key);
    }

    protected void setProxy(CxScanConfig config) {
        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setHost(prop("proxy.host"));
        proxyConfig.setPort(Integer.parseInt(prop("proxy.port")));
        config.setProxyConfig(proxyConfig);
    }

    protected void failOnException(Exception e) {
        log.error("Unexpected exception during test.", e);
        Assert.fail(e.getMessage());
    }
//
//    protected CxScanConfig initSastConfig(CxScanConfig config, String projectName) {
//        config.setReportsDir(new File("C:\\report"));
//        config.setSourceDir(prop("sastSource"));
//        config.setUsername(prop("username"));
//        config.setPassword(prop("password"));
//        config.setUrl(prop("serverUrl"));
//        config.setCxOrigin("common");
//        config.setProjectName(projectName);
//        config.setPresetName("Default");
//        config.setTeamPath("\\CxServer");
//        config.setSynchronous(true);
//        config.setGeneratePDFReport(true);
//        config.addScannerType(ScannerType.SAST);
//        config.setPresetName("Default");
////        config.setPresetId(7);
//
//        return config;
//    }

    protected AstScaConfig getScaConfig(boolean useOnPremiseAuthentication) {
        String accessControlProp, usernameProp, passwordProp;
        if (useOnPremiseAuthentication) {
            accessControlProp = "astSca.onPremise.accessControlUrl";
            usernameProp = "astSca.onPremise.username";
            passwordProp = "astSca.onPremise.password";
        } else {
            accessControlProp = "astSca.cloud.accessControlUrl";
            usernameProp = "astSca.cloud.username";
            passwordProp = "astSca.cloud.password";
        }

        AstScaConfig result = new AstScaConfig();
        result.setApiUrl(prop("astSca.apiUrl"));
        result.setWebAppUrl(prop("astSca.webAppUrl"));
        result.setTenant(prop("astSca.tenant"));
        result.setAccessControlUrl(prop(accessControlProp));
        result.setUsername(prop(usernameProp));
        result.setPassword(prop(passwordProp));
        result.setIncludeSources(false);
        return result;
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
//
//    protected CxScanConfig initOsaConfig(CxScanConfig config, String projectName) {
//        log.info("Scan ProjectName " + projectName);
//        config.addScannerType(ScannerType.OSA);
//        config.setSourceDir(prop("dependencyScanSourceDir"));
//        config.setReportsDir(new File("C:\\report"));
//        config.setUrl(prop("serverUrl"));
//        config.setUsername(prop("username"));
//        config.setPassword(prop("password"));
//
//        config.setCxOrigin("common");
//        config.setProjectName(projectName);
//        config.setPresetName("Default");
//        config.setTeamPath("\\CxServer");
//        config.setSynchronous(true);
//        config.setGeneratePDFReport(true);
//
//        config.setOsaRunInstall(true);
//        config.setOsaThresholdsEnabled(true);
//        config.setPublic(true);
//
//        return config;
//    }
}
