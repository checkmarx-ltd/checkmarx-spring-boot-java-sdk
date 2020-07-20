package com.checkmarx.sdk.config;

import com.cx.restclient.dto.scansummary.Severity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = AstProperties.CONFIG_PREFIX)
@Validated
@Getter
@Setter
public class AstProperties {
    public static final String CONFIG_PREFIX = "ast";

    private String apiUrl;
    private String token;
    private String preset;
    private String incremental;

}