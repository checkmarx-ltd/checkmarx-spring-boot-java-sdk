package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.cx.CxAuthResponse;
import com.checkmarx.sdk.exception.CheckmarxLegacyException;
import com.checkmarx.sdk.exception.InvalidCredentialsException;
import com.checkmarx.sdk.utils.ScanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Class used to orchestrate submitting scans and retrieving results
 */
@Service
public class CxAuthService implements CxAuthClient{
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxAuthService.class);
    private static final String LOGIN = "/auth/identity/connect/token";
    private static final String DEFAULT_TOKEN = "DEFAULT";
    private static final Integer LEGACY_SESSION_TIME = 5;
    private final CxProperties cxProperties;
    private final CxLegacyService cxLegacyService;
    private final RestTemplate restTemplate;
    private String token = null;
    private String soapToken = null;
    private String session = null;
    private LocalDateTime tokenExpires = null;
    private LocalDateTime soapTokenExpires = null;
    private LocalDateTime sessionTokenExpires = null;

    public CxAuthService(CxProperties cxProperties, CxLegacyService cxLegacyService, @Qualifier("cxRestTemplate") RestTemplate restTemplate) {
        this.cxProperties = cxProperties;
        this.cxLegacyService = cxLegacyService;
        this.restTemplate = restTemplate;
    }


    /**
     * Get Auth Token
     */
    private void getAuthToken() {
        getAuthToken(
                cxProperties.getUsername(),
                cxProperties.getPassword(),
                cxProperties.getClientId(),
                cxProperties.getClientSecret(),
                cxProperties.getScope()
        );
    }

    /**
     * Get SOAP Auth Token
     */
    private void getSoapAuthToken() {
        getSoapAuthToken(cxProperties.getUsername(),cxProperties.getPassword());
    }

    /**
     * Get Auth Token
     */
    @Override
    public String getAuthToken(String username, String password, String clientId, String clientSecret, String scope) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");
        map.add("scope", cxProperties.getScope());
        map.add("client_id", clientId);
        if(!ScanUtils.empty(cxProperties.getClientSecret())){
            map.add("client_secret", clientSecret);
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        try {
            //get the access token
            log.info("Logging into Checkmarx {}", cxProperties.getUrl().concat(LOGIN));
            CxAuthResponse response = restTemplate.postForObject(cxProperties.getUrl().concat(LOGIN), requestEntity, CxAuthResponse.class);
            if (response == null) {
                throw new InvalidCredentialsException();
            }
            token = response.getAccessToken();
            tokenExpires = LocalDateTime.now().plusSeconds(response.getExpiresIn()-500); //expire 500 seconds early
        }
        catch (NullPointerException | HttpStatusCodeException e) {
            log.error("Error occurred white obtaining Access Token.  Possibly incorrect credentials");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new InvalidCredentialsException();
        }
        return token;
    }

    /**
     * Get Auth Token specific to SOAP API Calls
     */
    @Override
    public String getSoapAuthToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");
        map.add("scope", cxProperties.getSoapScope());
        map.add("client_id", cxProperties.getSoapClientId());
        if(!ScanUtils.empty(cxProperties.getSoapClientSecret())){
            map.add("client_secret", cxProperties.getSoapClientSecret());
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        try {
            //get the access token
            log.info("Logging into Checkmarx for SOAP token {}", cxProperties.getUrl().concat(LOGIN));
            CxAuthResponse response = restTemplate.postForObject(cxProperties.getUrl().concat(LOGIN), requestEntity, CxAuthResponse.class);
            if (response == null) {
                throw new InvalidCredentialsException();
            }
            soapToken = response.getAccessToken();
            soapTokenExpires = LocalDateTime.now().plusSeconds(response.getExpiresIn()-500); //expire 500 seconds early
        }
        catch (NullPointerException | HttpStatusCodeException e) {
            log.error("Error occurred white obtaining Access Token.  Possibly incorrect credentials");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new InvalidCredentialsException();
        }
        return soapToken;
    }


    @Override
    public String getCurrentToken(){
        return this.token;
    }

    @Override
    public String getCurrentSoapToken(){
        //get a new access token if the current one is expired.
        if (soapToken == null || isSoapTokenExpired()) {
            getSoapAuthToken();
        }
        return this.soapToken;
    }

    @Override
    public String legacyLogin(String username, String password) throws InvalidCredentialsException {
        try{
            session = cxLegacyService.login(username, password);
        }catch (CheckmarxLegacyException e){
            throw new InvalidCredentialsException();
        }
        return session;
    }

    private boolean isTokenExpired() {
        if (tokenExpires == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(tokenExpires);
    }

    private boolean isSoapTokenExpired() {
        if (soapTokenExpires == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(soapTokenExpires);
    }

    private boolean isSessionTokenExpired() {
        if (sessionTokenExpires == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(sessionTokenExpires);
    }

    public HttpHeaders createAuthHeaders() {
        //get a new access token if the current one is expired.
        if (token == null || isTokenExpired()) {
            getAuthToken();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer ".concat(token));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public String getLegacySession(){
        if(cxProperties.getVersion() >= 9.0){
            return getCurrentSoapToken();
        }
        else {
            if (this.session == null || isSessionTokenExpired()) { //refresh session
                this.session = legacyLogin(cxProperties.getUsername(), cxProperties.getPassword());
                sessionTokenExpires = LocalDateTime.now().plusHours(LEGACY_SESSION_TIME);

            }
            return this.session;
        }
    }
}
