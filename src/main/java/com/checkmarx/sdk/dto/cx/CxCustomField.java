package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CxCustomField {

    @JsonProperty
    public Integer id;
    @JsonProperty
    public String name;

    @java.beans.ConstructorProperties({"id", "name"})
    public CxCustomField(Integer id, String name) {
	this.id = id;
	this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
	return "CxCustomField [id=" + id + ", name=" + name + "]";
    }
}
