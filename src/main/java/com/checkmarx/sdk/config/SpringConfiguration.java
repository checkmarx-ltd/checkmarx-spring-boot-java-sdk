package com.checkmarx.sdk.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class SpringConfiguration {

    private final CxProperties properties;

    public SpringConfiguration(CxProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "cxRestTemplate")
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(properties.getHttpConnectionTimeout()))
                .setReadTimeout(Duration.ofMillis(properties.getHttpReadTimeout()))
                .build();

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
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