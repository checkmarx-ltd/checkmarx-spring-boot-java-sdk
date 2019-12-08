package com.checkmarx.sdk.service;

import checkmarx.wsdl.portal.*;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.CxUser;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.CheckmarxLegacyException;
import com.checkmarx.sdk.utils.ScanUtils;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import javax.xml.datatype.XMLGregorianCalendar;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checkmarx SOAP WebService Client
 */
@Component
public class CxLegacyService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxLegacyService.class);
    private final CxProperties properties;
    private final WebServiceTemplate ws;

    private static final String CX_WS_PREFIX= "http://Checkmarx.com/";
    private static final String CX_WS_LOGIN_URI = CX_WS_PREFIX + "LoginV2";
    private static final String CX_WS_DESCRIPTION_URI = CX_WS_PREFIX + "GetResultDescription";
    private static final String CX_WS_LDAP_CONFIGURATIONS_URI = CX_WS_PREFIX + "GetLdapServersConfigurations";
    private static final String CX_WS_TEAM_LDAP_MAPPINGS_URI = CX_WS_PREFIX + "GetTeamLdapGroupsMapping";
    private static final String CX_WS_ADD_USER = CX_WS_PREFIX + "AddNewUser";
    private static final String CX_WS_ALL_USERS = CX_WS_PREFIX + "GetAllUsers";
    private static final String CX_WS_UPDATE_TEAM_URI = CX_WS_PREFIX + "UpdateTeam";
    private static final String CX_WS_TEAM_URI = CX_WS_PREFIX + "CreateNewTeam";
    private static final String CX_WS_DELETE_TEAM_URI = CX_WS_PREFIX + "DeleteTeam";

    @ConstructorProperties({"properties", "ws"})
    public CxLegacyService(CxProperties properties, WebServiceTemplate ws) {
        this.properties = properties;
        this.ws = ws;
    }

    /**
     * Login to Cx using legacy SOAP WS
     * @param username
     * @param password
     * @return
     * @throws CheckmarxLegacyException
     */
    public String login(String username, String password) throws CheckmarxLegacyException {
        LoginV2 request = new LoginV2();
        request.setApplicationCredentials(new Credentials(username, password));
        LoginV2Response response = (LoginV2Response) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_LOGIN_URI));
        try {
            if(!response.getLoginV2Result().isIsSuccesfull())
                throw new CheckmarxLegacyException("Authentication Error");
            return response.getLoginV2Result().getSessionId();
        }
        catch(NullPointerException e){
            log.error("Authentication Error while logging into CX using SOAP WS");
            throw new CheckmarxLegacyException("Authentication Error");
        }
    }


    public List<CxUser> getUsers(String session) throws CheckmarxLegacyException {
        GetAllUsers request = new GetAllUsers();
        request.setSessionID(session);
        GetAllUsersResponse response = (GetAllUsersResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_ALL_USERS));
        List<UserData> userData = response.getGetAllUsersResult().getUserDataList().getUserData();
        List<CxUser> users = new ArrayList<>();
        for(UserData u: userData){
            users.add(mapUser(u));
        }

        return null;
    }

    private CxUser mapUser(UserData u){
        CxUser user = new CxUser();
        user.setId(u.getID());
        user.setActive(u.isIsActive());
        user.setLastLoginDate(u.getLastLoginDate().toString());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        user.setEmail(u.getEmail());
        user.setPhoneNumber(u.getPhone());
        user.setCellPhoneNumber(u.getCellPhone());
        user.setJobTitle(u.getJobTitle());
        user.setCountry(u.getCountry());
        //TODO expiration date?
        user.setAllowedIpList(u.getAllowedIPs().getString());
        //user.setType8x();
        user.setCompanyId8x(u.getCompanyID());
        user.setCompany8x(u.getCompanyName());
        user.setUpn(u.getUPN());
        Map<String, String> teams = new HashMap<>();
        for(Group g : u.getGroupList().getGroup()){
            teams.put(g.getID(), g.getGroupName());
        }
        user.setTeams8x(teams);
        user.setRole8x(CxUser.Role8x.valueOf(u.getRoleData().getName())); //validate
        user.setAuditor(u.isAuditUser());
        return user;
    }

    public void addUser(String session, CxUser user) throws CheckmarxLegacyException{

        if(ScanUtils.empty(user.getCompany8x()) || ScanUtils.empty(user.getCompanyId8x()) ||
            user.getTeams8x() == null || user.getTeams8x().isEmpty()  ||
            user.getType8x() == null || user.getRole8x() == null
        ){
            throw new CheckmarxLegacyException("Missing team, type, or company details from user details");
        }

        AddNewUser request = new AddNewUser();
        request.setSessionID(session);
        UserData userData = new UserData();

        userData.setIsActive(user.getActive());
        userData.setAuditUser(user.isAuditor());
        userData.setUserPreferedLanguageLCID(user.getLanguageLCID());
        userData.setEmail(user.getEmail());
        userData.setUserName(user.getUserName());
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        if (!ScanUtils.empty(user.getPassword())) {
            userData.setPassword(user.getPassword());
        }
        userData.setWillExpireAfterDays(user.getExpirationDays().toString());
        if(!ScanUtils.empty(user.getUpn())){
            userData.setUPN(user.getUpn());
        }

/*        XMLGregorianCalendar time = new XMLGregorianCalendarImpl();
        time.setYear(0001);
        time.setMonth(01);
        time.setDay(01);
        time.setHour(00);
        time.setMinute(00);
        time.setSecond(00);

        userData.setDateCreated(time);
        userData.setLastLoginDate(time);
  */
        CxWSRoleWithUserPrivileges role = new CxWSRoleWithUserPrivileges();
        role.setName(user.getRole8x().getKey());
        role.setID(user.getRole8x().getValue().toString());
        userData.setRoleData(role);

        ArrayOfGroup arrayOfGroup = new ArrayOfGroup();
        List<Group> groups = arrayOfGroup.getGroup();
        for (Map.Entry<String, String> entry : user.getTeams8x().entrySet()){
            Group group = new Group();
            group.setGroupName(entry.getValue());
            group.setID(entry.getKey());
            group.setGuid(entry.getKey());
            group.setType(GroupType.TEAM);
            groups.add(group);
        }

        userData.setGroupList(arrayOfGroup);
        userData.setCompanyID(user.getCompanyId8x());
        userData.setCompanyName(user.getCompany8x());
        request.setUserData(userData);
        request.setUserType(user.getType8x());

        AddNewUserResponse response = (AddNewUserResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_ADD_USER));
        if(!response.getAddNewUserResult().isIsSuccesfull()){
            log.error(response.getAddNewUserResult().getErrorMessage());
        }

    }

    void createTeam(String sessionId, String parentId, String teamName) throws CheckmarxException {
        CreateNewTeam request = new CreateNewTeam(sessionId);
        request.setNewTeamName(teamName);
        request.setParentTeamID(parentId);
        log.info("Creating team {} ({})", teamName, parentId);

        try {
            CreateNewTeamResponse response = (CreateNewTeamResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_TEAM_URI));
            if(!response.getCreateNewTeamResult().isIsSuccesfull()){
                log.error("Error occurred while creating Team {} with parentId {}", teamName, parentId);
                throw new CheckmarxException("Error occurred during team creation");
            }
        }catch(NullPointerException e){
            log.error("Error occurred while creating Team {} with parentId {}", teamName, parentId);
            throw new CheckmarxException("Error occurred during team creation");
        }
    }

    void deleteTeam(String sessionId, String teamId) throws CheckmarxException {
        DeleteTeam request = new DeleteTeam();
        request.setSessionID(sessionId);
        request.setTeamID(teamId);
        log.info("Deleting team id {}", teamId);

        try {
            DeleteTeamResponse response = (DeleteTeamResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_DELETE_TEAM_URI));
            if(!response.getDeleteTeamResult().isIsSuccesfull()){
                log.error("Error occurred while deleting Team id {}", teamId);
                throw new CheckmarxException("Error occurred during team deletion");
            }
        }catch(NullPointerException e){
            log.error("Error occurred while deleting Team id {}", teamId);
            throw new CheckmarxException("Error occurred during team deletion");
        }
    }

    String getDescription(String session, Long scanId, Long pathId){
        GetResultDescription request = new GetResultDescription(session);
        request.setPathID(pathId);
        request.setScanID(scanId);

        log.debug("Retrieving description for {} / {} ", scanId, pathId);

        GetResultDescriptionResponse response = (GetResultDescriptionResponse)
                ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_DESCRIPTION_URI));
        try{
            if(!response.getGetResultDescriptionResult().isIsSuccesfull()){
                log.error(response.getGetResultDescriptionResult().getErrorMessage());
                return "";
            }
            else {
                String description = response.getGetResultDescriptionResult().getResultDescription();
                description = description.replace(properties.getHtmlStrip(), "");
                description = description.replaceAll("\\<.*?>", ""); /*Strip tag elements*/
                return description;
            }
        }catch (NullPointerException e){
            log.warn("Error occurred getting description for {} / {}", scanId, pathId);
            return "";
        }
    }

    void createLdapTeamMapping(String session, Integer ldapServerId, String teamId, String teamName, String groupDn) throws CheckmarxException{
        GetTeamLdapGroupsMapping ldapReq = new GetTeamLdapGroupsMapping();

        ldapReq.setSessionId(session);
        ldapReq.setTeamId(teamId);
        log.info("Retrieving existing Ldap Group Mappings for ldap server {}", ldapServerId);
        GetTeamLdapGroupsMappingResponse ldapResponse = (GetTeamLdapGroupsMappingResponse)
                ws.marshalSendAndReceive(ws.getDefaultUri(), ldapReq, new SoapActionCallback(CX_WS_TEAM_LDAP_MAPPINGS_URI));

        if (ldapResponse.getGetTeamLdapGroupsMappingResult().isIsSuccesfull()) {
            log.debug("Successfully retrieved ldapMappings");
            log.debug(ldapResponse.getGetTeamLdapGroupsMappingResult().getLdapGroups().getCxWSLdapGroupMapping().toString());
            CxWSLdapGroupMapping newMapping = new CxWSLdapGroupMapping();

            CxWSLdapGroup ldapGroup = new CxWSLdapGroup();
            ldapGroup.setDN(groupDn);
            String name = CxService.getNameFromLDAP(groupDn);

            ldapGroup.setName(name);

            newMapping.setLdapGroup(ldapGroup);
            newMapping.setLdapServerId(ldapServerId);

            ArrayOfCxWSLdapGroupMapping ldapArray = ldapResponse.getGetTeamLdapGroupsMappingResult().getLdapGroups();
            List<CxWSLdapGroupMapping> ldapGroupMapping = ldapArray.getCxWSLdapGroupMapping();
            if (!ldapGroupMapping.contains(newMapping)) {
                ldapGroupMapping.add(newMapping);
                updateTeam(session, teamId, teamName, ldapArray);
            } else {
                log.warn("Ldap mapping already exists for {} - {}", ldapServerId, groupDn);
            }

        } else {
            log.error("Error occurred while getting team ldap mapping {}", ldapResponse.getGetTeamLdapGroupsMappingResult().getErrorMessage());
            throw new CheckmarxException("Error occurred while getting team ldap mapping".concat(ldapResponse.getGetTeamLdapGroupsMappingResult().getErrorMessage()));
        }
    }

    private void updateTeam(String session, String teamId, String teamName, ArrayOfCxWSLdapGroupMapping ldapArray) throws CheckmarxException {
        UpdateTeam updateTeamReq = new UpdateTeam();
        updateTeamReq.setSessionID(session);
        updateTeamReq.setLdapGroupMappings(ldapArray);
        updateTeamReq.setTeamID(teamId);
        updateTeamReq.setNewTeamName(teamName);
        UpdateTeamResponse updateTeamResponse = (UpdateTeamResponse)
                ws.marshalSendAndReceive(ws.getDefaultUri(), updateTeamReq, new SoapActionCallback(CX_WS_UPDATE_TEAM_URI));
        if (!updateTeamResponse.getUpdateTeamResult().isIsSuccesfull()) {
            log.error("Error occurred while updating team ldap mapping {}", updateTeamResponse.getUpdateTeamResult().getErrorMessage());
            throw new CheckmarxException("Error occurred while updating team ldap mapping {}".concat(updateTeamResponse.getUpdateTeamResult().getErrorMessage()));
        }
    }

    void removeLdapTeamMapping(String session, Integer ldapServerId, String teamId, String teamName, String groupDn) throws CheckmarxException{
        GetTeamLdapGroupsMapping ldapReq = new GetTeamLdapGroupsMapping();

        ldapReq.setSessionId(session);
        ldapReq.setTeamId(teamId);
        log.info("Retrieving existing Ldap Group Mappings for ldap server {}", ldapServerId);
        GetTeamLdapGroupsMappingResponse ldapResponse = (GetTeamLdapGroupsMappingResponse)
                ws.marshalSendAndReceive(ws.getDefaultUri(), ldapReq, new SoapActionCallback(CX_WS_TEAM_LDAP_MAPPINGS_URI));

        if (ldapResponse.getGetTeamLdapGroupsMappingResult().isIsSuccesfull()) {
            log.debug("Successfully retrieved ldapMappings");
            log.debug(ldapResponse.getGetTeamLdapGroupsMappingResult().getLdapGroups().getCxWSLdapGroupMapping().toString());
            CxWSLdapGroupMapping newMapping = new CxWSLdapGroupMapping();

            CxWSLdapGroup ldapGroup = new CxWSLdapGroup();
            ldapGroup.setDN(groupDn);
            String name = CxService.getNameFromLDAP(groupDn);

            ldapGroup.setName(name);

            newMapping.setLdapGroup(ldapGroup);
            newMapping.setLdapServerId(ldapServerId);

            ArrayOfCxWSLdapGroupMapping ldapArray = ldapResponse.getGetTeamLdapGroupsMappingResult().getLdapGroups();
            List<CxWSLdapGroupMapping> ldapGroupMapping = ldapArray.getCxWSLdapGroupMapping();
            if (ldapGroupMapping.contains(newMapping)) {
                ldapGroupMapping.remove(newMapping);
                updateTeam(session, teamId, teamName, ldapArray);
            } else {
                log.warn("Ldap mapping already exists for {} - {}", ldapServerId, groupDn);
            }

        } else {
            log.error("Error occurred while getting team ldap mapping {}", ldapResponse.getGetTeamLdapGroupsMappingResult().getErrorMessage());
            throw new CheckmarxException("Error occurred while getting team ldap mapping".concat(ldapResponse.getGetTeamLdapGroupsMappingResult().getErrorMessage()));
        }
    }

    Integer getLdapServerId(String session, String serverName) throws  CheckmarxException{
        GetLdapServersConfigurations request = new GetLdapServersConfigurations();
        request.setSessionId(session);

        log.debug("Retrieving Ldap Server Configurations");

        GetLdapServersConfigurationsResponse response = (GetLdapServersConfigurationsResponse)
                ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_LDAP_CONFIGURATIONS_URI));
        try{
            if(!response.getGetLdapServersConfigurationsResult().isIsSuccesfull()){
                log.error(response.getGetLdapServersConfigurationsResult().getErrorMessage());
                throw new CheckmarxException(response.getGetLdapServersConfigurationsResult().getErrorMessage());
            }
            else {
                List<CxWSLdapServerConfiguration> ldapConfigs = response.getGetLdapServersConfigurationsResult().getServerConfigs().getCxWSLdapServerConfiguration();
                for(CxWSLdapServerConfiguration ldap: ldapConfigs){
                    if(ldap.getName().equalsIgnoreCase(serverName)){
                        return ldap.getId();
                    }
                }
                return -1;
            }
        }catch (NullPointerException e){
            log.warn("Error occurred getting ldap server configurations");
            throw new CheckmarxException("Error occurred while getting ldap server configurations");
        }
    }

}
