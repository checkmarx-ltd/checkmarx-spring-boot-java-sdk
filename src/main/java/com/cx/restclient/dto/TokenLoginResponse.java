package com.cx.restclient.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Galn on 19/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenLoginResponse {
    private String access_token;
    private long expires_in;
    private String token_type;
    private String refresh_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
