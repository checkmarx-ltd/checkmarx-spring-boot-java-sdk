package com.cx.restclient.configuration;

import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.dto.CxVersion;
import com.cx.restclient.dto.ProxyConfig;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.TokenLoginResponse;
//import com.cx.restclient.sast.dto.ReportType;
//import org.apache.commons.lang3.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class RestClientConfig implements Serializable {

    private boolean disableCertificateValidation = false;
    private String sourceDir;
    private String username;
    private String password;
    private String url;
    private String projectName;
    private String teamId;
    private Boolean isIncremental = false;
    private TokenLoginResponse token;
    private File zipFile;
    
    private Integer osaProgressInterval;
    private AstScaConfig astScaConfig;
    private AstSastConfig astSastConfig;

    private final Set<ScannerType> scannerTypes = new HashSet<>();
    private final List<Cookie> sessionCookies = new ArrayList<>();
    
}
