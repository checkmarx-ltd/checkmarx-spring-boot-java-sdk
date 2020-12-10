package com.checkmarx.sdk.service;

import com.checkmarx.sdk.config.CxProperties;
import com.checkmarx.sdk.dto.CxUser;
import com.checkmarx.sdk.exception.CheckmarxException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CxUserService implements CxUserClient{
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CxUserService.class);
    private final CxAuthService authClient;
    private final CxLegacyService cxLegacyService;
    private final CxProperties cxProperties;

    public CxUserService(CxAuthService authClient, CxLegacyService cxLegacyService, CxProperties cxProperties) {
        this.authClient = authClient;
        this.cxLegacyService = cxLegacyService;
        this.cxProperties = cxProperties;
    }

    public List<CxUser> getUsers() throws CheckmarxException{
        if(cxProperties.getVersion() < 9.0){
            String session = authClient.getLegacySession();
            return cxLegacyService.getUsers(session);
        }
        else{
            log.warn("getUsers for 9.0 has not been implemented");
            throw new CheckmarxException("Operation not supported in 9.x");
        }
    }

    @Override
    public CxUser getUser(Integer id) throws CheckmarxException{
        if(cxProperties.getVersion() < 9.0){
            String session = authClient.getLegacySession();
            return cxLegacyService.getUser(session, id);
        }
        else{
            log.warn("getUser for 9.0 has not been implemented");
            throw new CheckmarxException("Operation not supported in 9.x");
        }
    }

    @Override
    public void updateUser(CxUser user) throws CheckmarxException{
        if(cxProperties.getVersion() < 9.0){
            String session = authClient.getLegacySession();
            cxLegacyService.updateUser(session, user);
        }
        else{
            log.warn("getUser for 9.0 has not been implemented");
            throw new CheckmarxException("Operation not supported in 9.x");
        }
    }

    @Override
    public void deleteUser(Integer id) throws CheckmarxException {
        if(cxProperties.getVersion() < 9.0){
            String session = authClient.getLegacySession();
            cxLegacyService.deleteUser(session, id);
        }
        else{
            log.warn("deleteUser for 9.0 has not been implemented");
            throw new CheckmarxException("Operation not supported in 9.x");
        }
    }

    @Override
    public void addUser(CxUser user) throws CheckmarxException{
        if(cxProperties.getVersion() < 9.0){
            String session = authClient.getLegacySession();
            cxLegacyService.addUser(session, user);
        }
        else{
            log.warn("getUsers for 9.0 has not been implemented");
            throw new CheckmarxException("Operation not supported in 9.x");
        }
    }

    public String getCompanyId(String company) throws CheckmarxException{
        if(cxProperties.getVersion() < 9.0){
            String session = authClient.getLegacySession();
            return cxLegacyService.getCompany(session, company);
        }
        else{
            log.warn("getUsers for 9.0 has not been implemented");
            throw new CheckmarxException("Operation not supported in 9.x");
        }
    }

}
