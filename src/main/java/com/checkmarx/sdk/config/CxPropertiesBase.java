package com.checkmarx.sdk.config;

import com.checkmarx.sdk.dto.cx.CxEmailNotifications;
import javax.annotation.PostConstruct;

public abstract class CxPropertiesBase {

    private Double version = 8.9;
    protected String clientSecret;
    private String baseUrl;
    private String url;
    private boolean multiTenant = true;
    private String scanPreset = Constants.CX_DEFAULT_PRESET;
    private String configuration = Constants.CX_DEFAULT_CONFIGURATION;
    private String gitClonePath;
    private String team;
    private String jiraProjectField = "jira-project";
    private String jiraIssuetypeField = "jira-issuetype";
    private String jiraCustomField = "jira-fields";
    private String jiraAssigneeField = "jira-assignee";
    private Integer scanTimeout = 120;
    private Integer scanPolling = 20000;
    private String TEAM_PATH_SEPARATOR_9 = "/";
    private String TEAM_PATH_SEPARATOR_8 = "\\";
    private String portalUrl;
    private Boolean enableShardManager = false;
    private Boolean incremental = false;
    private String excludeFiles;
    private String excludeFolders;
    private Boolean offline = false;
    private String teamScript;
    private String projectScript;
    private Boolean enablePostActionMonitor = false;
    private String postCloneScript;
    private Boolean enablePostActionEvent = false;
    private CxEmailNotifications emailNotifications;
    private String detectionDateFormat = "M/d/y h:m:s a";

    private Boolean scanQueuing = false;
    private Integer scanQueuingTimeout = 720;

    private Integer projectBranchingCheckCount = 3;
    private Integer projectBranchingCheckInterval = 5;
    private Boolean restrictResultsToBranch = false;
    
    public abstract Boolean getEnableOsa();

    public Integer getScanQueuingTimeout() {
        return scanQueuingTimeout;
    }

    public void setScanQueuingTimeout(Integer scanQueuingTimeout) {
        this.scanQueuingTimeout = scanQueuingTimeout;
    }

    public Boolean getScanQueuing() {return scanQueuing;}

    public void setScanQueuing(Boolean scanQueuing) {this.scanQueuing = scanQueuing;}


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

    public boolean getEnableShardManager() {
        return this.enableShardManager;
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
            return getDefaultOsPath();
        }
        else {
            return this.gitClonePath;
        }
    }

    public static String getDefaultOsPath() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
            return Constants.WINDOWS_PATH;
        } else {
            // everything else
            return Constants.UNIX_PATH;
        }
    }

    public void setEnableShardManager(boolean enableShardManager) {
        this.enableShardManager = enableShardManager;
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

    public void setJiraProjectField(String jiraProjectField) {
        this.jiraProjectField = jiraProjectField;
    }

    public void setJiraIssuetypeField(String jiraIssuetypeField) {
        this.jiraIssuetypeField = jiraIssuetypeField;
    }

    public void setJiraCustomField(String jiraCustomField) {
        this.jiraCustomField = jiraCustomField;
    }

    public String getJiraProjectField() {
        return this.jiraProjectField;
    }

    public String getJiraCustomField() {
        return this.jiraCustomField;
    }

    public String getJiraIssuetypeField() {
        return this.jiraIssuetypeField;
    }

    public String getJiraAssigneeField() {
        return this.jiraAssigneeField;
    }

    public void setJiraAssigneeField(String jiraAssigneeField) {
        this.jiraAssigneeField = jiraAssigneeField;
    }

    public void setIncremental(Boolean incremental) {
        this.incremental = incremental;
    }
    
    public Boolean getIncremental() {
        return this.incremental;
    }

    public String getExcludeFiles() {
        return excludeFiles;
    }

    public void setExcludeFiles(String excludeFiles) {
        this.excludeFiles = excludeFiles;
    }

    public String getExcludeFolders() {
        return excludeFolders;
    }

    public void setExcludeFolders(String excludeFolders) {
        this.excludeFolders = excludeFolders;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public Boolean getOffline() {
        return this.offline;
    }

    public String getTeamScript() {
        return teamScript;
    }

    public void setTeamScript(String teamScript) {
        this.teamScript = teamScript;
    }

    public String getProjectScript() {
        return projectScript;
    }

    public void setProjectScript(String projectScript) {
        this.projectScript = projectScript;
    }

    public boolean getEnablePostActionMonitor() {
        return this.enablePostActionMonitor;
    }

    public void setEnablePostActionMonitor(Boolean enablePostActionMonitor) {
        this.enablePostActionMonitor = enablePostActionMonitor;
    }

    public boolean getEnablePostActionEvent() {
        return this.enablePostActionEvent;
    }

    public void setEnablePostActionEvent(Boolean enablePostActionEvent) {
        this.enablePostActionEvent = enablePostActionEvent;
    }

    public String getPostCloneScript() {
        return postCloneScript;
    }

    public void setPostCloneScript(String postCloneScript) {
        this.postCloneScript = postCloneScript;
    }

    public CxEmailNotifications getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(CxEmailNotifications emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public String getDetectionDateFormat() { return this.detectionDateFormat; }

    public void setDetectionDateFormat(String detectionDateFormat) {
        this.detectionDateFormat = detectionDateFormat;
    }

    public Integer getProjectBranchingCheckCount() {
        return projectBranchingCheckCount;
    }

    public void setProjectBranchingCheckCount(Integer projectBranchingCheckCount) {
        this.projectBranchingCheckCount = projectBranchingCheckCount;
    }

    public Integer getProjectBranchingCheckInterval() {
        return projectBranchingCheckInterval;
    }

    public void setProjectBranchingCheckInterval(Integer projectBranchingCheckInterval) {
        this.projectBranchingCheckInterval = projectBranchingCheckInterval;
    }

    public Boolean getRestrictResultsToBranch() { return this.restrictResultsToBranch; }

    public void setRestrictResultsToBranch(Boolean restrictResultsToBranch) {
        this.restrictResultsToBranch = restrictResultsToBranch;
    }
}

