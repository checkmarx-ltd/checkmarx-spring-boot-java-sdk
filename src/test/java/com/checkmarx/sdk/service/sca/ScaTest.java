package com.checkmarx.sdk.service.sca;

import com.checkmarx.sdk.GithubProperties;
import com.checkmarx.sdk.config.*;
import com.checkmarx.sdk.dto.AstScaResults;
import com.checkmarx.sdk.dto.ast.ScanParams;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import com.checkmarx.sdk.service.FilterInputFactory;
import com.checkmarx.sdk.service.FilterValidator;
import com.checkmarx.sdk.service.scanner.ScaScanner;
import com.checkmarx.sdk.dto.RemoteRepositoryInfo;
import com.checkmarx.sdk.dto.sca.ScaConfig;
import com.checkmarx.sdk.config.RestClientConfig;
import com.checkmarx.sdk.dto.SourceLocationType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.*;
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
import java.util.HashMap;

import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@Import(SpringConfiguration.class)
@SpringBootTest
@Slf4j
public class ScaTest extends ScaTestsBase {


    @Autowired
    ScaProperties scaProperties;

    @Autowired
    CxProperties cxProperties;

    @Autowired
    GithubProperties githubProperties;
    

    protected RestClientConfig initScaConfig(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        RestClientConfig config = initScaConfig(useOnPremAuthentication);
        config.getScaConfig().setSourceLocationType(SourceLocationType.REMOTE_REPOSITORY);
        RemoteRepositoryInfo repoInfo = new RemoteRepositoryInfo();

        URL repoUrl = new URL(repoUrlProp);
        repoInfo.setUrl(repoUrl);
        repoInfo.setUsername(githubProperties.getToken());

        config.getScaConfig().setRemoteRepositoryInfo(repoInfo);
        return config;
    }

    protected AstScaResults runScan(RestClientConfig config) {

        ScaScanner scanner = getScanner();
        AstScaResults results = scanner.scan(getScanParams(config));

        return results;
    }
    
    protected RestClientConfig initScaConfig(boolean useOnPremAuthentication){
        RestClientConfig config = new RestClientConfig();
        config.setProjectName("sdkScaProject");
        config.setProgressInterval(5);
        ScaConfig sca = getScaConfig(useOnPremAuthentication);
        config.setScaConfig(sca);

        return config;
    }
    protected ScaConfig getScaConfig(boolean useOnPremiseAuthentication) {
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

        ScaConfig result = new ScaConfig();
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
    public void scan_localDirUpload() throws IOException, ScannerRuntimeException {
        RestClientConfig config = initScaConfig(false);

        config.getScaConfig().setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

        Path sourcesDir = null;
        try {
            sourcesDir = extractTestProjectFromResources();
            config.setSourceDir(sourcesDir.toString());

            AstScaResults scanResults = runScan(config);
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
        RestClientConfig config = initScaConfig(false);
        AstScaResults latestResults = getLatestResults(config);
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
        RestClientConfig config = initScaConfig(false);
        config.setProjectName(projectName);
        AstScaResults latestResults = getLatestResults(config);
        Assert.assertNotNull("scanResults must not be null.", latestResults);
        Assert.assertNull("scaResults must be null.", latestResults.getScaResults());
    }

    @Test
    @Ignore("There is no stable on-prem environment.")
    public void scan_onPremiseAuthentication() throws MalformedURLException {
        scanRemoteRepo(githubProperties.getUrl(), true);
    }

    private AstScaResults getLatestResults(RestClientConfig config) {
        
            ScaScanner client = getScanner();
            Assert.assertNotNull(client);
            ScanParams params = getScanParams(config);
            return client.getLatestScanResults(params);
    }

    @Test
    public void scan_localDirUploadIncludeSources() throws IOException, ScannerRuntimeException {
        RestClientConfig config = initScaConfig(false);
        localDirScan(config);
    }

    @Test
    public void scan_localDirZeroCodeScan() throws IOException, ScannerRuntimeException {
        RestClientConfig config = initScaConfig(false);
        localDirScan(config);
    }

    private void localDirScan(RestClientConfig config) throws MalformedURLException {

        config.getScaConfig().setSourceLocationType(SourceLocationType.LOCAL_DIRECTORY);

        Path sourcesDir = null;
        try {
            sourcesDir = extractTestProjectFromResources();
            config.setSourceDir(sourcesDir.toString());

            AstScaResults scanResults = runScan(config);
            verifyScanResults(scanResults);
        } finally {
            deleteDir(sourcesDir);
        }
    }



    private ScaScanner getScanner() {
        return new ScaScanner(scaProperties, new FilterInputFactory(), new FilterValidator());
    }

    private ScanParams getScanParams(RestClientConfig config) {
        com.checkmarx.sdk.config.ScaConfig scaConfig = new com.checkmarx.sdk.config.ScaConfig();
        scaConfig.setAccessControlUrl(scaProperties.getAccessControlUrl());
        scaConfig.setApiUrl(scaProperties.getApiUrl());
        scaConfig.setAppUrl(scaProperties.getAppUrl());
        scaConfig.setTenant(scaProperties.getTenant());
        scaConfig.setThresholdsScore(scaProperties.getThresholdsScore());
        scaConfig.setThresholdsSeverity(new HashMap<>());
        scaConfig.setThresholdsSeverityDirectly(new HashMap<>());

        ScanParams scanParams = ScanParams.builder()
                .projectName(config.getProjectName())
                .remoteRepoUrl(getRepoUrl())
                .scaConfig(scaConfig)
                .build();
        return scanParams;
            
    }

    private URL getRepoUrl() {
        URL parsedUrl;
        try {
            String token = githubProperties.getToken();
            String gitAuthUrl = githubProperties.getUrl().replace(Constants.HTTPS, Constants.HTTPS.concat(token).concat("@"));
            gitAuthUrl = gitAuthUrl.replace(Constants.HTTP, Constants.HTTP.concat(token).concat("@"));

            parsedUrl = new URL(gitAuthUrl);
        } catch (MalformedURLException e) {
            log.error("Failed to parse repository URL: '{}'", githubProperties.getUrl());
            failOnException(e);
            throw new ScannerRuntimeException("Invalid repository URL.");
        }
        return parsedUrl;
    }
    
    private void scanRemoteRepo(String repoUrlProp, boolean useOnPremAuthentication) throws MalformedURLException {
        RestClientConfig config = initScaConfig(repoUrlProp, useOnPremAuthentication);
        AstScaResults scanResults = runScan(config);
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
        String srcResourceName = ScaTest.PACKED_SOURCES_TO_SCAN;
        log.info("Getting resource stream from '{}'", srcResourceName);
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(srcResourceName);
    }
}
