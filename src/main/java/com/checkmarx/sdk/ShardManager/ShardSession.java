package com.checkmarx.sdk.ShardManager;

import org.springframework.ws.client.core.WebServiceTemplate;

import java.time.LocalDateTime;

public class ShardSession {
    private WebServiceTemplate ws = null;
    private WebServiceTemplate shardWs = null;
    private String soapToken = "";
    private LocalDateTime soapTokenExpires = null;
    private String name = "";
    private String team = "";
    private String project = "";
    private boolean shardFound = false;
    private String url = "";
    private boolean isCredentialOverride = false;
    private String username = "";
    private String password = "";
    private String accessToken = "";
    private LocalDateTime tokenExpires = null;

    public ShardSession(WebServiceTemplate ws) {
        this.ws = ws;
    }

    private void createShardWs() {
        shardWs = new WebServiceTemplate();
        shardWs.setDefaultUri(this.url + "/cxwebinterface/Portal/CxWebService.asmx");
        shardWs.setMarshaller(ws.getMarshaller());
        shardWs.setUnmarshaller(ws.getUnmarshaller());
    }

    public WebServiceTemplate getShardWs() {
        return shardWs;
    }

    public String getSoapToken() {
        return soapToken;
    }

    public void setSoapToken(String soapToken) {
        this.soapToken = soapToken;
    }

    public LocalDateTime getSoapTokenExpires() {
        return soapTokenExpires;
    }

    public void setSoapTokenExpires(LocalDateTime soapTokenExpires) {
        this.soapTokenExpires = soapTokenExpires;
    }

    public boolean getShardFound() {
        return shardFound;
    }

    public void setShardFound(boolean shardFound) {
        this.shardFound = shardFound;
    }

    public boolean getIsCredentialOverride() {
        return isCredentialOverride;
    }

    public void setIsCredentialOverride(boolean isCredentialOverride) {
        this.isCredentialOverride = isCredentialOverride;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(LocalDateTime tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        createShardWs();
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}