package com.checkmarx.sdk.utils.scanner.client.httpClient;

import com.checkmarx.sdk.dto.LoginSettings;
import com.checkmarx.sdk.dto.ProxyConfig;
import com.checkmarx.sdk.dto.TokenLoginResponse;
import com.checkmarx.sdk.dto.sca.ClientType;
import com.checkmarx.sdk.exception.CxHTTPClientException;
import com.checkmarx.sdk.exception.CxTokenExpiredException;
import com.checkmarx.sdk.exception.ScannerRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.win.WindowsNTLMSchemeFactory;
import org.apache.http.impl.auth.win.WindowsNegotiateSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.checkmarx.sdk.config.ContentType.CONTENT_TYPE_APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;


/**
 * Created by Galn on 05/02/2018.
 */
public class CxHttpClient implements Closeable {

    public static final String CSRF_TOKEN_HEADER = "CXCSRFToken";
    public static final String REFRESH_TOKEN_PROP = "refresh_token";
    public static final String CLIENT_ID_PROP = "client_id";
    public static final String SSO_AUTHENTICATION = "auth/identity/externalLogin";
    public static final String REVOCATION = "auth/identity/connect/revocation";
    public static final String ORIGIN_HEADER = "cxOrigin";
    public static final String TEAM_PATH = "cxTeamPath";
    private static final String HTTPS = "https";
    private static final String DEFAULT_GRANT_TYPE = "password";
    private static final String AUTH_MESSAGE = "authenticate";
    private static final String CLIENT_SECRET_PROP = "client_secret";
    private static final String PASSWORD_PROP = "password";
    private final Map<String, String> customHeaders = new HashMap<>();
    private HttpClient apacheClient;
    private Logger log;
    private TokenLoginResponse token;
    private String rootUri;
    private LoginSettings lastLoginSettings;
    private String teamPath;
    private HttpClientBuilder cb = HttpClients.custom();


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

        if (!setCustomProxy(cb, log)) {
            cb.useSystemProperties();
        }

        cb.setConnectionReuseStrategy(new NoConnectionReuseStrategy());
        cb.setDefaultAuthSchemeRegistry(getAuthSchemeProviderRegistry());
        apacheClient = cb.build();
    }

    private static boolean setCustomProxy(HttpClientBuilder cb, Logger log) {

        ProxyConfig proxyConfig = generateProxyConfig(log);

        if (proxyConfig == null ||
                StringUtils.isEmpty(proxyConfig.getHost()) ||
                proxyConfig.getPort() == 0) {
            return false;
        }

        String scheme = proxyConfig.isUseHttps() ? HTTPS : "http";
        HttpHost proxy = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort(), scheme);
        if (StringUtils.isNotEmpty(proxyConfig.getUsername()) &&
                StringUtils.isNotEmpty(proxyConfig.getPassword())) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxyConfig.getUsername(), proxyConfig.getPassword());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxy), credentials);
            cb.setDefaultCredentialsProvider(credsProvider);
        }

        log.info("Setting proxy for Checkmarx http client");
        cb.setProxy(proxy);
        cb.setRoutePlanner(getRoutePlanner(proxyConfig, proxy, log));
        cb.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        return true;
    }

    private static DefaultProxyRoutePlanner getRoutePlanner(ProxyConfig proxyConfig, HttpHost proxyHost, Logger log) {
        return new DefaultProxyRoutePlanner(proxyHost) {
            public HttpRoute determineRoute(
                    final HttpHost host,
                    final HttpRequest request,
                    final HttpContext context) throws HttpException {
                String hostname = host.getHostName();
                String noHost = proxyConfig.getNoProxyHosts();
                if (StringUtils.isNotEmpty(noHost)) {
                    String[] hosts = noHost.split("\\|");
                    for (String nonHost : hosts) {
                        try {
                            if (matchNonProxyHostWildCard(hostname, noHost)) {
                                log.debug("Bypassing proxy as host {} is found in the nonProxyHosts", hostname);
                                return new HttpRoute(host);
                            }
                        } catch (PatternSyntaxException e) {
                            log.warn("Wrong nonProxyHost param: {} ", nonHost);
                        }
                    }
                }
                return super.determineRoute(host, request, context);
            }
        };
    }

    /*
     * '*' is the only wildcard support in nonProxyHosts JVM argument.
     *  * in Java regex has different meaning than required here.
     *  Hence the custom logic
     */
    private static boolean matchNonProxyHostWildCard(String sourceHost, String nonProxyHost) {
        if (nonProxyHost.indexOf("*") > -1)
            nonProxyHost = nonProxyHost.replaceAll("\\.", "\\\\.");

        nonProxyHost = nonProxyHost.replaceAll("\\*", "\\.\\*");

        Pattern p = Pattern.compile(nonProxyHost);//. represents single character
        Matcher m = p.matcher(sourceHost);
        return m.matches();
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

    private static ProxyConfig generateProxyConfig(Logger log) {
        final String HTTP_HOST = System.getProperty("http.proxyHost");
        final String HTTP_PORT = System.getProperty("http.proxyPort");
        final String HTTP_USERNAME = System.getProperty("http.proxyUser");
        final String HTTP_PASSWORD = System.getProperty("http.proxyPassword");

        final String HTTPS_HOST = System.getProperty("https.proxyHost");
        final String HTTPS_PORT = System.getProperty("https.proxyPort");
        final String HTTPS_USERNAME = System.getProperty("https.proxyUser");
        final String HTTPS_PASSWORD = System.getProperty("https.proxyPassword");

        ProxyConfig proxyConfig = null;
        try {
            if (isNotEmpty(HTTP_HOST) && isNotEmpty(HTTP_PORT)) {
                proxyConfig = new ProxyConfig();
                proxyConfig.setUseHttps(false);
                proxyConfig.setHost(HTTP_HOST);
                proxyConfig.setPort(Integer.parseInt(HTTP_PORT));
                if (isNotEmpty(HTTP_USERNAME) && isNotEmpty(HTTP_PASSWORD)) {
                    proxyConfig.setUsername(HTTP_USERNAME);
                    proxyConfig.setPassword(HTTP_PASSWORD);
                }
            } else if (isNotEmpty(HTTPS_HOST) && isNotEmpty(HTTPS_PORT)) {
                proxyConfig = new ProxyConfig();
                proxyConfig.setUseHttps(true);
                proxyConfig.setHost(HTTPS_HOST);
                proxyConfig.setPort(Integer.parseInt(HTTPS_PORT));
                if (isNotEmpty(HTTPS_USERNAME) && isNotEmpty(HTTPS_PASSWORD)) {
                    proxyConfig.setUsername(HTTPS_USERNAME);
                    proxyConfig.setPassword(HTTPS_PASSWORD);
                }
            }
        } catch (Exception e) {
            log.error("Failed to set custom proxy.", e);
        }

        return proxyConfig;
    }

    public String getRootUri() {
        return rootUri;
    }

    public void setRootUri(String rootUri) {
        this.rootUri = rootUri;
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
        
        log.info(CSRF_TOKEN_HEADER + ": {}", csrfToken);
        log.info("cookie: CXCSRFToken={}; cxCookie={}", csrfToken, cxCookie);

        apacheClient = cb.setDefaultHeaders(headers).build();
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
        log.debug("Adding a custom header: {} : {}", name, value);
        customHeaders.put(name, value);
    }


    private <T> T request(HttpRequestBase httpMethod, String contentType, HttpEntity entity, Class<T> responseType, int expectStatus, String failedMsg, boolean isCollection, boolean retry) throws IOException {
        if (contentType != null) {
            httpMethod.setHeader("Content-type", contentType);
        }
        if (entity != null && httpMethod instanceof HttpEntityEnclosingRequestBase) { //Entity for Post methods
            ((HttpEntityEnclosingRequestBase) httpMethod).setEntity(entity);
        }
        HttpResponse response = null;
        int statusCode = 0;

        try {
            httpMethod.setHeader(TEAM_PATH, this.teamPath);
            if (token != null) {
                httpMethod.setHeader(HttpHeaders.AUTHORIZATION, token.getToken_type() + " " + token.getAccess_token());
            }

            for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
                httpMethod.setHeader(entry.getKey(), entry.getValue());
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
            log.debug(ExceptionUtils.getStackTrace(e));
            throw new CxHTTPClientException("Connection failed. Please recheck the hostname and credentials you provided and try again.");
        } catch (CxTokenExpiredException ex) {
            log.debug(ExceptionUtils.getStackTrace(ex));
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
