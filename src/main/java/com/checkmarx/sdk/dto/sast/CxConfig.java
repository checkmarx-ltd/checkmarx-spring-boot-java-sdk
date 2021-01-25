package com.checkmarx.sdk.dto.sast;

import com.checkmarx.sdk.dto.Osa;
import com.checkmarx.sdk.dto.sca.Sca;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the contents of a config-as-code file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CxConfig implements Serializable {

    @JsonProperty("version")
    private Double version;
    @JsonProperty("active")
    private Boolean active = true;
    @JsonProperty("host")
    private String host;
    @JsonProperty("credential")
    private Credential credential;
    @JsonProperty("project")
    private String project;
    @JsonProperty("team")
    private String team;
    @JsonProperty("policy")
    private String policy;
    @JsonProperty("customFields")
    private Map<String, String> customFields;
    @JsonProperty("sast")
    private Sast sast;
    @JsonProperty("osa")
    private Osa osa;
    @JsonProperty("additionalProperties")
    private Map<String, Object> additionalProperties = new HashMap<>();
    @JsonProperty("sca")
    private Sca sca;

    private static final long serialVersionUID = 2851455437649831239L;

    public Sca getSca() {
        return sca;
    }

    public void setSca(Sca sca) {
        this.sca = sca;
    }

    @JsonProperty("version")
    public Double getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(Double version) {
        this.version = version;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("cxHost")
    public String getCxHost() {
        return host;
    }

    @JsonProperty("cxHost")
    public void setCxHost(String cxHost) {
        this.host = cxHost;
    }

    @JsonProperty("credential")
    public Credential getCredential() {
        return credential;
    }

    @JsonProperty("credential")
    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    @JsonProperty("project")
    public String getProject() {
        return project;
    }

    @JsonProperty("project")
    public void setProject(String project) {
        this.project = project;
    }

    @JsonProperty("team")
    public String getTeam() {
        return team;
    }

    @JsonProperty("team")
    public void setTeam(String team) {
        this.team = team;
    }

    @JsonProperty("policy")
    public String getPolicy() {
        return policy;
    }

    @JsonProperty("policy")
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    @JsonProperty("customFields")
    public Map<String, String> getCustomFields() {
        return customFields;
    }

    @JsonProperty("customFields")
    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    @JsonProperty("sast")
    public Sast getSast() {
        return sast;
    }

    @JsonProperty("sast")
    public void setSast(Sast sast) {
        this.sast = sast;
    }

    @JsonProperty("osa")
    public Osa getOsa() {
        return osa;
    }

    @JsonProperty("osa")
    public void setOsa(Osa osa) {
        this.osa = osa;
    }

    @JsonProperty("additionalProperties")
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}