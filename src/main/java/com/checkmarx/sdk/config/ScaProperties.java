package com.checkmarx.sdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "sca")
@Validated
@Getter
@Setter
public class ScaProperties {

    private String appUrl;
    private String apiUrl;
    private String accessControlUrl;
    private String tenant;
    private String username;
    private String password;
}