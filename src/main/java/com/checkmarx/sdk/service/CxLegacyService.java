package com.checkmarx.sdk.service;

import checkmarx.wsdl.portal.*;
import com.checkmarx.sdk.ShardManager.ShardSession;
import com.checkmarx.sdk.ShardManager.ShardSessionTracker;
import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.sast.CxUser;
import com.checkmarx.sdk.exception.CheckmarxException;
import com.checkmarx.sdk.exception.CheckmarxLegacyException;
import com.checkmarx.sdk.utils.ScanUtils;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import java.io.IOException;
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
    private final ShardSessionTracker sessionTracker;

    private static final String CX_WS_PREFIX= "http://Checkmarx.com/";
    private static final String CX_WS_LOGIN_URI = CX_WS_PREFIX + "LoginV2";
    private static final String CX_WS_DESCRIPTION_URI = CX_WS_PREFIX + "GetResultDescription";
    private static final String CX_WS_LDAP_CONFIGURATIONS_URI = CX_WS_PREFIX + "GetLdapServersConfigurations";
    private static final String CX_WS_TEAM_LDAP_MAPPINGS_URI = CX_WS_PREFIX + "GetTeamLdapGroupsMapping";
    private static final String CX_WS_ADD_USER = CX_WS_PREFIX + "AddNewUser";
    private static final String CX_WS_UPDATE_USER = CX_WS_PREFIX + "UpdateUserData";
    private static final String CX_WS_ALL_USERS = CX_WS_PREFIX + "GetAllUsers";
    private static final String CX_WS_GET_USER = CX_WS_PREFIX + "GetUserById";
    private static final String CX_WS_UPDATE_TEAM_URI = CX_WS_PREFIX + "UpdateTeam";
    private static final String CX_WS_CREATE_TEAM_URI = CX_WS_PREFIX + "CreateNewTeam";
    private static final String CX_WS_DELETE_TEAM_URI = CX_WS_PREFIX + "DeleteTeam";
    private static final String CX_WS_MOVE_TEAM_URI = CX_WS_PREFIX + "MoveTeam";
    private static final String CX_WS_GET_COMPANIES_TEAM_URI = CX_WS_PREFIX + "GetAllCompanies";
    private static final Map<Integer, CxUser.Role8x> ROLEMAP = ImmutableMap.of(
            0, CxUser.Role8x.SCANNER,
            1, CxUser.Role8x.REVIEWER,
            2, CxUser.Role8x.COMPANYMANAGER,
            4, CxUser.Role8x.SPMANAGER,
            5, CxUser.Role8x.SERVERMANAGER
    );

    public CxLegacyService(CxProperties properties, WebServiceTemplate ws, ShardSessionTracker sessionTracker) {
        this.properties = properties;
        this.ws = ws;
        this.sessionTracker = sessionTracker;
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
        WebServiceTemplate wsInstance = ws;
        // If shards are enabled then fetch the current shard info for override.
        if(properties.getEnableShardManager()) {
            ShardSession shard = sessionTracker.getShardSession();
            wsInstance = shard.getShardWs();
            username = shard.getUsername();
            password = shard.getPassword();
        }
        request.setApplicationCredentials(new Credentials(username, password));
        if(properties.getVersion() >= 9.0){
            return "-1";
        }
        LoginV2Response response = (LoginV2Response) wsInstance.marshalSendAndReceive(wsInstance.getDefaultUri(), request, new SoapActionCallback(CX_WS_LOGIN_URI));
        try {
            if(!response.getLoginV2Result().isIsSuccesfull())
                throw new CheckmarxLegacyException("Authentication Error");
            if(properties.getEnableShardManager()) {
                ShardSession shard = sessionTracker.getShardSession();
                shard.setSoapToken(response.getLoginV2Result().getSessionId());
            }
            return response.getLoginV2Result().getSessionId();
        }
        catch(NullPointerException e){
            log.error("Authentication Error while logging into CX using SOAP WS");
            throw new CheckmarxLegacyException("Authentication Error");
        }
    }

    /**
     * @param session
     * @param company
     * @return
     * @throws CheckmarxLegacyException
     */
    public String getCompany(String session, String company) throws CheckmarxLegacyException {
        GetAllCompanies request = new GetAllCompanies();
        request.setSessionID(session);
        GetAllCompaniesResponse response = (GetAllCompaniesResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_GET_COMPANIES_TEAM_URI));
        if(!response.getGetAllCompaniesResult().isIsSuccesfull()) {
            throw new CheckmarxLegacyException("Error getting Companies: "+ response.getGetAllCompaniesResult().getErrorMessage());
        }
        List<TeamData> teams = response.getGetAllCompaniesResult().getTeamDataList().getTeamData();
        for(TeamData t: teams){
            String team = t.getCompany().getGroupName();
            if(team.equalsIgnoreCase(company)){
                return t.getCompany().getGuid();
            }
        }
        throw new CheckmarxLegacyException("Company not found");
    }

    public List<CxUser> getUsers(String session) throws CheckmarxLegacyException {
        GetAllUsers request = new GetAllUsers();
        request.setSessionID(session);
        GetAllUsersResponse response = (GetAllUsersResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_ALL_USERS));
        if(!response.getGetAllUsersResult().isIsSuccesfull()) {
            throw new CheckmarxLegacyException("Error getting Users: "+ response.getGetAllUsersResult().getErrorMessage());
        }
        List<UserData> userData = response.getGetAllUsersResult().getUserDataList().getUserData();
        List<CxUser> users = new ArrayList<>();
        for(UserData u: userData){
            users.add(mapUser(u));
        }

        return users;
    }

    public CxUser getUser(String session, Integer id) throws CheckmarxLegacyException {
        GetUserById request = new GetUserById();
        request.setSessionID(session);
        request.setUserId(id);
        GetUserByIdResponse response = (GetUserByIdResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_GET_USER));
        if(!response.getGetUserByIdResult().isIsSuccesfull()) {
            throw new CheckmarxLegacyException("Error user by Id: "+ response.getGetUserByIdResult().getErrorMessage());
        }
        UserData userData = response.getGetUserByIdResult().getUserData();
        return mapUser(userData);
    }

    private CxUser mapUser(UserData u){
        CxUser user = new CxUser();
        user.setId(u.getID());
        user.setActive(u.isIsActive());
        user.setLastLoginDate(u.getLastLoginDate().toString());
        user.setUserName(u.getUserName());
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
        CxUser.Role8x role = ROLEMAP.get(Integer.parseInt(u.getRoleData().getID()));
        if(role != null) {
            user.setRole8x(role); //validate
        }
        else{
            log.warn("Uknown role id {}", u.getRoleData().getID());
        }
        user.setAuditor(u.isAuditUser());
        return user;
    }

    public void addUser(String session, CxUser user) throws CheckmarxLegacyException{

        if(ScanUtils.empty(user.getCompany8x()) ||
            user.getTeams8x() == null || user.getTeams8x().isEmpty()  ||
            user.getType8x() == null || user.getRole8x() == null
        ){
            throw new CheckmarxLegacyException("Missing team, type, or company details from user details");
        }
        if(user.getType8x().equals(CxUserTypes.LDAP) && ScanUtils.empty(user.getUpn())){
            throw new CheckmarxLegacyException("Missing upn, which is required for LDAP user");
        }
        if(user.getType8x().equals(CxUserTypes.SAML)){
            String username = user.getUserName();
            if(username.startsWith("SAML\\")){
                username = username.replace("SAML\\","SAML#");
            }
            if(!username.startsWith("SAML#")){
                username = "SAML#".concat(username);
            }
            user.setUserName(username);
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
        if(ScanUtils.empty(user.getCompanyId8x())){
            user.setCompanyId8x(getCompany(session, user.getCompany8x()));
        }
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
            throw new CheckmarxLegacyException("Error occurred while creating user: "+ response.getAddNewUserResult().getErrorMessage());
        }
    }

    public void updateUser(String session, CxUser user) throws CheckmarxLegacyException{

        if(ScanUtils.empty(user.getCompany8x()) || ScanUtils.empty(user.getCompanyId8x()) ||
                user.getTeams8x() == null || user.getTeams8x().isEmpty()  ||
                user.getRole8x() == null
        ){
            throw new CheckmarxLegacyException("Missing team, type, or company details from user details");
        }
        UpdateUserData request = new UpdateUserData();
        request.setSessionID(session);
        UserData userData = new UserData();
        userData.setID(user.getId());
        userData.setIsActive(user.getActive());
        userData.setAuditUser(user.isAuditor());
        userData.setUserPreferedLanguageLCID(user.getLanguageLCID());
        userData.setEmail(user.getEmail());
        if(!ScanUtils.empty(user.getPassword())) {
            userData.setPassword(user.getPassword());
        }
        userData.setFirstName(user.getFirstName());
        userData.setLastName(user.getLastName());
        if (!ScanUtils.empty(user.getPassword())) {
            userData.setPassword(user.getPassword());
        }
        if(user.getExpirationDays() != null && user.getExpirationDays() > 0){
            userData.setWillExpireAfterDays(user.getExpirationDays().toString());
        }

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

        UpdateUserDataResponse response = (UpdateUserDataResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_UPDATE_USER));
        if(!response.getUpdateUserDataResult().isIsSuccesfull()){
            log.error(response.getUpdateUserDataResult().getErrorMessage());
            throw new CheckmarxLegacyException("Error occurred while updating user: "+ response.getUpdateUserDataResult().getErrorMessage());
        }
    }

    public void deleteUser(String session, Integer userId) throws CheckmarxLegacyException {
        DeleteUser request = new DeleteUser();
        request.setSessionID(session);
        request.setUserID(userId);
        DeleteUserResponse response = (DeleteUserResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_GET_USER));
        if(!response.getDeleteUserResult().isIsSuccesfull()) {
            throw new CheckmarxLegacyException("Error deleting user with Id: "+ response.getDeleteUserResult().getErrorMessage());
        }
    }

    void createTeam(String sessionId, String parentId, String teamName) throws CheckmarxException {
        CreateNewTeam request = new CreateNewTeam(sessionId);
        request.setNewTeamName(teamName);
        request.setParentTeamID(parentId);
        log.info("Creating team {} ({})", teamName, parentId);
        // If shards are enabled then fetch the current shard info for override.
        WebServiceTemplate wsInstance = ws;
        if(properties.getEnableShardManager()) {
            ShardSession shard = sessionTracker.getShardSession();
            wsInstance = shard.getShardWs();
        }

        try {
            CreateNewTeamResponse response = (CreateNewTeamResponse) wsInstance.marshalSendAndReceive(wsInstance.getDefaultUri(), request, new SoapActionCallback(CX_WS_CREATE_TEAM_URI));
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

    void moveTeam(String sessionId, String teamId, String newParentId) throws CheckmarxException {
        MoveTeam request = new MoveTeam();
        request.setSessionID(sessionId);
        request.setSourceID(teamId);
        request.setDestenationID(newParentId);
        log.info("Moving team {} to under {}", teamId, newParentId);

        try {
            MoveTeamResponse response = (MoveTeamResponse) ws.marshalSendAndReceive(ws.getDefaultUri(), request, new SoapActionCallback(CX_WS_MOVE_TEAM_URI));
            if(!response.getMoveTeamResult().isIsSuccesfull()){
                log.error("Error occurred while moving team {} under parentId {}", teamId, newParentId);
                throw new CheckmarxException("Error occurred during team move");
            }
        } catch(NullPointerException e){
            log.error("Error occurred while moving team {} under parentId {}", teamId, newParentId);
            throw new CheckmarxException("Error occurred during team move");
        }
    }

    String getDescription(String session, Long scanId, Long pathId){
        GetResultDescription request = new GetResultDescription(session);
        request.setPathID(pathId);
        request.setScanID(scanId);
        log.debug("Retrieving description for {} / {} ", scanId, pathId);
        WebServiceTemplate wsInstance = ws;
        String shardURI = wsInstance.getDefaultUri();
        // If shards are enabled then fetch the current shard info for override.
        if(properties.getEnableShardManager()) {
            ShardSession shard = sessionTracker.getShardSession();
            wsInstance = shard.getShardWs();
            shardURI = shard.getUrl() + "/cxwebinterface/Portal/CxWebService.asmx";
        }
        GetResultDescriptionResponse response = (GetResultDescriptionResponse)
                wsInstance.marshalSendAndReceive(shardURI, request, getWSCallback(CX_WS_DESCRIPTION_URI, session));
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

    void updateTeam(String session, String teamId, String teamName, ArrayOfCxWSLdapGroupMapping ldapArray) throws CheckmarxException {
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

    private WebServiceMessageCallback getWSCallback(String callbackUri, String token){
        String curToken;
        if(properties.getEnableShardManager()) {
            ShardSession shard = sessionTracker.getShardSession();
            curToken = shard.getSoapToken();
        } else {
            curToken = token;
        }
        return message -> {
            SoapMessage soapMessage = (SoapMessage) message;
            soapMessage.setSoapAction(callbackUri);
            TransportContext context = TransportContextHolder.getTransportContext();
            HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
            try {
                if(!ScanUtils.empty(curToken) && properties.getVersion() >= 9.0) {
                    connection.addRequestHeader(HttpHeaders.AUTHORIZATION, "Bearer ".concat(token));
                }
            }catch (IOException e){
                log.warn("Problem adding SOAP WS http header: {}", ExceptionUtils.getStackTrace(e));
            }
        };
    }

}
