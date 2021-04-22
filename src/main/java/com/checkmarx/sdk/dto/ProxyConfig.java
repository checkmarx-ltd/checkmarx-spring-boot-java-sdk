package com.checkmarx.sdk.dto;

import java.io.Serializable;

public class ProxyConfig implements Serializable {

    private String host;
    private int port;
    private String username;
    private String password;
    private boolean useHttps;
    private String noProxyHosts;

    public ProxyConfig() {
    }

    public ProxyConfig(String host, int port, String username, String password, boolean useHttps) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useHttps = useHttps;
        this.noProxyHosts = "";
    }

    public ProxyConfig(String host, int port, String username, String password, boolean useHttps, String noProxyHosts){
        this.port = port;
        this.username = username;
        this.password = password;
        this.useHttps = useHttps;
        this.noProxyHosts = noProxyHosts;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public boolean isUseHttps() {
        return useHttps;
    }

    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }

    public String getNoProxyHosts() {
        return noProxyHosts;
    }

    public void setNoProxyHosts(String noProxyHosts) {
        this.noProxyHosts = noProxyHosts;
    }
}
