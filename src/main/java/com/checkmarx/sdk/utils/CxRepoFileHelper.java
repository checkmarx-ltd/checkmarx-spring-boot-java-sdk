package com.checkmarx.sdk.utils;

import com.checkmarx.sdk.config.Constants;
import com.checkmarx.sdk.config.CxPropertiesBase;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.utils.zip.ZipUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

@Slf4j
public class CxRepoFileHelper {
   private final CxPropertiesBase cxProperties;

    public CxRepoFileHelper(CxPropertiesBase cxProperties) {
        this.cxProperties = cxProperties;
    }

    public CxRepoFileHelper() {
        this.cxProperties = null;
    }
    
    public String prepareRepoFile(CxScanParams params) throws CheckmarxException {

        try {
            File pathFile = gitCloneAndRunPostCloneScript(params);
            String zipFilePath = zipClonedRepo(pathFile, params.getFileExclude());
            deleteCloneLocalDir(pathFile);
            return zipFilePath;

        } catch (GitAPIException | IOException | URISyntaxException e)  {
            log.error(ExceptionUtils.getRootCauseMessage(e));
            throw new CheckmarxException("Unable to clone Git Url.");
        }
    }

    public void deleteCloneLocalDir(File pathFile) {
        try {
            boolean isWin = System.getProperty("os.name").startsWith("Windows");
            makeWritableDirectory(pathFile, isWin);
            FileUtils.deleteDirectory(pathFile);
        } catch (Exception e) {
            log.error("Error deleting file {} - {}", pathFile, ExceptionUtils.getRootCauseMessage(e));
        }
    }
    

    public String zipClonedRepo(File pathFile, List<String> fileExclude) throws IOException {
        String cxZipFile = getGitClonePath().concat("/").concat("cx.".concat(UUID.randomUUID().toString()).concat(".zip"));
        String exclusions = null;
        if(fileExclude != null && !fileExclude.isEmpty()){
            exclusions = String.join(",",fileExclude);
        }
        log.info("running zip file");
        ZipUtils.zipFile(pathFile.getAbsolutePath(), cxZipFile, exclusions);
        return cxZipFile;
    }

    public File gitCloneAndRunPostCloneScript(
            CxScanParams params) throws GitAPIException, URISyntaxException {
        String gitURL = params.getGitUrl();
        String branch = params.getBranch();
        String srcPath;
        srcPath = getGitClonePath().concat("/").concat(UUID.randomUUID().toString());
        File pathFile = new File(srcPath);

        gitClone(gitURL, branch, pathFile);
        log.info("git: {}, Cloned successfully", gitURL);
        runPostCloneScript(params, srcPath);

        return pathFile;
    }

    private String getGitClonePath() {
        return cxProperties == null ? CxPropertiesBase.getDefaultOsPath() : cxProperties.getGitClonePath();
    }

    public void makeWritableDirectory(final File folder, boolean isWin) {
        for (final File fileEntry : folder.listFiles()) {
            makeWritable(fileEntry, isWin);
            if (fileEntry.isDirectory() ) {
                makeWritableDirectory(fileEntry, isWin);
            } 
        }
    }

    private void makeWritable(File file, boolean isWin) {
            if (isWin) {
                makeWritableWin(file);
            }else{
                makeWritableLinux(file);
            }
    }

    private void makeWritableLinux(File file) {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);

        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_WRITE);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);

        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);

        try {
            Files.setPosixFilePermissions(file.toPath(), perms);
        } catch (Exception e) {
            log.warn("Error changing file {} attributes: {}", file.getAbsolutePath(), e.getMessage());
        }
    }
    
    private void makeWritableWin(File file) {
        Path pathGit = Paths.get(file.getAbsolutePath());
        DosFileAttributeView dos = Files.getFileAttributeView(pathGit, DosFileAttributeView.class);
        try {
            dos.setHidden(false);
            dos.setReadOnly(false);
        } catch (Exception e) {
            log.warn("Error changing file {} attributes: {}", file.getAbsolutePath(), e.getMessage());
        }
    }

    private void gitClone(String gitURL, String branch, File pathFile) throws URISyntaxException, GitAPIException {
        URI uri = new URI(gitURL);
        CredentialsProvider credentialsProvider = null;
        String token = uri.getUserInfo();
        if(token == null){
            token = "";
            log.info("empty token");
        }
        if(token.startsWith("oauth2:")){
            log.debug("Using gitlab clone");
            token = token.replace("oauth2:","");
            gitURL = gitURL.replace(uri.getUserInfo(), "gitlab-ci-token:".concat(token));
            credentialsProvider = new UsernamePasswordCredentialsProvider("oauth2", token);
        }
        else if(token.contains(":")){
            String[] userDetails = token.split(":");
            if(userDetails.length == 2) {
                log.debug("Using clone with username/password");
                credentialsProvider = new UsernamePasswordCredentialsProvider(userDetails[0], userDetails[1]);
            }
            log.info("credentialsProvider is not allocated");
        }
        else if (gitURL.contains("@bitbucket.org")) {
            credentialsProvider = new UsernamePasswordCredentialsProvider("x-token-auth", token);
        }
        else{
            credentialsProvider = new UsernamePasswordCredentialsProvider(token, "");
            log.info("credentialsProvider without password");
        }
        log.info("Cloning code locally to {}", pathFile);
        Git.cloneRepository()
                .setURI(gitURL)
                .setBranch(branch)
                .setBranchesToClone(Collections.singleton(branch))
                .setDirectory(pathFile)
                .setCredentialsProvider(credentialsProvider)
                .call()
                .close();
    }

    private void runPostCloneScript(CxScanParams params, String path) {
        if (!ScanUtils.empty(getPostCloneScript())) {
            try {
                Binding binding = new Binding();
                binding.setProperty("params", params);
                binding.setVariable("path", path);
                File script = new File(getPostCloneScript());
                String scriptName = script.getName();
                String scriptDir = script.getParent();
                String[] roots = new String[]{scriptDir};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                gse.run(scriptName, binding);
            } catch (GroovyRuntimeException | IOException | ResourceException | ScriptException e) {
                log.error("Error occurred while executing Post Clone Script {}", ExceptionUtils.getMessage(e), e);
            }
        }
    }

    private String getPostCloneScript() {
        return cxProperties==null ? null : cxProperties.getPostCloneScript();
    }

    public String getScaClonedRepoFolderPath(String repoUrlWithAuth, List<String> excludeFiles,
                                    String branch) throws CheckmarxException {
        CxScanParams cxScanParams = prepareScanParamsToCloneRepo( repoUrlWithAuth,  excludeFiles,  branch);
        File pathFile = null;
        try {
            pathFile = gitCloneAndRunPostCloneScript(cxScanParams);
        } catch (GitAPIException | URISyntaxException e) {
            log.error(ExceptionUtils.getRootCauseMessage(e));
            throw new CheckmarxException("Unable to clone Git Url.");
        }
        return pathFile.getAbsolutePath();
    }

    private CxScanParams prepareScanParamsToCloneRepo(String repoUrlWithAuth, List<String> excludeFiles, String branch) {
        CxScanParams cxScanParams = new CxScanParams();
        cxScanParams.withGitUrl(repoUrlWithAuth);
        cxScanParams.withFileExclude(excludeFiles);

        if (StringUtils.isNotEmpty(branch)) {
            cxScanParams.withBranch(Constants.CX_BRANCH_PREFIX.concat(branch));
        }
        return cxScanParams;
    }
}
