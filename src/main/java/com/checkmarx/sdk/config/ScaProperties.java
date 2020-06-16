package com.checkmarx.sdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@ConfigurationProperties(prefix = ScaProperties.CONFIG_PREFIX)
@Validated
@Getter
@Setter
public class ScaProperties {
    public static final String CONFIG_PREFIX = "sca";

    private List<String> filterSeverity;
    private double filterScore;
    private String appUrl;
    private String apiUrl;
    private String accessControlUrl;
    private String tenant;
    private String username;
    private String password;
}