package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxGoProperties;
import com.checkmarx.sdk.dto.cx.CxGoAuthResponse;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;

/**
 * Gets and stores CxGo authentication parameters.
 */
@Service
public class CxGoAuthService {
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

    private void getAuthToken(String clientSecretOverride) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl(CacheControl.noCache());
        String request = getJSONTokenReq(clientSecretOverride);
        HttpEntity<String> req = new HttpEntity<>(request, headers);
        token = restTemplate.postForObject(
                cxGoProperties.getUrl().concat(GET_SESSION_TOKEN),
                req,
                CxGoAuthResponse.class);

        final long SAFETY_INTERVAL_SECONDS = 500;
        LocalDateTime now = LocalDateTime.now();
        tokenExpires = Optional.ofNullable(token)
                .map(t -> now.plusSeconds(t.getExpiresIn() - SAFETY_INTERVAL_SECONDS)) //expire 500 seconds early
                .orElse(now);
    }

    public HttpHeaders createAuthHeaders() {
        return createAuthHeaders(null);
    }

    public HttpHeaders createAuthHeaders(String clientSecretOverride) {
        if (token == null || isTokenExpired()) {
            getAuthToken(clientSecretOverride);
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

    /**
     * Create JSON http request body for passing oAuth token to CxGo
     *
     * @return String representation of the request.
     */
    private String getJSONTokenReq(String clientSecretOverride) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("grant_type", GRANT_TYPE);
            requestBody.put("token", getEffectiveClientSecret(clientSecretOverride));
        } catch (JSONException e) {
            log.error("Error creating JSON Token Request object - JSON object will be empty");
        }
        return requestBody.toString();
    }

    private String getEffectiveClientSecret(String clientSecretOverride) {
        String result;
        if (StringUtils.isNotEmpty(clientSecretOverride)) {
            log.info("Using client secret override.");
            result = clientSecretOverride;
        } else {
            result = cxGoProperties.getClientSecret();
        }
        return result;
    }
}
