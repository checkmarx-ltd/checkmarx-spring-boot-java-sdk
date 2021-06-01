package com.checkmarx.sdk.config;

import com.checkmarx.sdk.utils.ScanUtils;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

import javax.annotation.PostConstruct;


@Component
@ConfigurationProperties(prefix = "checkmarx")
@Validated
public class CxProperties extends CxPropertiesBase{
    public static final String CONFIG_PREFIX = "sast";
    
    private String username;
    private String password;
    private String clientId = "resource_owner_client";
    private String soapClientId = "resource_owner_sast_client";
    private String soapClientSecret;
    private String scope = "sast_rest_api";
    private String soapScope = "sast_api";
    private String acUrl;

    private Boolean enableOsa = false;
    private Integer incrementalThreshold = 7;
    private Integer incrementalNumScans = 5;

    private Boolean preserveXml = false;
    
    private Integer httpConnectionTimeout = 30000;
    private Integer httpReadTimeout = 120000;

    private Integer reportPolling = 5000;
    private Integer reportTimeout = 300000;
    private Integer codeSnippetLength = 2500;
    private Integer postActionPostbackId = 0;

    private Boolean settingsOverride = false;

    private String portalPackage = "checkmarx.wsdl.portal";

    private String htmlStrip = "<style>.cxtaghighlight{color: rgb(101, 170, 235);font-weight:bold;}</style>";

    private Boolean enabledZipScan = false;

    private Map<String, String> customStateMap;

    /**
     * Maps finding state ID (as returned in CxSAST report) to state name (as specified in filter configuration).
     */
    private static final Map<String, String> CXSAST_STATE_ID_TO_NAME = ImmutableMap.of(
            "0", "TO VERIFY",
            "2", "CONFIRMED",
            "3", "URGENT",
            "4", "PROPOSED NOT EXPLOITABLE"
    );

    public void setEnabledZipScan(Boolean enabledZipScan){
        this.enabledZipScan = enabledZipScan;
    }
    
    public Boolean getEnabledZipScan() {
        return enabledZipScan;
    }
    
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
    

    public String getClientId() {
        return this.clientId;
    }


    public Integer getIncrementalThreshold() {
        return this.incrementalThreshold;
    }

    public Integer getIncrementalNumScans(){
        return this.incrementalNumScans;
    }


    public Boolean getPreserveXml() {
        return preserveXml;
    }

    public void setPreserveXml(Boolean preserveXml) {
        this.preserveXml = preserveXml;
    }

 
    public String getPortalPackage() {
        return this.portalPackage;
    }

    public String getHtmlStrip() {
        return this.htmlStrip;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setIncrementalThreshold(Integer incrementalThreshold){
        this.incrementalThreshold = incrementalThreshold;
    }
    
    public void setIncrementalNumScans(Integer incrementalNumScans){
        this.incrementalNumScans = incrementalNumScans;
    }
  
    public void setPortalPackage(String portalPackage) {
        this.portalPackage = portalPackage;
    }

    public void setHtmlStrip(String htmlStrip) {
        this.htmlStrip = htmlStrip;
    }

 
 
    public Integer getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    public void setHttpConnectionTimeout(Integer httpConnectionTimeout) {
        this.httpConnectionTimeout = httpConnectionTimeout;
    }

    public Integer getHttpReadTimeout() {
        return httpReadTimeout;
    }

    public void setHttpReadTimeout(Integer httpReadTimeout) {
        this.httpReadTimeout = httpReadTimeout;
    }

    public Integer getReportTimeout() {
        return reportTimeout;
    }

    public void setReportTimeout(Integer reportTimeout) {
        this.reportTimeout = reportTimeout;
    }
    
    public String getAcUrl() {
        return acUrl;
    }

    public void setAcUrl(String acUrl) {
        this.acUrl = acUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getReportPolling() {
        return reportPolling;
    }

    public void setReportPolling(Integer reportPolling) {
        this.reportPolling = reportPolling;
    }

    public Boolean getEnableOsa() {
        //return enableOsa;
        return false;
    }

    public Integer getCodeSnippetLength() {
        return codeSnippetLength;
    }

    public void setCodeSnippetLength(Integer codeSnippetLength) {
        this.codeSnippetLength = codeSnippetLength;
    }

    public void setEnableOsa(Boolean enableOsa) {
        this.enableOsa = enableOsa;
    }

    public String getSoapClientId() {
        return soapClientId;
    }

    public void setSoapClientId(String soapClientId) {
        this.soapClientId = soapClientId;
    }

    public String getSoapClientSecret() {
        if(ScanUtils.empty(soapClientSecret)){
            return clientSecret; //default to clientSecret that was provided
        }
        return soapClientSecret;
    }

    public void setSoapClientSecret(String soapClientSecret) {
        this.soapClientSecret = soapClientSecret;
    }

    public String getSoapScope() {
        return soapScope;
    }

    public void setSoapScope(String soapScope) {
        this.soapScope = soapScope;
    }
    
    public void setPostActionPostbackId(Integer postActionPostbackId) {
        this.postActionPostbackId = postActionPostbackId;
    }

    public Integer getPostActionPostbackId() {
        return this.postActionPostbackId;
    }

    public Boolean getSettingsOverride() {
        return settingsOverride;
    }

    public void setSettingsOverride(Boolean settingsOverride) {
        this.settingsOverride = settingsOverride;
    }

    public void setCustomStateMap(Map<String, String> customStateMap) {
	this.customStateMap = customStateMap;
    }

    public String getStateFullName(String key){
	String stateFullName = CXSAST_STATE_ID_TO_NAME.get(key);
	if (stateFullName == null && customStateMap != null) {
	    stateFullName = customStateMap.get(key);
	}
	return stateFullName;
    }
}

