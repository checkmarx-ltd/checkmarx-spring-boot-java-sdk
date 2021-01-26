package com.checkmarx.sdk.dto;

import com.checkmarx.sdk.dto.sca.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginSettings {
    private String accessControlBaseUrl;
    private String username;
    private String password;
    private CharSequence tenant;
    private String refreshToken;
    private final List<Cookie> sessionCookies = new ArrayList<>();
    private String version;

    // TODO: find a way to use a single client type here.
    private ClientType clientTypeForRefreshToken;
    private ClientType clientTypeForPasswordAuth;

    public void setAccessControlBaseUrl(String accessControlBaseUrl) {
        this.accessControlBaseUrl = accessControlBaseUrl;
    }

    public String getAccessControlBaseUrl() {
        return accessControlBaseUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setClientTypeForRefreshToken(ClientType clientType) {
        this.clientTypeForRefreshToken = clientType;
    }

    public ClientType getClientTypeForRefreshToken() {
        return clientTypeForRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setClientTypeForPasswordAuth(ClientType clientType) {
        this.clientTypeForPasswordAuth = clientType;
    }

    public ClientType getClientTypeForPasswordAuth() {
        return clientTypeForPasswordAuth;
    }

    public CharSequence getTenant() {
        return tenant;
    }

    public void setTenant(CharSequence tenant) {
        this.tenant = tenant;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Cookie> getSessionCookies() {
        return sessionCookies;
    }

}
