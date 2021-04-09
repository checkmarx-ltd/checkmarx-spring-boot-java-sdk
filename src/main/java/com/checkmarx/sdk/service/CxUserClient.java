package com.checkmarx.sdk.service;

import com.checkmarx.sdk.dto.sast.CxUser;
import com.checkmarx.sdk.exception.CheckmarxException;

import java.util.List;


/**
 * Class used to orchestrate submitting scans and retrieving results
 */
public interface CxUserClient {
    public List<CxUser> getUsers() throws CheckmarxException;

    public CxUser getUser(Integer id) throws CheckmarxException;

    public void addUser(CxUser user) throws CheckmarxException;

    public void updateUser(CxUser user) throws CheckmarxException;

    public void deleteUser(Integer id) throws CheckmarxException;
}