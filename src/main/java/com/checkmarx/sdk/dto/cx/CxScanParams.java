package com.checkmarx.sdk.dto.cx;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class CxScanParams {
    private String teamId;
    private String teamName;
    private Integer projectId;
    private String projectName;
    private boolean incremental = false;
    private boolean isPublic = true;
    private boolean forceScan = false;
    private List<String> fileExclude;
    private List<String> folderExclude;
    private Integer scanPresetId;
    private String scanPreset;
    private String scanConfiguration;
    private Type sourceType = Type.GIT;
    private String gitUrl;
    private String branch;
    private String defaultBranch;
    private String sshKeyIdentifier;
    private Boolean preserveProjectName;
    private String filePath; //Only used if Type.FILE is used
    //TODO add custom fields
    private Map<String, String> customFields;
    private Map<String, String> scanCustomFields;
    //TODO add add post actions
    private String postAction;
    // Email notifications
    private CxEmailNotifications emailNotifications;

    @Getter
    private String clientSecret;

    @Getter @Setter
    private String modifiedProjectName;

    @Getter @Setter
    private Integer postBackActionId;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public boolean isForceScan() {
        return forceScan;
    }

    public void setForceScan(boolean forceScan) {
        this.forceScan = forceScan;
    }

    public List<String> getFileExclude() {
        return fileExclude;
    }

    public void setFileExclude(List<String> fileExclude) {
        this.fileExclude = fileExclude;
    }

    public List<String> getFolderExclude() {
        return folderExclude;
    }

    public void setFolderExclude(List<String> folderExclude) {
        this.folderExclude = folderExclude;
    }

    public String getScanPreset() {
        return scanPreset;
    }

    public void setScanPreset(String scanPreset) {
        this.scanPreset = scanPreset;
    }

    public String getScanConfiguration() {
        return scanConfiguration;
    }

    public void setScanConfiguration(String scanConfiguration) {
        this.scanConfiguration = scanConfiguration;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public Map<String, String> getScanCustomFields() {
        return scanCustomFields;
    }

    public void setScanCustomFields(Map<String, String> scanCustomFields) {
        this.scanCustomFields = scanCustomFields;
    }

    public String getPostAction() {
        return postAction;
    }

    public void setPostAction(String postAction) {
        this.postAction = postAction;
    }

    public Type getSourceType() {
        return sourceType;
    }

    public void setSourceType(Type sourceType) {
        this.sourceType = sourceType;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getScanPresetId() {
        return scanPresetId;
    }

    public void setScanPresetId(Integer scanPresetId) {
        this.scanPresetId = scanPresetId;
    }

    public void setSshKeyIdentifier(String sshKeyIdentifier) {
        this.sshKeyIdentifier = sshKeyIdentifier;
    }

    public String getSshKeyIdentifier() {
        return sshKeyIdentifier;
    }

    public Boolean getPreserveProjectName() { return preserveProjectName; }

    public void setPreserveProjectName(Boolean preserveProjectName) { this.preserveProjectName = preserveProjectName; }

    public CxEmailNotifications getEmailNotifications() { return emailNotifications; }

    public void setEmailNotifications(CxEmailNotifications emailNotifications) { this.emailNotifications = emailNotifications; }

    public CxScanParams withIncremental(boolean incremental) {
        this.incremental = incremental;
        return this;
    }

    public CxScanParams withSshKeyIdentifier(String sshKeyIdentifier) {
        this.sshKeyIdentifier = sshKeyIdentifier;
        return this;
    }

    public CxScanParams withIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public CxScanParams withForceScan(boolean forceScan) {
        this.forceScan = forceScan;
        return this;
    }

    public CxScanParams withFileExclude(List<String> fileExclude) {
        this.fileExclude = fileExclude;
        return this;
    }

    public CxScanParams withFolderExclude(List<String> folderExclude) {
        this.folderExclude = folderExclude;
        return this;
    }

    public CxScanParams withScanPreset(String scanPreset) {
        this.scanPreset = scanPreset;
        return this;
    }

    public CxScanParams withModifiedProjectName(String modifiedProjectName) {
        this.modifiedProjectName = modifiedProjectName;
        return this;
    }

    public CxScanParams withScanConfiguration(String scanConfiguration) {
        this.scanConfiguration = scanConfiguration;
        return this;
    }

    public CxScanParams withCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
        return this;
    }

    public CxScanParams withScanCustomFields(Map<String, String> scanCustomFields) {
        this.scanCustomFields = scanCustomFields;
        return this;
    }

    public CxScanParams withPostAction(String postAction) {
        this.postAction = postAction;
        return this;
    }

    public CxScanParams withSourceType(Type sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public CxScanParams withGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    public CxScanParams withBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public CxScanParams withDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
        return this;
    }

    public CxScanParams withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public CxScanParams withTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public CxScanParams withProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public CxScanParams withClientSecret(String value) {
        this.clientSecret = value;
        return this;
    }


    public CxScanParams teamId(final String teamId) {
        this.teamId = teamId;
        return this;
    }

    public CxScanParams projectId(final Integer projectId) {
        this.projectId = projectId;
        return this;
    }

    public CxScanParams scanPresetId(final Integer scanPresetId) {
        this.scanPresetId = scanPresetId;
        return this;
    }

    public CxScanParams withPreserveProjectName(Boolean preserveProjectName){
        this.preserveProjectName = preserveProjectName;
        return this;
    }

    public CxScanParams withEmailNotifications(CxEmailNotifications emailNotifications) {
        this.emailNotifications = emailNotifications;
        return this;
    }

    public CxScanParams withPostbackActionId(Integer postbackActionId) {
        this.postBackActionId = postbackActionId;
        return this;
    }

    public enum Type {
        GIT("GIT"),
        FILE("FILE");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public boolean isGitSource(){
        return getSourceType().equals(Type.GIT);
    }
    public boolean isFileSource(){
        return getSourceType().equals(Type.FILE);
    }

    @Override
    public String toString() {
        return "CxScanParams{" +
                "teamName='" + teamName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", incremental=" + incremental +
                ", isPublic=" + isPublic +
                ", forceScan=" + forceScan +
                ", fileExclude=" + fileExclude +
                ", folderExclude=" + folderExclude +
                ", scanPreset='" + scanPreset + '\'' +
                ", scanConfiguration='" + scanConfiguration + '\'' +
                ", sourceType=" + sourceType +
                ", gitUrl='" + gitUrl + '\'' +
                ", filePath='" + filePath + '\'' +
                ", customFields=" + customFields +
                ", scanCustomFields=" + scanCustomFields +
                ", postAction='" + postAction + '\'' +
                ", emailNotifications='" + emailNotifications + '\'' +
                '}';
    }
}
