package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "errors",
        "data"
})
public class OdScanList {
    @JsonProperty("errors")
    private List<Object> errors = null;
    @JsonProperty("data")
    private OdScanListData data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("errors")
    public List<Object> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    @JsonProperty("data")
    public OdScanListData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(OdScanListData data) {
        this.data = data;
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