package com.checkmarx.sdk.config;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
@Configuration
public class SpringConfiguration {

    private final CxProperties properties;

    public SpringConfiguration(CxProperties properties) {
        this.properties = properties;
    }


    public static SSLContext createCustomSSLContext(String trustStorePath, String trustStorePassword) throws Exception {
        TrustManagerFactory defaultTmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        defaultTmFactory.init((KeyStore) null);

        // Load the custom trust store
        KeyStore customTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream fis = new FileInputStream(trustStorePath)) {
            customTrustStore.load(fis, trustStorePassword.toCharArray());
        }
        TrustManagerFactory customTmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        customTmFactory.init(customTrustStore);

        // Combine both TrustManagers
        TrustManager[] defaultTrustManagers = defaultTmFactory.getTrustManagers();
        TrustManager[] customTrustManagers = customTmFactory.getTrustManagers();
        TrustManager[] combinedTrustManagers = new TrustManager[defaultTrustManagers.length + customTrustManagers.length];
        System.arraycopy(customTrustManagers, 0, combinedTrustManagers, 0, customTrustManagers.length);
        System.arraycopy(defaultTrustManagers, 0, combinedTrustManagers, customTrustManagers.length, defaultTrustManagers.length);

        // Initialize SSLContext with combined TrustManagers
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, combinedTrustManagers, new java.security.SecureRandom());
        return sslContext;
    }

    @Bean(name = "cxRestTemplate")
    public RestTemplate restTemplateByPassSSL(RestTemplateBuilder builder) throws Exception {

        if (properties.getCustomkeystore()) {
            SSLContext sslContext = createCustomSSLContext(properties.getTruststorepath(), properties.getTruststorepassword());

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);


            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(socketFactory)
                    .build();

            org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = org.apache.hc.client5.http.impl.classic.HttpClients.custom()

                    .setConnectionManager(connectionManager)
                    .evictExpiredConnections()
                    .build();
            HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory();
            customRequestFactory.setHttpClient(httpClient);
            return builder.requestFactory(() -> customRequestFactory).build();
        } else if (properties.isTrustcerts()) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);

            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(socketFactory)
                    .build();

            org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient = org.apache.hc.client5.http.impl.classic.HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .evictExpiredConnections()
                    .build();
            HttpComponentsClientHttpRequestFactory customRequestFactory = new HttpComponentsClientHttpRequestFactory();
            customRequestFactory.setHttpClient(httpClient);
            return builder.requestFactory(() -> customRequestFactory).build();
        } else {
            RestTemplate restTemplate = new RestTemplateBuilder()
                    .setConnectTimeout(Duration.ofMillis(properties.getHttpConnectionTimeout()))
                    .setReadTimeout(Duration.ofMillis(properties.getHttpReadTimeout()))
                    .build();

            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            return restTemplate;

        }
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

    /*@Bean
    public CxHttpClient getCxHttpClient() {
        try {
            return new CxHttpClient(
                    properties.getBaseUrl(),
                    properties.getUsername(),
                    properties.getPassword(),
                    "CxFlow",
                    true,
                    org.slf4j.LoggerFactory.getLogger(CxHttpClient.class)
            );
        }
        catch (MalformedURLException e){
            return null;
        }
    }*/
}