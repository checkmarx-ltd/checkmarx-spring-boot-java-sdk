package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name",
        "fullName",
        "parentId"
})
public class CxTeam {

    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("fullName")
    public String fullName;
    @JsonProperty("parentId")
    public String parentId;

    public CxTeam() {
    }

    public String getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "CxTeam{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}