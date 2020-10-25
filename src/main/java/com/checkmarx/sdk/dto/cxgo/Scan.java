package com.checkmarx.sdk.dto.cxgo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Scan {
    /**

     */
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("status")
    private String status;
    @JsonProperty("progress")
    private Integer progress;
    @JsonProperty("fail_code")
    private String failCode;
    @JsonProperty("engine_types")
    private List<String> engineTypes = null;
    @JsonProperty("risk_level")
    private String riskLevel;
    @JsonProperty("project_id")
    private Integer projectId;
    @JsonProperty("application_id")
    private Integer applicationId;
    @JsonProperty("business_unit_id")
    private Integer businessUnitId;
    @JsonProperty("scanner")
    private Scanner scanner;
    @JsonProperty("engines")
    private Map<String, Object> engines;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("progress")
    public Integer getProgress() {
        return progress;
    }

    @JsonProperty("progress")
    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @JsonProperty("fail_code")
    public String getFailCode() {
        return failCode;
    }

    @JsonProperty("fail_code")
    public void setFailCode(String failCode) {
        this.failCode = failCode;
    }

    @JsonProperty("engine_types")
    public List<String> getEngineTypes() {
        return engineTypes;
    }

    @JsonProperty("engine_types")
    public void setEngineTypes(List<String> engineTypes) {
        this.engineTypes = engineTypes;
    }

    @JsonProperty("risk_level")
    public String getRiskLevel() {
        return riskLevel;
    }

    @JsonProperty("risk_level")
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    @JsonProperty("scanner")
    public Scanner getScanner() {
        return scanner;
    }

    @JsonProperty("scanner")
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @JsonProperty("project_id")
    public Integer getProjectId() {
        return projectId;
    }

    @JsonProperty("project_id")
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("application_id")
    public Integer getApplicationId() {
        return applicationId;
    }

    @JsonProperty("application_id")
    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    @JsonProperty("business_unit_id")
    public Integer getBusinessUnitId() {
        return businessUnitId;
    }

    @JsonProperty("business_unit_id")
    public void setBusinessUnitId(Integer businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public Map<String, Object> getEngines() {
        return engines;
    }

    public void setEngines(Map<String, Object> engines) {
        this.engines = engines;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

