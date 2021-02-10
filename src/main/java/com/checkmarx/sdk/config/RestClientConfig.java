package com.checkmarx.sdk.config;

import com.checkmarx.sdk.dto.sca.ScaConfig;
import com.checkmarx.sdk.dto.ScannerType;
import com.checkmarx.sdk.dto.TokenLoginResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class RestClientConfig  {

    private boolean disableCertificateValidation = false;
    private String sourceDir;
    private String username;
    private String password;
    private String url;
    private String projectName;
    private String teamId;
    private TokenLoginResponse token;
    private File zipFile;
    private boolean clonedRepo = false;

    private Integer progressInterval;
    private ScaConfig scaConfig;
    private AstConfig astConfig;

    private final Set<ScannerType> scannerTypes = new HashSet<>();
    private final List<Cookie> sessionCookies = new ArrayList<>();
    
}
