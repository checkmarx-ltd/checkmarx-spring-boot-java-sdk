package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.CxUser;
import com.checkmarx.sdk.exception.CheckmarxException;
import org.slf4j.Logger;

import java.util.List;

public class CxUserService implements CxUserClient{
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxUserService.class);
    private final CxAuthClient authClient;
    private final CxLegacyService cxLegacyService;
    private final CxProperties cxProperties;

    public CxUserService(CxAuthClient authClient, CxLegacyService cxLegacyService, CxProperties cxProperties) {
        this.authClient = authClient;
        this.cxLegacyService = cxLegacyService;
        this.cxProperties = cxProperties;
    }

    public List<CxUser> getUsers(){
        return null;
    }

    public List<CxUser> getUsersWS(){
        String session = authClient.getLegacySession();

        return null;
    }

    public CxUser getUser(Integer id){
        return null;
    }

    public void updateUser(CxUser user) throws CheckmarxException{

    }

    public void addUser(CxUser user) throws CheckmarxException{

    }

    public void addUserWS(String session, CxUser user) {

    }



}
