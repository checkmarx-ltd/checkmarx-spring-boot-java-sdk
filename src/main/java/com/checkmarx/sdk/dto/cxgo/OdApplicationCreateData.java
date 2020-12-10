package com.checkmarx.sdk.dto.cxgo;

import com.fasterxml.jackson.annotation.*;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "baName",
        "description",
        "criticality",
        "baBuId"
})
public class OdApplicationCreateData {
    @JsonProperty("baId")
    private String baId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("baId")
    public String getBaId() {
        return baId;
    }

    @JsonProperty("baId")
    public void setBaId(String baId) {
        this.baId = baId;
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