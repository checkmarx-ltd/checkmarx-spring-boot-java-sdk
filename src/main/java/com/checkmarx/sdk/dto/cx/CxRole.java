package com.checkmarx.sdk.dto.cx;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CxRole {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("isSystemRole")
    private Boolean isSystemRole;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("permissionIds")
    private List<Integer> permissionIds = null;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("isSystemRole")
    public Boolean getIsSystemRole() {
        return isSystemRole;
    }

    @JsonProperty("isSystemRole")
    public void setIsSystemRole(Boolean isSystemRole) {
        this.isSystemRole = isSystemRole;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("permissionIds")
    public List<Integer> getPermissionIds() {
        return permissionIds;
    }

    @JsonProperty("permissionIds")
    public void setPermissionIds(List<Integer> permissionIds) {
        this.permissionIds = permissionIds;
    }

}