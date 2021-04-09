package com.checkmarx.sdk.dto.sast;

import java.util.List;
import java.util.Map;
import checkmarx.wsdl.portal.CxUserTypes;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CxUser {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("lastLoginDate")
    private String lastLoginDate;
    @JsonProperty("roleIds")
    private List<Integer> roleIds = null;
    @JsonProperty("teamIds")
    private List<Integer> teamIds = null;
    @JsonProperty("authenticationProviderId")
    private Integer authenticationProviderId;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("cellPhoneNumber")
    private String cellPhoneNumber;
    @JsonProperty("jobTitle")
    private String jobTitle;
    @JsonProperty("other")
    private String other;
    @JsonProperty("country")
    private String country;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("expirationDate")
    private Object expirationDate;
    @JsonProperty("allowedIpList")
    private List<String> allowedIpList = null;
    @JsonProperty("localeId")
    private Integer localeId;
    private Map<String, String> teams8x; //only used for 8.x SOAP WS
    private CxUserTypes type8x; //only used for 8.x SOAP WS
    private String company8x; //only used for 8.x SOAP WS
    private String companyId8x; //only used for 8.x SOAP WS
    private String upn; //only used for 8.x SOAP WS
    private Role8x role8x; //only used for 8.x SOAP WS
    private Integer expirationDays = 1825; //only used for 8.x SOAP WS
    private Integer languageLCID = 1033; //english - only used for 8.x SOAP WS
    private boolean auditor = false; //only used for 8.x SOAP WS

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    public CxUser withId(Long id) {
        this.id = id;
        return this;
    }

    @JsonProperty("userName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public CxUser withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @JsonProperty("lastLoginDate")
    public String getLastLoginDate() {
        return lastLoginDate;
    }

    @JsonProperty("lastLoginDate")
    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public CxUser withLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
        return this;
    }

    @JsonProperty("roleIds")
    public List<Integer> getRoleIds() {
        return roleIds;
    }

    @JsonProperty("roleIds")
    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public CxUser withRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
        return this;
    }

    @JsonProperty("teamIds")
    public List<Integer> getTeamIds() {
        return teamIds;
    }

    @JsonProperty("teamIds")
    public void setTeamIds(List<Integer> teamIds) {
        this.teamIds = teamIds;
    }

    public CxUser withTeamIds(List<Integer> teamIds) {
        this.teamIds = teamIds;
        return this;
    }

    @JsonProperty("authenticationProviderId")
    public Integer getAuthenticationProviderId() {
        return authenticationProviderId;
    }

    @JsonProperty("authenticationProviderId")
    public void setAuthenticationProviderId(Integer authenticationProviderId) {
        this.authenticationProviderId = authenticationProviderId;
    }

    public CxUser withAuthenticationProviderId(Integer authenticationProviderId) {
        this.authenticationProviderId = authenticationProviderId;
        return this;
    }

    @JsonProperty("firstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public CxUser withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    @JsonProperty("lastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public CxUser withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    public CxUser withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public CxUser withPassword(String password) {
        this.password = password;
        return this;
    }

    @JsonProperty("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public CxUser withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    @JsonProperty("cellPhoneNumber")
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    @JsonProperty("cellPhoneNumber")
    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public CxUser withCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
        return this;
    }

    @JsonProperty("jobTitle")
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonProperty("jobTitle")
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public CxUser withJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
        return this;
    }

    @JsonProperty("other")
    public String getOther() {
        return other;
    }

    @JsonProperty("other")
    public void setOther(String other) {
        this.other = other;
    }

    public CxUser withOther(String other) {
        this.other = other;
        return this;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    public CxUser withCountry(String country) {
        this.country = country;
        return this;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    public CxUser withActive(Boolean active) {
        this.active = active;
        return this;
    }

    @JsonProperty("expirationDate")
    public Object getExpirationDate() {
        return expirationDate;
    }

    @JsonProperty("expirationDate")
    public void setExpirationDate(Object expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CxUser withExpirationDate(Object expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    @JsonProperty("allowedIpList")
    public List<String> getAllowedIpList() {
        return allowedIpList;
    }

    @JsonProperty("allowedIpList")
    public void setAllowedIpList(List<String> allowedIpList) {
        this.allowedIpList = allowedIpList;
    }

    public CxUser withAllowedIpList(List<String> allowedIpList) {
        this.allowedIpList = allowedIpList;
        return this;
    }

    @JsonProperty("localeId")
    public Integer getLocaleId() {
        return localeId;
    }

    @JsonProperty("localeId")
    public void setLocaleId(Integer localeId) {
        this.localeId = localeId;
    }

    public CxUser withLocaleId(Integer localeId) {
        this.localeId = localeId;
        return this;
    }

    public Map<String, String> getTeams8x() {
        return teams8x;
    }

    public void setTeams8x(Map<String, String> teams8x) {
        this.teams8x = teams8x;
    }

    public CxUser withTeams8x(Map<String, String> teams8x) {
        this.teams8x = teams8x;
        return this;
    }

    public CxUserTypes getType8x() {
        return type8x;
    }

    public void setType8x(CxUserTypes type8x) {
        this.type8x = type8x;
    }

    public CxUser withType8x(CxUserTypes type8x) {
        this.type8x = type8x;
        return this;
    }

    public String getCompany8x() {
        return company8x;
    }

    public void setCompany8x(String company8x) {
        this.company8x = company8x;
    }

    public CxUser withCompany8x(String company8x) {
        this.company8x = company8x;
        return this;
    }

    public String getCompanyId8x() {
        return companyId8x;
    }

    public void setCompanyId8x(String companyId8x) {
        this.companyId8x = companyId8x;
    }

    public CxUser withCompanyId8x(String companyId8x) {
        this.companyId8x = companyId8x;
        return this;
    }

    public Role8x getRole8x() {
        return role8x;
    }

    public void setRole8x(Role8x role8x) {
        this.role8x = role8x;
    }

    public CxUser withRole8x(Role8x role8x) {
        this.role8x = role8x;
        return this;
    }

    public Integer getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(Integer expirationDays) {
        this.expirationDays = expirationDays;
    }

    public boolean isAuditor() {
        return auditor;
    }

    public void setAuditor(boolean auditor) {
        this.auditor = auditor;
    }

    public Integer getLanguageLCID() {
        return languageLCID;
    }

    public void setLanguageLCID(Integer languageLCID) {
        this.languageLCID = languageLCID;
    }

    public String getUpn() {
        return upn;
    }

    public void setUpn(String upn) {
        this.upn = upn;
    }

    public CxUser withUpn(String upn) {
        this.upn = upn;
        return this;
    }

    public enum Role8x {

        SCANNER("Scanner", 0),
        REVIEWER("Reviewer", 1),
        COMPANYMANAGER("CompanyManager", 2),
        SPMANAGER("SPManager", 4),
        SERVERMANAGER("ServerManager", 5);


        private final String key;
        private final Integer value;

        Role8x(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }
        public Integer getValue() {
            return value;
        }
    }
}