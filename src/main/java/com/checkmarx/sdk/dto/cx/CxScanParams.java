package com.checkmarx.sdk.dto.cx;

import java.util.Map;

public class CxScanParams {
    private String teamName;
    private String projectName;
    private boolean incremental = false;
    private boolean isPublic = true;
    private boolean forceScan = false;
    private String fileExclude;
    private String folderExclude;
    private String scanPreset;
    private String scanConfiguration;
    private Type sourceType = Type.GIT;
    private String gitUrl;
    private String filePath; //Only used if Type.FILE is used
    //TODO add custom fields
    private Map<String, String> customFields;
    //TODO add add post actions
    private String postAction;

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

    public String getFileExclude() {
        return fileExclude;
    }

    public void setFileExclude(String fileExclude) {
        this.fileExclude = fileExclude;
    }

    public String getFolderExclude() {
        return folderExclude;
    }

    public void setFolderExclude(String folderExclude) {
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public CxScanParams withIncremental(boolean incremental) {
        this.incremental = incremental;
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

    public CxScanParams withFileExclude(String fileExclude) {
        this.fileExclude = fileExclude;
        return this;
    }

    public CxScanParams withFolderExclude(String folderExclude) {
        this.folderExclude = folderExclude;
        return this;
    }

    public CxScanParams withScanPreset(String scanPreset) {
        this.scanPreset = scanPreset;
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
}
