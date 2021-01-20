package com.checkmarx.sdk.service.sca;

import com.checkmarx.sdk.GithubProperties;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.config.ScaProperties;
import com.checkmarx.sdk.config.SpringConfiguration;
import com.checkmarx.sdk.exception.ASTRuntimeException;
import com.cx.restclient.CxClientDelegator;
import com.cx.restclient.ast.dto.common.RemoteRepositoryInfo;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.configuration.CxScanConfig;
import com.cx.restclient.dto.CommonScanResults;
import com.cx.restclient.dto.ProxyConfig;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.SourceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@Import(SpringConfiguration.class)
@SpringBootTest
@Slf4j
public class AstScaTest extends ScaTestsBase {


    @Autowired
    ScaProperties scaProperties;

    @Autowired
    CxProperties cxProperties;

    @Autowired
    GithubProperties githubProperties;
    
    protected void setProxy(CxScanConfig config, String proxyHost, String proxyPort) {
        ProxyConfig proxyConfig = new ProxyConfig();
        proxyConfig.setHost(proxyHost);
        proxyConfig.setPort(Integer.parseInt(proxyPort));
        config.setProxyConfig(proxyConfig);
    }

    protected CxScanConfig initScaConfig(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(useOnPremAuthentication);
        config.getAstScaConfig().setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();

        URL repoUrl = new URL(repoUrlProp);
        repoInfo.setUrl(repoUrl);
        repoInfo.setUsername(githubProperties.getToken());

        config.getAstScaConfig().setRemoteRepositoryInfo(repoInfo);
        return config;
    }

    protected CxScanConfig initScaConfig(boolean useOnPremAuthentication){
        CxScanConfig config = new CxScanConfig();
        config.addScannerType(ScannerType.AST_SCA);
        config.setProjectName("sdkScaProject");
        config.setOsaProgressInterval(5);
        AstScaConfig sca = getScaConfig(useOnPremAuthentication);
        config.setAstScaConfig(sca);

        return config;
    }
    protected AstScaConfig getScaConfig(boolean useOnPremiseAuthentication) {
        String accessControlProp, usernameProp, passwordProp;
        if (useOnPremiseAuthentication) {
//            accessControlProp = onPremiseControlUrl;
//            usernameProp = onPremiseUsername;
//            passwordProp = onPremisePass;
            throw new UnsupportedOperationException();
        } else {
            accessControlProp = scaProperties.getAccessControlUrl();
            usernameProp = scaProperties.getUsername();
            passwordProp = scaProperties.getPassword();
        }

        AstScaConfig result = new AstScaConfig();
        result.setApiUrl(scaProperties.getApiUrl());
        result.setWebAppUrl(scaProperties.getAppUrl());
        result.setTenant(scaProperties.getTenant());
        result.setAccessControlUrl(accessControlProp);
        result.setUsername(usernameProp);
        result.setPassword(passwordProp);
        result.setIncludeSources(false);
        return result;
    }
    
    @Test
    public void scan_localDirUpload() throws IOException, ASTRuntimeException {
        CxScanConfig config = initScaConfig(false);
        config.setOsaThresholdsEnabled(true);
        config.getAstScaConfig().setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

        Path sourcesDir = null;
        try {
            sourcesDir = extractTestProjectFromResources();
            config.setSourceDir(sourcesDir.toString());

            CommonScanResults scanResults = runScan(config);
            verifyScanResults(scanResults);
        } finally {
            deleteDir(sourcesDir);
        }
    }

    @Test
    public void scan_remotePublicRepo() throws MalformedURLException {
        scanRemoteRepo(githubProperties.getUrl(), false);
    }

    @Test
    public void scan_remotePrivateRepo() throws MalformedURLException {
        scanRemoteRepo(githubProperties.getUrl(), false);
    }

    @Test
    public void getLatestScanResults_existingResults() {
        CxScanConfig config = initScaConfig(false);
        CommonScanResults latestResults = getLatestResults(config);
        verifyScanResults(latestResults);
    }

    /**
     * Getting latest results for a project that doesn't exist.
     */
    @Test
    public void getLatestScanResults_nonexistentProject() {
        testMissingResultsCase("nonexistent-project-name");
    }

    /**
     * Existing project without any scans.
     */
    @Test
    public void getLatestScanResults_projectWithoutScans() {
        testMissingResultsCase("common-client-test-02-no-scans");
    }

    /**
     * Project with all scans failed (e.g. invalid git repo).
     */
    @Test
    public void getLatestScanResults_projectWithAllScansFailed() {
        testMissingResultsCase("common-client-test-03-all-scans-failed");
    }

    /**
     * Make sure that SCA results are null in different expected cases.
     */
    private void testMissingResultsCase(String projectName) {
        log.info("Checking that scaResults are null for the {} project", projectName);
        CxScanConfig config = initScaConfig(false);
        config.setProjectName(projectName);
        CommonScanResults latestResults = getLatestResults(config);
        Assert.assertNotNull("scanResults must not be null.", latestResults);
        Assert.assertNull("scaResults must be null.", latestResults.getScaResults());
    }

    @Test
    @Ignore("There is no stable on-prem environment.")
    public void scan_onPremiseAuthentication() throws MalformedURLException {
        scanRemoteRepo(githubProperties.getUrl(), true);
    }

    @Test
    @Ignore("Needs specific network configuration with a proxy.")
    public void runScaScanWithProxy() throws MalformedURLException, ASTRuntimeException {
        CxScanConfig config = initScaConfig(false);
        //TODO - supply proxy host and port when running this
        setProxy(config, "", "");
        CommonScanResults scanResults = runScan(config);
        verifyScanResults(scanResults);
    }

    private CommonScanResults getLatestResults(CxScanConfig config) {
        CxClientDelegator client = null;
        try {
            client = new CxClientDelegator(config, log);
        } catch (MalformedURLException e) {
            failOnException(e);
        }
        Assert.assertNotNull(client);
        client.init();

        return client.getLatestScanResults();
    }

    @Test
    public void scan_localDirUploadIncludeSources() throws IOException, ASTRuntimeException {
        CxScanConfig config = initScaConfig(false);
        localDirScan(config);
    }

    @Test
    public void scan_localDirZeroCodeScan() throws IOException, ASTRuntimeException {
        CxScanConfig config = initScaConfig(false);
        localDirScan(config);
    }

    private void localDirScan(CxScanConfig config) throws MalformedURLException {
        config.setOsaThresholdsEnabled(true);
        config.getAstScaConfig().setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

        Path sourcesDir = null;
        try {
            sourcesDir = extractTestProjectFromResources();
            config.setSourceDir(sourcesDir.toString());

            CommonScanResults scanResults = runScan(config);
            verifyScanResults(scanResults);
        } finally {
            deleteDir(sourcesDir);
        }
    }

    private void scanRemoteRepo(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        CxScanConfig config = initScaConfig(repoUrlProp, useOnPremAuthentication);
        CommonScanResults scanResults = runScan(config);
        verifyScanResults(scanResults);
    }

    private Path extractTestProjectFromResources() {
        InputStream testProjectStream = getTestProjectStream();
        Path tempDirectory = createTempDirectory();
        extractResourceToDir(testProjectStream, tempDirectory);
        return tempDirectory;
    }

    private void extractResourceToDir(InputStream source, Path targetDir) {
        log.info("Unpacking sources into the temp dir.");
        int fileCount = 0;
        try (ArchiveInputStream inputStream = new ArchiveStreamFactory().createArchiveInputStream(source)) {
            ArchiveEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (!inputStream.canReadEntryData(entry)) {
                    throw new IOException(String.format("Unable to read entry: %s", entry));
                }
                Path fullTargetPath = targetDir.resolve(entry.getName());
                File targetFile = fullTargetPath.toFile();
                if (entry.isDirectory()) {
                    extractDirectory(targetFile);
                } else {
                    extractFile(inputStream, targetFile);
                    fileCount++;
                }
            }
        } catch (IOException | ArchiveException e) {
            failOnException(e);
        }
        log.info("Files extracted: {}", fileCount);
    }

    private static void extractFile(ArchiveInputStream inputStream, File targetFile) throws IOException {
        File parent = targetFile.getParentFile();
        extractDirectory(parent);
        try (OutputStream outputStream = Files.newOutputStream(targetFile.toPath())) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    private static void extractDirectory(File targetFile) throws IOException {
        if (!targetFile.isDirectory() && !targetFile.mkdirs()) {
            throw new IOException(String.format("Failed to create directory %s", targetFile));
        }
    }

    private static InputStream getTestProjectStream() {
        String srcResourceName = AstScaTest.PACKED_SOURCES_TO_SCAN;
        log.info("Getting resource stream from '{}'", srcResourceName);
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(srcResourceName);
    }
}
