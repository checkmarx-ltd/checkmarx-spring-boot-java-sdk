package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonProperty;


public class CxGoAuthResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("id_token")
    private String idToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    public CxGoAuthResponse(){}

    public CxGoAuthResponse(String accessToken, String idToken, Long expiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String toString() {
        return "CxAuthResponse(accessToken=XXXXX,"  + ", expiresIn=" + this.getExpiresIn() + ", tokenType=" + this.getTokenType() + ")";
    }

}
