package com.checkmarx.sdk.utils.scanner.client.httpClient;

import com.checkmarx.sdk.exception.ScannerRuntimeException;

import com.checkmarx.sdk.dto.sca.ClientType;
import com.checkmarx.sdk.dto.LoginSettings;
import com.checkmarx.sdk.dto.TokenLoginResponse;
import com.checkmarx.sdk.exception.CxHTTPClientException;
import com.checkmarx.sdk.exception.CxTokenExpiredException;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.win.WindowsNTLMSchemeFactory;
import org.apache.http.impl.auth.win.WindowsNegotiateSchemeFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;

import static com.checkmarx.sdk.config.ContentType.CONTENT_TYPE_APPLICATION_JSON;


/**
 * Created by Galn on 05/02/2018.
 */
public class CxHttpClient implements Closeable {

    private static final String HTTPS = "https";

    private static final String LOGIN_FAILED_MSG = "Fail to login with windows authentication: ";
    public static final String CSRF_TOKEN_HEADER = "CXCSRFToken";
    private static final String DEFAULT_GRANT_TYPE = "password";
    private static final String LOCATION_HEADER = "Location";
    private static final String AUTH_MESSAGE = "authenticate";
    private static final String CLIENT_SECRET_PROP = "client_secret";
    public static final String REFRESH_TOKEN_PROP = "refresh_token";
    private static final String PASSWORD_PROP = "password";
    public static final String CLIENT_ID_PROP = "client_id";
    private static final String KEY_USER = "user";
    private static final String KEY_DOMAIN = "domain";
    public static final String SSO_AUTHENTICATION = "auth/identity/externalLogin";
    public static final String REVOCATION = "auth/identity/connect/revocation";
    public static final String ORIGIN_HEADER = "cxOrigin";
    public static final String TEAM_PATH = "cxTeamPath";
    private HttpClient apacheClient;

    private Logger log;
    private TokenLoginResponse token;
    private String rootUri;
    private LoginSettings lastLoginSettings;
    private String teamPath;
    private CookieStore cookieStore = new BasicCookieStore();
    private HttpClientBuilder cb = HttpClients.custom();
    private final Map<String, String> customHeaders = new HashMap<>();


    public CxHttpClient(String rootUri, boolean disableSSLValidation, 
                         Logger log) throws ScannerRuntimeException {
        this.log = log;
        this.rootUri = rootUri;
        //create httpclient
        cb.setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build());
        setSSLTls("TLSv1.2", log);
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        Registry<ConnectionSocketFactory> registry;
        PoolingHttpClientConnectionManager cm = null;
        if (disableSSLValidation) {
            try {
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
                registry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register(HTTPS, sslConnectionSocketFactory)
                        .build();
                cm = new PoolingHttpClientConnectionManager(registry);
                cm.setMaxTotal(100);
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                log.error(e.getMessage());
            }
            cb.setSSLSocketFactory(sslConnectionSocketFactory);
            cb.setConnectionManager(cm);
        } else {
            cb.setConnectionManager(getHttpConnectionManager(false));
        }
        cb.setConnectionManagerShared(true);
        
        cb.setConnectionReuseStrategy(new NoConnectionReuseStrategy());
        cb.setDefaultAuthSchemeRegistry(getAuthSchemeProviderRegistry());
        apacheClient = cb.build();
}

    public void setRootUri(String rootUri) {
        this.rootUri = rootUri;
    }

    public String getRootUri() {
        return rootUri;
    }
    

    private static HashMap<String,String> splitDomainAndTheUserName(String userName)
    {
        String domain="";
        String user="";
        // If the username has a backslash, then the domain is the first part and the username is the second part
        if (userName.contains("\\")) {
            String[] parts = userName.split("[\\\\]");
            if (parts.length == 2) {
                domain = parts[0];
                user = parts[1];
            }
        } else if (userName.contains("/")) {
            // If the username has a slash, then the domain is the first part and the username is the second part
            String[] parts = userName.split("[/]");
            if (parts.length == 2) {
                domain = parts[0];
                user = parts[1];
            }
        } else if (userName.contains("@")) {
            // If the username has an asterisk, then the domain is the second part and the username is the first part
            String[] parts = userName.split("[@]");
            if (parts.length == 2) {
                user = parts[0];
                domain = parts[1];
            }
        }

        HashMap<String,String> userDomain = new HashMap<String,String>();
        userDomain.put(KEY_USER,user);
        userDomain.put(KEY_DOMAIN, domain);
        return userDomain;
    }

    private static SSLConnectionSocketFactory getTrustAllSSLSocketFactory() {
        TrustStrategy acceptingTrustStrategy = new TrustAllStrategy();
        SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new ScannerRuntimeException("Fail to set trust all certificate, 'SSLConnectionSocketFactory'", e);
        }
        return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    private static PoolingHttpClientConnectionManager getHttpConnectionManager(boolean disableSSLValidation) {
        ConnectionSocketFactory factory;
        if (disableSSLValidation) {
            factory = getTrustAllSSLSocketFactory();
        } else {
            factory = new SSLConnectionSocketFactory(SSLContexts.createDefault());
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTPS, factory)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connManager.setMaxTotal(50);
        connManager.setDefaultMaxPerRoute(5);
        return connManager;
    }

    private static Registry<AuthSchemeProvider> getAuthSchemeProviderRegistry() {
        return RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .register(AuthSchemes.NTLM, new WindowsNTLMSchemeFactory(null))
                .register(AuthSchemes.SPNEGO, new WindowsNegotiateSchemeFactory(null))
                .build();
    }

    public void login(LoginSettings settings) throws IOException {
        lastLoginSettings = settings;

        if (!settings.getSessionCookies().isEmpty()) {
            setSessionCookies(settings.getSessionCookies());
            return;
        }

        if (settings.getRefreshToken() != null) {
            token = getAccessTokenFromRefreshToken(settings);
        } else {
            token = generateToken(settings);
        }
    }

    public ArrayList<Cookie> ssoLegacyLogin() {
        HttpUriRequest request;
        HttpResponse loginResponse = null;

        try {
            request = RequestBuilder.post()
                    .setUri(rootUri + "auth/ssologin")
                    .setConfig(RequestConfig.DEFAULT)
                    .setEntity(new StringEntity("", StandardCharsets.UTF_8))
                    .build();

            loginResponse = apacheClient.execute(request);

        } catch (IOException e) {
            String message = LOGIN_FAILED_MSG + e.getMessage();
            log.error(message);
            throw new ScannerRuntimeException(message);
        } finally {
            HttpClientUtils.closeQuietly(loginResponse);
        }
        setSessionCookies(cookieStore.getCookies());

        //return cookies clone - for IDE's usage
        return new ArrayList<>(cookieStore.getCookies());
    }

    private void setSessionCookies(List<Cookie> cookies) {
        String cxCookie = null;
        String csrfToken = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(CSRF_TOKEN_HEADER)) {
                csrfToken = cookie.getValue();
            }
            if (cookie.getName().equals("cxCookie")) {
                cxCookie = cookie.getValue();
            }
        }

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(CSRF_TOKEN_HEADER, csrfToken));
        headers.add(new BasicHeader("cookie", String.format("CXCSRFToken=%s; cxCookie=%s", csrfToken, cxCookie)));

        // Don't delete these prints, they are being used on VS Code plugin
        System.out.println(CSRF_TOKEN_HEADER + ": " + csrfToken);
        System.out.printf("cookie: CXCSRFToken=%s; cxCookie=%s%n", csrfToken, cxCookie);

        apacheClient = cb.setDefaultHeaders(headers).build();
    }

    private TokenLoginResponse ssoLogin() {
        HttpUriRequest request;
        HttpResponse response;
        final String BASE_URL = "/auth/identity/";

        RequestConfig requestConfig = RequestConfig.custom()
                .setRedirectsEnabled(false)
                .setAuthenticationEnabled(true)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        try {
            //Request1
            request = RequestBuilder.post()
                    .setUri(rootUri + SSO_AUTHENTICATION)
                    .setConfig(requestConfig)
                    .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString())
                    .setEntity(generateSSOEntity())
                    .build();

            response = apacheClient.execute(request);

            //Request2
            String cookies = retrieveCookies();
            String redirectURL = response.getHeaders(LOCATION_HEADER)[0].getValue();
            request = RequestBuilder.get()
                    .setUri(rootUri + BASE_URL + redirectURL)
                    .setConfig(requestConfig)
                    .setHeader("Cookie", cookies)
                    .setHeader("Upgrade-Insecure-Requests", "1")
                    .build();
            response = apacheClient.execute(request);

            //Request3
            cookies = retrieveCookies();
            redirectURL = response.getHeaders(LOCATION_HEADER)[0].getValue();
            redirectURL = rootUri + redirectURL.replace("/CxRestAPI/", "");
            request = RequestBuilder.get()
                    .setUri(redirectURL)
                    .setConfig(requestConfig)
                    .setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString())
                    .setHeader("Cookie", cookies)
                    .build();
            response = apacheClient.execute(request);
            return extractToken(response);
        } catch (IOException e) {
            throw new ScannerRuntimeException(LOGIN_FAILED_MSG + e.getMessage());
        }
    }

    private TokenLoginResponse extractToken(HttpResponse response) {
        String redirectURL = response.getHeaders(LOCATION_HEADER)[0].getValue();
        if (!redirectURL.contains("access_token")) {
            throw new ScannerRuntimeException("Failed retrieving access token from server");
        }
        return new Gson().fromJson(urlToJson(redirectURL), TokenLoginResponse.class);
    }

    private String urlToJson(String url) {
        url = url.replace("=", "\":\"");
        url = url.replace("&", "\",\"");
        return "{\"" + url + "\"}";
    }

    private String retrieveCookies() {
        List<Cookie> cookieList = cookieStore.getCookies();
        final StringBuilder builder = new StringBuilder();
        cookieList.forEach(cookie ->
                builder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";"));
        return builder.toString();
    }

    public TokenLoginResponse generateToken(LoginSettings settings) throws IOException {
        UrlEncodedFormEntity requestEntity = getAuthRequest(settings);
        HttpPost post = new HttpPost(settings.getAccessControlBaseUrl());
        try {
            return request(post, ContentType.APPLICATION_FORM_URLENCODED.toString(), requestEntity,
                    TokenLoginResponse.class, HttpStatus.SC_OK, AUTH_MESSAGE, false, false);
        } catch (ScannerRuntimeException e) {
            if (!e.getMessage().contains("invalid_scope")) {
                throw new ScannerRuntimeException(String.format("Failed to generate access token, failure error was: %s", e.getMessage()), e);
            }
            ClientType.RESOURCE_OWNER.setScopes("sast_rest_api");
            settings.setClientTypeForPasswordAuth(ClientType.RESOURCE_OWNER);
            requestEntity = getAuthRequest(settings);
            return request(post, ContentType.APPLICATION_FORM_URLENCODED.toString(), requestEntity,
                    TokenLoginResponse.class, HttpStatus.SC_OK, AUTH_MESSAGE, false, false);
        }
    }

    private TokenLoginResponse getAccessTokenFromRefreshToken(LoginSettings settings) throws IOException {
        UrlEncodedFormEntity requestEntity = getTokenRefreshingRequest(settings);
        HttpPost post = new HttpPost(settings.getAccessControlBaseUrl());
        try {
            return request(post, ContentType.APPLICATION_FORM_URLENCODED.toString(), requestEntity,
                    TokenLoginResponse.class, HttpStatus.SC_OK, AUTH_MESSAGE, false, false);
        } catch (ScannerRuntimeException e) {
            throw new ScannerRuntimeException(String.format("Failed to generate access token from refresh token. The error was: %s", e.getMessage()), e);
        }
    }

    public void revokeToken(String token) throws IOException {
        UrlEncodedFormEntity requestEntity = getRevocationRequest(ClientType.CLI, token);
        HttpPost post = new HttpPost(rootUri + REVOCATION);
        try {
            request(post, ContentType.APPLICATION_FORM_URLENCODED.toString(), requestEntity,
                    String.class, HttpStatus.SC_OK, "revocation", false, false);
        } catch (ScannerRuntimeException e) {
            throw new ScannerRuntimeException(String.format("Token revocation failure error was: %s", e.getMessage()), e);
        }
    }

    private static UrlEncodedFormEntity getRevocationRequest(ClientType clientType, String token) {
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("token_type_hint", REFRESH_TOKEN_PROP));
        parameters.add(new BasicNameValuePair("token", token));
        parameters.add(new BasicNameValuePair(CLIENT_ID_PROP, clientType.getClientId()));
        parameters.add(new BasicNameValuePair(CLIENT_SECRET_PROP, clientType.getClientSecret()));

        return new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
    }

    private static UrlEncodedFormEntity getAuthRequest(LoginSettings settings) {
        ClientType clientType = settings.getClientTypeForPasswordAuth();
        String grantType = StringUtils.defaultString(clientType.getGrantType(), DEFAULT_GRANT_TYPE);
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("username", settings.getUsername()));
        parameters.add(new BasicNameValuePair(PASSWORD_PROP, settings.getPassword()));
        parameters.add(new BasicNameValuePair("grant_type", grantType));
        parameters.add(new BasicNameValuePair("scope", clientType.getScopes()));
        parameters.add(new BasicNameValuePair(CLIENT_ID_PROP, clientType.getClientId()));
        parameters.add(new BasicNameValuePair(CLIENT_SECRET_PROP, clientType.getClientSecret()));

        if (!StringUtils.isEmpty(settings.getTenant())) {
            String authContext = String.format("Tenant:%s", settings.getTenant());
            parameters.add(new BasicNameValuePair("acr_values", authContext));
        }

        return new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8);
    }

    private static UrlEncodedFormEntity getTokenRefreshingRequest(LoginSettings settings) throws UnsupportedEncodingException {
        ClientType clientType = settings.getClientTypeForRefreshToken();
        List<BasicNameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("grant_type", REFRESH_TOKEN_PROP));
        parameters.add(new BasicNameValuePair(CLIENT_ID_PROP, clientType.getClientId()));
        parameters.add(new BasicNameValuePair(CLIENT_SECRET_PROP, clientType.getClientSecret()));
        parameters.add(new BasicNameValuePair(REFRESH_TOKEN_PROP, settings.getRefreshToken()));

        return new UrlEncodedFormEntity(parameters, StandardCharsets.UTF_8.name());
    }

    //GET REQUEST
    public <T> T getRequest(String relPath, String contentType, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection) throws IOException {
        return getRequest(rootUri, relPath, CONTENT_TYPE_APPLICATION_JSON, contentType, responseType, expectStatus, failedMsg, isCollection);
    }

    public <T> T getRequest(String rootURL, String relPath, String acceptHeader, String contentType, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection) throws IOException {
        HttpGet get = new HttpGet(rootURL + relPath);
        get.addHeader(HttpHeaders.ACCEPT, acceptHeader);
        return request(get, contentType, null, responseType, expectStatus, "get " + failedMsg, isCollection, true);
    }

    //POST REQUEST
    public <T> T postRequest(String relPath, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg) throws IOException {
        HttpPost post = new HttpPost(rootUri + relPath);
        return request(post, contentType, entity, responseType, expectStatus, failedMsg, false, true);
    }

    //PUT REQUEST
    public <T> T putRequest(String relPath, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg) throws IOException {
        HttpPut put = new HttpPut(rootUri + relPath);
        return request(put, contentType, entity, responseType, expectStatus, failedMsg, false, true);
    }

    //PATCH REQUEST
    public void patchRequest(String relPath, String contentType, HttpEntity entity, int expectStatus, String failedMsg) throws IOException {
        HttpPatch patch = new HttpPatch(rootUri + relPath);
        request(patch, contentType, entity, null, expectStatus, failedMsg, false, true);
    }

    public void deleteRequest(String relPath, int expectStatus, String failedMsg) throws IOException {
        HttpDelete httpDelete = new HttpDelete(rootUri + relPath);
        request(httpDelete, null, null, null, expectStatus, failedMsg, false, false);
    }

    public void setTeamPathHeader(String teamPath) {
        this.teamPath = teamPath;
    }

    public void setCustomHeader(String name, String value) {
        log.debug(String.format("Adding a custom header: %s: %s", name, value));
        customHeaders.put(name, value);
    }

    
    private <T> T request(HttpRequestBase httpMethod, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection, boolean retry) throws IOException {
        if (contentType != null) {
            httpMethod.addHeader("Content-type", contentType);
        }
        if (entity != null && httpMethod instanceof HttpEntityEnclosingRequestBase) { //Entity for Post methods
            ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(entity);
        }
        HttpResponse response = null;
        int statusCode = 0;

        try {
            //httpMethod.addHeader(ORIGIN_HEADER, "");
            httpMethod.addHeader(TEAM_PATH, this.teamPath);
            if (token != null) {
                httpMethod.addHeader(HttpHeaders.AUTHORIZATION, token.getToken_type() + " " + token.getAccess_token());
            }

            for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                httpMethod.addHeader(entry.getKey(), entry.getValue());
            }

            response = apacheClient.execute(httpMethod);
            statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_UNAUTHORIZED) { // Token has probably expired
                throw new CxTokenExpiredException(HttpClientHelper.extractResponseBody(response));
            }

            HttpClientHelper.validateResponse(response, expectStatus, "Failed to " + failedMsg);

            //extract response as object and return the link
            return HttpClientHelper.convertToObject(response, responseType, isCollection);
        } catch (UnknownHostException e) {
            throw new CxHTTPClientException("Connection failed. Please recheck the hostname and credentials you provided and try again.");
        } catch (CxTokenExpiredException ex) {
            if (retry) {
                logTokenError(httpMethod, statusCode, ex);
                if (lastLoginSettings != null) {
                    login(lastLoginSettings);
                    return request(httpMethod, contentType, entity, responseType, expectStatus, failedMsg, isCollection, false);
                }
            }
            throw ex;
        } finally {
            httpMethod.releaseConnection();
            HttpClientUtils.closeQuietly(response);
        }
    }

    public void close() {
        HttpClientUtils.closeQuietly(apacheClient);
    }

    private void setSSLTls(String protocol, Logger log) {
        try {
            final SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.warn(String.format("Failed to set SSL TLS : %s", e.getMessage()));
        }
    }

    //TODO handle missing scope issue with management_and_orchestration_api
    private StringEntity generateSSOEntity() {
        final String clientId = "cxsast_client";
        final String redirectUri = "%2Fcxwebclient%2FauthCallback.html%3F";
        final String responseType = "id_token%20token";
        final String nonce = "9313f0902ba64e50bc564f5137f35a52";
        final String isPrompt = "true";
        final String scopes = "sast_api openid sast-permissions access-control-permissions access_control_api management_and_orchestration_api".replace(" ", "%20");
        final String providerId = "2"; //windows provider id

        String redirectUrl = MessageFormat.format("/CxRestAPI/auth/identity/connect/authorize/callback" +
                        "?client_id={0}" +
                        "&redirect_uri={1}" + redirectUri +
                        "&response_type={2}" +
                        "&scope={3}" +
                        "&nonce={4}" +
                        "&prompt={5}"
                , clientId, rootUri, responseType, scopes, nonce, isPrompt);
        try {
            List<NameValuePair> urlParameters = new ArrayList<>();
            urlParameters.add(new BasicNameValuePair("redirectUrl", redirectUrl));
            urlParameters.add(new BasicNameValuePair("providerid", providerId));
            return new UrlEncodedFormEntity(urlParameters, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new ScannerRuntimeException(e.getMessage());
        }
    }

    public void setToken(TokenLoginResponse token) {
        this.token = token;
    }

    private void logTokenError(HttpRequestBase httpMethod, int statusCode, CxTokenExpiredException ex) {
        String message = String.format("Received status code %d for URL: %s with the message: %s",
                statusCode,
                httpMethod.getURI(),
                ex.getMessage());

        log.warn(message);

        log.info("Possible reason: access token has expired. Trying to request a new token...");
    }

}
