package com.cx.restclient.configuration;

import com.cx.restclient.ast.dto.sast.AstSastConfig;
import com.cx.restclient.ast.dto.sca.AstScaConfig;
import com.cx.restclient.dto.CxVersion;
import com.cx.restclient.dto.ProxyConfig;
import com.cx.restclient.dto.ScannerType;
import com.cx.restclient.dto.TokenLoginResponse;
//import com.cx.restclient.sast.dto.ReportType;
//import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.Cookie;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Created by galn on 21/12/2016.
 */
public class CxScanConfig implements Serializable {
//
    private String cxOrigin;
//    private CxVersion cxVersion;

    private boolean disableCertificateValidation = false;
    private boolean useSSOLogin = false;

    private String sourceDir;
    private String osaLocationPath;
    private File reportsDir;
//    // Map<reportType, reportPath> / (e.g. PDF to its file path)
//    private Map<ReportType, String> reports = new HashMap<>();
    private String username;
    private String password;
    private String refreshToken;
    private String url;
    private String projectName;
    private String teamPath;
    private String mvnPath;
    private String teamId;
    private Boolean denyProject = false;
    private Boolean hideResults = false;
    private Boolean isPublic = true;
    private Boolean forceScan = false;
    private String presetName;
    private Integer presetId;
    private String sastFolderExclusions;
    private String sastFilterPattern;
    private Integer sastScanTimeoutInMinutes;
    private Integer osaScanTimeoutInMinutes;
    private String scanComment;
    private Boolean isIncremental = false;
    private TokenLoginResponse token;
    private File zipFile;
    private String engineConfigurationName;

    private String osaFolderExclusions;

    private String osaFilterPattern;

    private Boolean osaGenerateJsonReport = true;

    private Boolean osaThresholdsEnabled = false;

    private Integer osaProgressInterval;
    private Integer connectionRetries;

    private Integer maxZipSize;

    private String scaJsonReport;

    private AstScaConfig astScaConfig;
    private AstSastConfig astSastConfig;

    private final Set<ScannerType> scannerTypes = new HashSet<>();
    private final List<Cookie> sessionCookies = new ArrayList<>();
    private Boolean isProxy = true;
    private ProxyConfig proxyConfig;
    private Boolean useNTLM=false;


    public CxScanConfig() {
    }

    public boolean isAstScaEnabled() {
        return scannerTypes.contains(ScannerType.AST_SCA);
    }

    public boolean isAstSastEnabled() {
        return scannerTypes.contains(ScannerType.AST_SAST);
    }

    public String getCxOrigin() {
        return cxOrigin;
    }
    

    public boolean isDisableCertificateValidation() {
        return disableCertificateValidation;
    }
    

    public boolean isUseSSOLogin() {
        return useSSOLogin;
    }
    

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getEffectiveSourceDirForDependencyScan() {
        return osaLocationPath != null ? osaLocationPath : sourceDir;
    }

    public File getReportsDir() {
        return reportsDir;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getScaJsonReport() {
        return scaJsonReport;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }
    
    public String getPresetName() {
        return presetName;
    }
    
    public Integer getOsaScanTimeoutInMinutes() {
        return osaScanTimeoutInMinutes == null ? -1 : osaScanTimeoutInMinutes;
    }
    
    public Boolean getIncremental() {
        return isIncremental;
    }

    public void setIncremental(Boolean incremental) {
        this.isIncremental = incremental;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public String getOsaFilterPattern() {
        return osaFilterPattern;
    }

    public String getOsaFolderExclusions() {
        return osaFolderExclusions;
    }

    public Boolean getOsaThresholdsEnabled() {
        return osaThresholdsEnabled;
    }

    public void setOsaThresholdsEnabled(Boolean osaThresholdsEnabled) {
        this.osaThresholdsEnabled = osaThresholdsEnabled;
    }

    public Boolean getOsaGenerateJsonReport() {
        return osaGenerateJsonReport;
    }
    

    public Integer getOsaProgressInterval() {
        return osaProgressInterval;
    }

    public void setOsaProgressInterval(Integer osaProgressInterval) {
        this.osaProgressInterval = osaProgressInterval;
    }

    public Integer getConnectionRetries() {
        return connectionRetries;
    }

    public Integer getMaxZipSize() {
        return maxZipSize;
    }

    public AstScaConfig getAstScaConfig() {
        return astScaConfig;
    }

    public void setAstScaConfig(AstScaConfig astScaConfig) {
        this.astScaConfig = astScaConfig;
    }

    public AstSastConfig getAstSastConfig() {
        return astSastConfig;
    }

    public void setAstSastConfig(AstSastConfig astConfig) {
        this.astSastConfig = astConfig;
    }

    public Set<ScannerType> getScannerTypes() {
        return scannerTypes;
    }

    public void addScannerType(ScannerType scannerType) {
        this.scannerTypes.add(scannerType);
    }
    

    public Boolean isProxy() {
        return isProxy;
    }
    
    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }

    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }

    public void addCookie(Cookie cookie) {
        this.sessionCookies.add(cookie);
    }
    

    public TokenLoginResponse getToken() {
        return token;
    }

    public void setToken(TokenLoginResponse token) {
        this.token = token;
    }

    public Boolean getNTLM() {
        return useNTLM;
    }

}
