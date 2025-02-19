package com.checkmarx.sdk.config;

import com.checkmarx.sdk.utils.ScanUtils;
import com.google.common.collect.ImmutableMap;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;


@Component
@ConfigurationProperties(prefix = "checkmarx")
@Validated
public class CxProperties extends CxPropertiesBase{
    public static final String CONFIG_PREFIX = "sast";
    
    private String username;
    private String password;
    private String sshKey;
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

    @Getter
    @Setter
    private Boolean cancelInpregressScan = false;

    @Getter
    @Setter

    private Boolean enableTokenLogin = false;

    @Getter
    @Setter
    private String token ;


    @Getter
    @Setter
    @Builder.Default
    private Boolean isDefaultBranchEmpty = false;


    @Getter
    @Setter
    @Builder.Default
    private Boolean isBranchedIncremental = false;

    @Getter
    @Setter
    private int corepoolsize;
    @Getter
    @Setter
    private int maxpoolsize;
    @Getter
    @Setter
    private int queuecapacityarg;

    @Getter
    @Setter
    private boolean trustcerts = false;

    @Getter
    @Setter
    private String truststorepath;
    @Getter
    @Setter
    private String truststorepassword;
    
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

    @Getter @Setter
    private Map<String, String> customStateFalsePositiveMap;



    @Getter @Setter
    private Map<String, String> modifyBranchNameByPatternMap;

    private Map<String, String> sshKeyList;

    private Boolean cxBranch = false;

    @Getter @Setter
    private Boolean customkeystore = false;

    /*
     * If set to true, group results by vulnerability, filename and
     * severity (by default, results are grouped only by vulnerability
     * and filename).
     */
    private Boolean groupBySeverity = false;

    /*
     * If set to true, results are not grouped at all
     * (by default, results are grouped only by vulnerability
     * and filename. Setting default to false).
     */
    private Boolean disableClubbing = false;

    @Getter
    @Setter
    private Boolean infiniteTimeOutCheckflag = true;// Adding this if any issue occurs user can turn off this check and it will not affect original

    @Getter
    @Setter
    private Boolean deleteRunningScans = false;

    @Getter @Setter
    private Boolean overrideProjectSetting = true;

    @Getter @Setter
    private Boolean considerScanningStatus = false;

    @Getter @Setter
    private Boolean projectSummary= false;


    /**
     * Maps finding state ID (as returned in CxSAST report) to state name (as specified in filter configuration).
     */
    private static final Map<String, String> CXSAST_STATE_ID_TO_NAME = ImmutableMap.of(
            "0", "TO VERIFY",
            "2", "CONFIRMED",
            "3", "URGENT",
            "4", "PROPOSED NOT EXPLOITABLE"
    );

    private static final Map<String, String> CXSAST_SEVERITY_ID_TO_NAME = ImmutableMap.of(
            "0", "Info",
            "1","Low",
            "2", "Medium",
            "3", "High",
            "4", "Critical"
    );

    public void setEnabledZipScan(Boolean enabledZipScan){
        this.enabledZipScan = enabledZipScan;
    }
    
    public Boolean getEnabledZipScan() {
        return enabledZipScan;
    }
    
    public String getSshKey() {
        return this.sshKey;
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

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
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

    public String getSeverityFullName(String key){
        return CXSAST_SEVERITY_ID_TO_NAME.get(key);
    }

    public String checkCustomFalsePositive(String key){
        try {
            return customStateFalsePositiveMap.get(key);
        }catch (Exception e){
            return null;
        }
    }

    public Boolean getGroupBySeverity() {
	      return groupBySeverity;
   }

    public void setGroupBySeverity(Boolean groupBySeverity) {
	this.groupBySeverity = groupBySeverity;
    }

    public Map<String, String> getSshKeyList() {
        return sshKeyList;
    }

    public void setSshKeyList(Map<String, String> sshKeyList) {
        this.sshKeyList = sshKeyList;
    }

    public Boolean getCxBranch() {
        return cxBranch;
    }

    public void setCxBranch(Boolean cxBranch) {
        this.cxBranch = cxBranch;
    }

    public Boolean getDisableClubbing() {
        return disableClubbing;
    }

    public void setDisableClubbing(Boolean disableClubbing) {
        this.disableClubbing = disableClubbing;
    }
}

