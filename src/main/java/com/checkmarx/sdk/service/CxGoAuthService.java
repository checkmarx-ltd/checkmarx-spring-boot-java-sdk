package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxGoProperties;
import com.checkmarx.sdk.dto.cx.CxGoAuthResponse;
import com.checkmarx.sdk.exception.InvalidCredentialsException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * Class used to orchestrate submitting scans and retrieving results
 */
@Service
public class CxGoAuthService implements CxAuthClient {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxGoAuthService.class);
    private final CxGoProperties cxGoProperties;
    private final RestTemplate restTemplate;
    private CxGoAuthResponse token = null;
    private LocalDateTime tokenExpires = null;
    //
    /// REST API end-points
    //
    private static final String GET_SESSION_TOKEN = "/v1/auth/login";
    private static final String GRANT_TYPE = "client_credentials";

    public CxGoAuthService(CxGoProperties cxGoProperties, @Qualifier("cxRestTemplate") RestTemplate restTemplate) {
        this.cxGoProperties = cxGoProperties;
        this.restTemplate = restTemplate;
    }

    private void getAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl(CacheControl.noCache());
        HttpEntity req = new HttpEntity<>(getJSONTokenReq(), headers);
        token = restTemplate.postForObject(
                cxGoProperties.getUrl().concat(GET_SESSION_TOKEN),
                req,
                CxGoAuthResponse.class);
        tokenExpires = LocalDateTime.now().plusSeconds(token.getExpiresIn()-500); //expire 500 seconds early
    }

    @Override
    public HttpHeaders createAuthHeaders() {
        if (token == null || isTokenExpired()) {
            getAuthToken();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setCacheControl(CacheControl.noCache());
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer ".concat(token.getIdToken()));
        return httpHeaders;
    }

    private boolean isTokenExpired() {
        if (tokenExpires == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(tokenExpires);
    }

    @Override
    public String getCurrentToken() {
        return token.getAccessToken();
    }

    /**
     * Create JSON http request body for passing oAuth token to CxOD
     *
     * @return String representation of the token
     */
    private String getJSONTokenReq() {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("grant_type", GRANT_TYPE);
            requestBody.put("token", cxGoProperties.getClientSecret());
        } catch (JSONException e) {
            log.error("Error creating JSON Token Request object - JSON object will be empty");
        }
        return requestBody.toString();
    }

    @Override
    public String getAuthToken(String username, String password, String clientId, String clientSecret, String scope) throws InvalidCredentialsException {
        return null;
    }

    // TODO: jeffa, I'm not sure if this is required anymore?
    @Override
    public String getSoapAuthToken(String username, String password) throws InvalidCredentialsException {
        return null;
    }

    // TODO: jeffa, I'm not sure if this is required anymore?
    @Override
    public String legacyLogin(String username, String password) throws InvalidCredentialsException {
        return null;
    }

    // TODO: jeffa, I'm not sure if this is required anymore?
    @Override
    public String getCurrentSoapToken() {
        return null;
    }

    // TODO: jeffa, I'm not sure if this is required anymore?
    @Override
    public String getLegacySession() {
        return null;
    }
}
