package com.checkmarx.sdk.config;

import com.checkmarx.sdk.dto.ProxyConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
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
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.net.ssl.*;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Configuration
public class SpringConfiguration {

    private final CxProperties properties;

    public SpringConfiguration(CxProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "cxRestTemplate")
    public RestTemplate getRestTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate restTemplate = new RestTemplate();

        HttpClientBuilder cb = HttpClients.custom();
        setSSLTls("TLSv1.2");
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslConnectionSocketFactory = null;
        Registry<ConnectionSocketFactory> registry;
        PoolingHttpClientConnectionManager cm = null;

        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
            registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new PlainConnectionSocketFactory())
                    .register("https", sslConnectionSocketFactory)
                    .build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(100);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {

        }
        cb.setSSLSocketFactory(sslConnectionSocketFactory);
        cb.setConnectionManager(cm);
        cb.setConnectionManagerShared(true);
        cb.setConnectionReuseStrategy(new NoConnectionReuseStrategy());
        cb.setDefaultAuthSchemeRegistry(getAuthSchemeProviderRegistry());

        if (!setCustomProxy(cb)) {
            cb.useSystemProperties();
        }

        HttpClient httpClient = cb.build();
        ////////////



        //////
        HttpComponentsClientHttpRequestFactory requestFactory = new
                HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(properties.getHttpConnectionTimeout());
        requestFactory.setReadTimeout(properties.getHttpReadTimeout());
        restTemplate.setRequestFactory(requestFactory);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    private static boolean setCustomProxy(HttpClientBuilder cb) {

        ProxyConfig proxyConfig = generateProxyConfig();

        if (proxyConfig == null ||
                StringUtils.isEmpty(proxyConfig.getHost()) ||
                proxyConfig.getPort() == 0) {
            return false;
        }

        String scheme = proxyConfig.isUseHttps() ? "https" : "http";
        HttpHost proxy = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort(), scheme);
        if (StringUtils.isNotEmpty(proxyConfig.getUsername()) &&
                StringUtils.isNotEmpty(proxyConfig.getPassword())) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(proxyConfig.getUsername(), proxyConfig.getPassword());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxy), credentials);
            cb.setDefaultCredentialsProvider(credsProvider);
        }

        cb.setProxy(proxy);
        cb.setRoutePlanner(getRoutePlanner(proxyConfig, proxy));
        cb.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        return true;
    }

    private static DefaultProxyRoutePlanner getRoutePlanner(ProxyConfig proxyConfig, HttpHost proxyHost) {
        return new DefaultProxyRoutePlanner(proxyHost) {
            public HttpRoute determineRoute(
                    final HttpHost host,
                    final HttpRequest request,
                    final HttpContext context) throws HttpException {
                String hostname = host.getHostName();
                String noHost = proxyConfig.getNoProxyHosts(); // StringUtils.isNotEmpty(HTTP_NO_HOST) ? HTTP_NO_HOST : HTTPS_NO_HOST;
                if (StringUtils.isNotEmpty(noHost)) {
                    String[] hosts = noHost.split("\\|");
                    for (String nonHost : hosts) {
                        try {
                            if (matchNonProxyHostWildCard(hostname, noHost)) {

                                return new HttpRoute(host);
                            }
                        } catch (PatternSyntaxException e) {

                        }
                    }
                }
                return super.determineRoute(host, request, context);
            }
        };
    }

    private static boolean matchNonProxyHostWildCard(String sourceHost, String nonProxyHost) {
        if(nonProxyHost.indexOf("*")> -1)
            nonProxyHost = nonProxyHost.replaceAll("\\.", "\\\\.");

        nonProxyHost = nonProxyHost.replaceAll("\\*", "\\.\\*");

        Pattern p = Pattern.compile(nonProxyHost);//. represents single character
        Matcher m = p.matcher(sourceHost);
        return m.matches();
    }

    private static ProxyConfig generateProxyConfig() {
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

        }

        return proxyConfig;
    }

    private static Registry<AuthSchemeProvider> getAuthSchemeProviderRegistry() {
        return RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .register(AuthSchemes.NTLM, new WindowsNTLMSchemeFactory(null))
                .register(AuthSchemes.SPNEGO, new WindowsNegotiateSchemeFactory(null))
                .build();
    }

    private void setSSLTls(String protocol) {
        try {
            final SSLContext sslContext = SSLContext.getInstance(protocol);
            sslContext.init(null, null, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {

        }
    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(
            SSLContext sslcontext) {

        SSLConnectionSocketFactory sslsocketFactory = new SSLConnectionSocketFactory(sslcontext, NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("https", sslsocketFactory)
                        .register("http", new PlainConnectionSocketFactory())
                        .build();

        PoolingHttpClientConnectionManager poolingManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingManager.setMaxTotal(110);
        return poolingManager;
    }

    private TrustManager[] insecureTrustManager() {
        X509TrustManager trustManager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        TrustManager[] tmList = {trustManager};
        return tmList;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        if (properties != null && properties.getPortalPackage() != null && !properties.getPortalPackage().isEmpty()) {
            marshaller.setContextPaths(properties.getPortalPackage());
        }
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate ws = new WebServiceTemplate();
        if (properties != null && properties.getPortalUrl() != null && !properties.getPortalUrl().isEmpty()) {
            ws.setDefaultUri(properties.getPortalUrl());
        }
        if (marshaller != null) {
            ws.setMarshaller(marshaller);
            ws.setUnmarshaller(marshaller);
        }
        return ws;
    }
}