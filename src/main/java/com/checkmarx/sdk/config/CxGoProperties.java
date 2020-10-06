package com.checkmarx.sdk.config;

import com.checkmarx.sdk.utils.ScanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "cxgo")
@Validated
public class CxGoProperties {
    public static final String CONFIG_PREFIX = "sast";

    private Double version = 8.9;
//    private String username;
//    private String password;
//    private String clientId = "resource_owner_client";
//    private String soapClientId = "resource_owner_sast_client";
    private String clientSecret;
//    private String soapClientSecret;
//    private String scope = "sast_rest_api";
//    private String soapScope = "sast_api";
    private String baseUrl;
    private String url;
//    private String acUrl;
    private boolean multiTenant = true;
//    private String teamScript;
//    private String projectScript;
    private String scanPreset = Constants.CX_DEFAULT_PRESET;
    private String configuration = Constants.CX_DEFAULT_CONFIGURATION;
//    private String excludeFiles;
//    private String excludeFolders;
//    private Boolean incremental = false;
    private String gitClonePath;
//    private Integer incrementalThreshold = 7;
//    private Integer incrementalNumScans = 5;
    private String team;
//    private Boolean offline = false;
//    private Boolean preserveXml = false;
    private Integer scanTimeout = 120;
//    private String jiraProjectField = "jira-project";
//    private String jiraIssuetypeField = "jira-issuetype";
//    private String jiraCustomField = "jira-fields";
//    private String jiraAssigneeField = "jira-assignee";
//    private Integer httpConnectionTimeout = 30000;
//    private Integer httpReadTimeout = 120000;
    private Integer scanPolling = 20000;
//    private Integer reportPolling = 5000;
//    private Integer reportTimeout = 300000;
//    private Integer codeSnippetLength = 2500;
    private String TEAM_PATH_SEPARATOR_9 = "/";
    private String TEAM_PATH_SEPARATOR_8 = "\\";
    private String portalUrl;
//    private List<String> engineTypes = Arrays.asList("SAST", "SCA");
    private String postCloneScript;
//    private Boolean enableShardManager = false;

    private List<String> engineTypes = Arrays.asList("SAST", "SCA");
    
    // TODO:, JeffA this needs to be removed.
//    private String portalPackage = "checkmarx.wsdl.portal";

    public void setGitClonePath(String gitClonePath) {
        this.gitClonePath = gitClonePath;
    }
    public Integer getScanTimeout() {
        return this.scanTimeout;
    }

    public void setScanTimeout(Integer scanTimeout) {
        this.scanTimeout = scanTimeout;
    }

    public Integer getScanPolling() {
        return scanPolling;
    }

    public void setScanPolling(Integer scanPolling) {
        this.scanPolling = scanPolling;
    }

    public List<String> getEngineTypes() {
        return engineTypes;
    }

    public void setEngineTypes(List<String> engineTypes) {
        this.engineTypes = engineTypes;
    }


    public String getPostCloneScript() {
        return postCloneScript;
    }

    public void setPostCloneScript(String postCloneScript) {
        this.postCloneScript = postCloneScript;
    }

    
    public String getClientSecret() {
        return this.clientSecret;
    }


    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isMultiTenant() {
        return this.multiTenant;
    }

    public String getScanPreset() {
        return this.scanPreset;
    }

    public String getConfiguration() {
        return this.configuration;
    }

    public String getTeam() {
        return this.team;
    }
    
    
    public String getPortalUrl() {
        return this.portalUrl;
    }

    public String getGitClonePath(){
        if(this.gitClonePath == null){
            if (System.getProperty("os.name").startsWith("Windows")) {
                // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
                return Constants.WINDOWS_PATH;
            } else {
                // everything else
                return Constants.UNIX_PATH;
            }
        }
        else {
            return this.gitClonePath;
        }
    }
    

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMultiTenant(boolean multiTenant) {
        this.multiTenant = multiTenant;
    }


    public void setScanPreset(String scanPreset) {
        this.scanPreset = scanPreset;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }

 
    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }


    public Boolean getEnableOsa() {
        //return enableOsa;
        return false;
    }

    public String getTeamPathSeparator(){
        if(version < 9.0){
            return TEAM_PATH_SEPARATOR_8;
        }
        else{
            return TEAM_PATH_SEPARATOR_9;
        }
    }

    @PostConstruct
    private void initTeam(){
        if(team != null && !team.startsWith(getTeamPathSeparator())){
            this.team = getTeamPathSeparator().concat(this.team);
        }
    }
}

