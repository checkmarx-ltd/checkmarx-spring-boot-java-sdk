package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxGoProperties;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.cx.CxScanParams;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.utils.ScanUtils;
import com.checkmarx.sdk.utils.ZipUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.UUID;

@Service
public class CxRepoFileService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxRepoFileService.class);
    private final CxGoProperties cxProperties;

    public CxRepoFileService(CxGoProperties cxProperties) {
        this.cxProperties = cxProperties;
    }

    public String prepareRepoFile(CxScanParams params) throws CheckmarxException {
        String gitURL = params.getGitUrl();
        String branch = params.getBranch();
        String srcPath;
        File pathFile = null;
        srcPath = cxProperties.getGitClonePath().concat("/").concat(UUID.randomUUID().toString());
        pathFile = new File(srcPath);

        try {
            URI uri = new URI(gitURL);
            CredentialsProvider credentialsProvider = null;
            String token = uri.getUserInfo();
            if(token == null){
                token = "";
            }
            if(token.startsWith("oauth2:")){
                log.debug("Using gitlab clone");
                token = token.replace("oauth2:","");
                gitURL = gitURL.replace(uri.getUserInfo(), "gitlab-ci-token:".concat(token));
                credentialsProvider = new UsernamePasswordCredentialsProvider("user", token);
            }
            else if(token.contains(":")){
                String[] userDetails = token.split(":");
                if(userDetails.length == 2) {
                    log.debug("Using clone with username/password");
                    credentialsProvider = new UsernamePasswordCredentialsProvider(userDetails[0], userDetails[1]);
                }
            }
            else{
                credentialsProvider = new UsernamePasswordCredentialsProvider(token, "");
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
            String cxZipFile = cxProperties.getGitClonePath().concat("/").concat("cx.".concat(UUID.randomUUID().toString()).concat(".zip"));
            String exclusions = null;
            if(params.getFileExclude() != null && !params.getFileExclude().isEmpty()){
                exclusions = String.join(",",params.getFileExclude());
            }
            runPostCloneScript(params, srcPath);
            ZipUtils.zipFile(srcPath, cxZipFile, exclusions);
            try {
                FileUtils.deleteDirectory(pathFile);
            } catch (IOException e){
                log.warn("Error deleting file {} - {}", pathFile, ExceptionUtils.getRootCauseMessage(e));
            }
            return cxZipFile;
        } catch (GitAPIException | IOException | URISyntaxException e)  {
            log.error(ExceptionUtils.getRootCauseMessage(e));
            throw new CheckmarxException("Unable to clone Git Url.");
        }
    }

    private void runPostCloneScript(CxScanParams params, String path) {
        if (!ScanUtils.empty(cxProperties.getPostCloneScript())) {
            try {
                Binding binding = new Binding();
                binding.setProperty("params", params);
                binding.setVariable("path", path);
                File script = new File(cxProperties.getPostCloneScript());
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
}
