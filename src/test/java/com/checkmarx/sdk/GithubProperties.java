package com.checkmarx.sdk;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = GithubProperties.CONFIG_PREFIX)
@Validated
@Getter
@Setter
public class GithubProperties {
    public static final String CONFIG_PREFIX = "github";

    private String token;
    private String url;
}