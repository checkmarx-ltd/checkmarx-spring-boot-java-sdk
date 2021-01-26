package com.checkmarx.sdk.config;

import com.checkmarx.sdk.dto.ast.AstConfig;
import com.checkmarx.sdk.dto.sca.AstScaConfig;
import com.checkmarx.sdk.dto.ScannerType;
import com.checkmarx.sdk.dto.TokenLoginResponse;
//import com.checkmarx.sdk.scanner.restclient.sast.dto.ReportType;
//import org.apache.commons.lang3.StringUtils;
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
    
    private Integer progressInterval;
    private AstScaConfig astScaConfig;
    private AstConfig astConfig;

    private final Set<ScannerType> scannerTypes = new HashSet<>();
    private final List<Cookie> sessionCookies = new ArrayList<>();
    
}
