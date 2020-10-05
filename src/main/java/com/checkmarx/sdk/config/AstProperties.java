package com.checkmarx.sdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = AstProperties.CONFIG_PREFIX)
@Validated
@Getter
@Setter
public class AstProperties {
    public static final String CONFIG_PREFIX = "ast";

    private String apiUrl;
    private String clientId;
    private String webAppUrl;
    private String clientSecret;
    private String preset;
    private String incremental;
}