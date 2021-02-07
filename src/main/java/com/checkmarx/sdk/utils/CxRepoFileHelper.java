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
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        String gitURL = params.getGitUrl();
        String branch = params.getBranch();
        String srcPath;
        File pathFile = null;
        srcPath = getGitClonePath().concat("/").concat(UUID.randomUUID().toString());
        pathFile = new File(srcPath);

        try {
            gitClone(gitURL, branch, pathFile);

            log.info("After Clone");
            String exclusions = null;
            if(params.getFileExclude() != null && !params.getFileExclude().isEmpty()){
                exclusions = String.join(",",params.getFileExclude());
            }
            runPostCloneScript(params, srcPath);
            
            String cxZipFile = getGitClonePath().concat("/").concat("cx.".concat(UUID.randomUUID().toString()).concat(".zip"));
            log.info("running zip file");
            ZipUtils.zipFile(srcPath, cxZipFile, exclusions);
            try {
                makeWritableDirectory(pathFile);
                FileUtils.deleteDirectory(pathFile);
            } catch (IOException e) {
                log.warn("Error deleting file {} - {}", pathFile, ExceptionUtils.getRootCauseMessage(e));
            }
            return cxZipFile;
            
        } catch (GitAPIException | IOException | URISyntaxException e)  {
            log.error(ExceptionUtils.getRootCauseMessage(e));
            throw new CheckmarxException("Unable to clone Git Url.");
        }
    }

    private String getGitClonePath() {
        return cxProperties == null ? CxPropertiesBase.getDefaultOsPath() : cxProperties.getGitClonePath();
    }

    public void makeWritableDirectory(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory() ) {
                makeWritable(fileEntry);
                makeWritableDirectory(fileEntry);
            } else {
                makeWritable(fileEntry);
            }
        }
    }

    private void makeWritable(File file) {
        Path pathGit = Paths.get(file.getAbsolutePath());
        DosFileAttributeView dos = Files.getFileAttributeView(pathGit, DosFileAttributeView.class);
        try {
            dos.setHidden(false);
            dos.setReadOnly(false);
        } catch (IOException e) {
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

    public String getScaZipFolderPath(String repoUrlWithAuth, List<String> excludeFiles, String branch) throws CheckmarxException {
        CxScanParams cxScanParams = prepareScanParamsToCloneRepo( repoUrlWithAuth,  excludeFiles,  branch);
        return prepareRepoFile(cxScanParams);
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
