package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CxRoleLdap {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("ldapServerId")
    public Integer ldapServerId;
    @JsonProperty("roleId")
    public Integer roleId;
    @JsonProperty("ldapGroupDn")
    public String ldapGroupDn;
    @JsonProperty("ldapGroupDisplayName")
    public String ldapGroupDisplayName;

    public CxRoleLdap(){}

    public CxRoleLdap(Integer id, Integer ldapServerId, Integer roleId, String ldapGroupDn, String ldapGroupDisplayName) {
        this.id = id;
        this.ldapServerId = ldapServerId;
        this.roleId = roleId;
        this.ldapGroupDn = ldapGroupDn;
        this.ldapGroupDisplayName = ldapGroupDisplayName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLdapServerId() {
        return ldapServerId;
    }

    public void setLdapServerId(Integer ldapServerId) {
        this.ldapServerId = ldapServerId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getLdapGroupDn() {
        return ldapGroupDn;
    }

    public void setLdapGroupDn(String ldapGroupDn) {
        this.ldapGroupDn = ldapGroupDn;
    }

    public String getLdapGroupDisplayName() {
        return ldapGroupDisplayName;
    }

    public void setLdapGroupDisplayName(String ldapGroupDisplayName) {
        this.ldapGroupDisplayName = ldapGroupDisplayName;
    }

    public CxRoleLdap id(final Integer id) {
        this.id = id;
        return this;
    }

    public CxRoleLdap ldapServerId(final Integer ldapServerId) {
        this.ldapServerId = ldapServerId;
        return this;
    }

    public CxRoleLdap teamId(final Integer roleId) {
        this.roleId = roleId;
        return this;
    }

    public CxRoleLdap ldapGroupDn(final String ldapGroupDn) {
        this.ldapGroupDn = ldapGroupDn;
        return this;
    }

    public CxRoleLdap ldapGroupDisplayName(final String ldapGroupDisplayName) {
        this.ldapGroupDisplayName = ldapGroupDisplayName;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        CxRoleLdap objTmp = (CxRoleLdap) obj;
        if(objTmp.getLdapServerId().equals(this.ldapServerId) &&
                objTmp.roleId.equals(this.roleId) &&
                objTmp.getLdapGroupDn().equalsIgnoreCase(this.getLdapGroupDn())){
            return true;
        }
        return false;
    }
}

