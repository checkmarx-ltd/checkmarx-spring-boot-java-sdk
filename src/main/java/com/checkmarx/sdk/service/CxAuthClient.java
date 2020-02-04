package com.checkmarx.sdk.service;

import com.checkmarx.sdk.exception.InvalidCredentialsException;
import org.springframework.http.HttpHeaders;


/**
 * Class used to orchestrate submitting scans and retrieving results
 */
public interface CxAuthClient {

    /**
     * Authenictate with Checkmarx and Creates a JWT/OIDC access token for Checkmarx REST based resource
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    public String getAuthToken(String username, String password, String clientId, String clientSecret, String scope) throws InvalidCredentialsException;

    /**
     * Authenictate with Checkmarx and Creates a JWT/OIDC for access token for Checkmarx SOAP based resource (9.0 onward)
     *
     * @param username
     * @param password
     * @return
     */
    public String getSoapAuthToken(String username, String password) throws InvalidCredentialsException;

    /**
     * Authenictate with Checkmarx and Creates a session to access Checkmarx Legacy SOAP based resource
     *
     * @param username
     * @param password
     * @return
     * @throws InvalidCredentialsException
     */
    public String legacyLogin(String username, String password) throws InvalidCredentialsException;

    /**
     * Create REST API Headers for Authentication (JWT/OIDC)
     *
     * @return
     */
    public HttpHeaders createAuthHeaders();

    public String getCurrentToken();

    public String getCurrentSoapToken();

    public String getLegacySession();

}
