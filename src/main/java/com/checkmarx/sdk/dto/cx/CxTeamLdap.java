package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

public class CxTeamLdap {

    @JsonProperty("id")
    public Integer id;
    @JsonProperty("ldapServerId")
    public Integer ldapServerId;
    @JsonProperty("teamId")
    public String teamId;
    @JsonProperty("ldapGroupDn")
    public String ldapGroupDn;
    @JsonProperty("ldapGroupDisplayName")
    public String ldapGroupDisplayName;

    public CxTeamLdap(Integer id, Integer ldapServerId, String teamId, String ldapGroupDn, String ldapGroupDisplayName) {
        this.id = id;
        this.ldapServerId = ldapServerId;
        this.teamId = teamId;
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

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
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

    public CxTeamLdap id(final Integer id) {
        this.id = id;
        return this;
    }

    public CxTeamLdap ldapServerId(final Integer ldapServerId) {
        this.ldapServerId = ldapServerId;
        return this;
    }

    public CxTeamLdap teamId(final String teamId) {
        this.teamId = teamId;
        return this;
    }

    public CxTeamLdap ldapGroupDn(final String ldapGroupDn) {
        this.ldapGroupDn = ldapGroupDn;
        return this;
    }

    public CxTeamLdap ldapGroupDisplayName(final String ldapGroupDisplayName) {
        this.ldapGroupDisplayName = ldapGroupDisplayName;
        return this;
    }

}

