package com.checkmarx.sdk.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


@Component
@ConfigurationProperties(prefix = "checkmarx")
@Validated
public class CxProperties {
    private String username;
    private String password;
    private String clientSecret;
    private String baseUrl;
    private String url;
    private boolean multiTenant = false;
    private String teamScript;
    private String projectScript;
    private String scanPreset = Constants.CX_DEFAULT_PRESET;
    private String configuration = Constants.CX_DEFAULT_CONFIGURATION;
    private String excludeFiles;
    private String excludeFolders;
    private Boolean incremental = false;
    private Integer incrementalThreshold = 7;
    private Integer incrementalNumScans = 5;
    private String team;
    private Boolean offline = false;
    private Boolean preserveXml = false;
    private Integer scanTimeout = 120;
    private String jiraProjectField = "jira-project";
    private String jiraIssuetypeField = "jira-issuetype";
    private String jiraCustomField = "jira-fields";
    private String jiraAssigneeField = "jira-assignee";
    private Integer httpConnectionTimeout = 30000;
    private Integer httpReadTimeout = 120000;

    private String portalUrl;

    private String sdkUrl;

    private String portalWsdl;

    private String sdkWsdl;

    private String portalPackage = "checkmarx.wsdl.portal";

    private String htmlStrip = "<style>.cxtaghighlight{color: rgb(101, 170, 235);font-weight:bold;}</style>";

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
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

    public Boolean getIncremental() {
        return this.incremental;
    }

    public Integer getIncrementalThreshold() {
        return this.incrementalThreshold;
    }

    public Integer getIncrementalNumScans(){
        return this.incrementalNumScans;
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

    public Boolean getOffline() {
        return this.offline;
    }

    public Boolean getPreserveXml() {
        return preserveXml;
    }

    public void setPreserveXml(Boolean preserveXml) {
        this.preserveXml = preserveXml;
    }

    public Integer getScanTimeout() {
        return this.scanTimeout;
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

    public String getPortalUrl() {
        return this.portalUrl;
    }

    public String getSdkUrl() {
        return this.sdkUrl;
    }

    public String getPortalWsdl() {
        return this.portalWsdl;
    }

    public String getSdkWsdl() {
        return this.sdkWsdl;
    }

    public String getPortalPackage() {
        return this.portalPackage;
    }

    public String getHtmlStrip() {
        return this.htmlStrip;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setIncremental(Boolean incremental) {
        this.incremental = incremental;
    }

    public void setIncrementalThreshold(Integer incrementalThreshold){
        this.incrementalThreshold = incrementalThreshold;
    }

    public void setIncrementalNumScans(Integer incrementalNumScans){
        this.incrementalNumScans = incrementalNumScans;
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

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public void setScanTimeout(Integer scanTimeout) {
        this.scanTimeout = scanTimeout;
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

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }

    public void setSdkUrl(String sdkUrl) {
        this.sdkUrl = sdkUrl;
    }

    public void setPortalWsdl(String portalWsdl) {
        this.portalWsdl = portalWsdl;
    }

    public void setSdkWsdl(String sdkWsdl) {
        this.sdkWsdl = sdkWsdl;
    }

    public void setPortalPackage(String portalPackage) {
        this.portalPackage = portalPackage;
    }

    public void setHtmlStrip(String htmlStrip) {
        this.htmlStrip = htmlStrip;
    }

    public String getJiraAssigneeField() {
        return this.jiraAssigneeField;
    }

    public void setJiraAssigneeField(String jiraAssigneeField) {
        this.jiraAssigneeField = jiraAssigneeField;
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

    public Integer getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    public void setHttpConnectionTimeout(Integer httpConnectionTimeout) {
        this.httpConnectionTimeout = httpConnectionTimeout;
    }

    public Integer getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public void setHttpReadTimeout(Integer httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }
}

