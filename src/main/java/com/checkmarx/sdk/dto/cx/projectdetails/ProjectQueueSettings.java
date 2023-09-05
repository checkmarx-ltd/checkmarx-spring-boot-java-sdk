
package com.checkmarx.sdk.dto.cx.projectdetails;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "queueKeepMode",
    "scansType",
    "includeScansInProcess",
    "identicalCodeOnly"
})
@Generated("jsonschema2pojo")
public class ProjectQueueSettings {

    @JsonProperty("queueKeepMode")
    private String queueKeepMode;
    @JsonProperty("scansType")
    private String scansType;
    @JsonProperty("includeScansInProcess")
    private Boolean includeScansInProcess;
    @JsonProperty("identicalCodeOnly")
    private Boolean identicalCodeOnly;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("queueKeepMode")
    public String getQueueKeepMode() {
        return queueKeepMode;
    }

    @JsonProperty("queueKeepMode")
    public void setQueueKeepMode(String queueKeepMode) {
        this.queueKeepMode = queueKeepMode;
    }

    @JsonProperty("scansType")
    public String getScansType() {
        return scansType;
    }

    @JsonProperty("scansType")
    public void setScansType(String scansType) {
        this.scansType = scansType;
    }

    @JsonProperty("includeScansInProcess")
    public Boolean getIncludeScansInProcess() {
        return includeScansInProcess;
    }

    @JsonProperty("includeScansInProcess")
    public void setIncludeScansInProcess(Boolean includeScansInProcess) {
        this.includeScansInProcess = includeScansInProcess;
    }

    @JsonProperty("identicalCodeOnly")
    public Boolean getIdenticalCodeOnly() {
        return identicalCodeOnly;
    }

    @JsonProperty("identicalCodeOnly")
    public void setIdenticalCodeOnly(Boolean identicalCodeOnly) {
        this.identicalCodeOnly = identicalCodeOnly;
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
