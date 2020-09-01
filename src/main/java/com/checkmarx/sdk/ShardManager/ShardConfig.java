package com.checkmarx.sdk.ShardManager;

public class ShardConfig {
    private String name = "Unknown";
    private String url = "";
    private int projectLimit = 1;
    private int teamLimit = 1;
    private int isDisabled = 0;
    private int isCredentialOverride = 0;
    private String username = "";
    private String password = "";
    private int isTeamOverride = 0;
    private String team = "";
    private int forceSettingReload = 0;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProjectLimit() {
        return this.projectLimit;
    }

    public void setProjectLimit(int projectLimit) {
        this.projectLimit = projectLimit;
    }

    public int getTeamLimit() {
        return this.teamLimit;
    }

    public void setTeamLimit(int teamLimit) {
        this.teamLimit = teamLimit;
    }

    public int getIsDisabled() {
        return this.isDisabled;
    }

    public void setIsDisabled(int isDisabled) {
        this.isDisabled = isDisabled;
    }

    public int getIsCredentialOverride() {
        return this.isCredentialOverride;
    }

    public void setIsCredentialOverride(int isCredentialOverride) {
        this.isCredentialOverride = isCredentialOverride;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsTeamOverride() {
        return this.isTeamOverride;
    }

    public void setIsTeamOverride(int isTeamOverride) {
        this.isTeamOverride = isTeamOverride;
    }

    public String getTeam() {
        return this.team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getForceSettingReload() {
        return this.forceSettingReload;
    }

    public void setForceSettingReload(int forceSettingReload) {
        this.forceSettingReload = forceSettingReload;
    }
}
