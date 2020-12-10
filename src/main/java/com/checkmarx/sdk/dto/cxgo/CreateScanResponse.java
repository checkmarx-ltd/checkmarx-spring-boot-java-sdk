package com.checkmarx.sdk.dto.cxgo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateScanResponse {
    @JsonProperty("scan")
    private Scan scan;
    @JsonProperty("storage")
    private Storage storage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("scan")
    public Scan getScan() {
        return scan;
    }

    @JsonProperty("scan")
    public void setScan(Scan scan) {
        this.scan = scan;
    }

    @JsonProperty("storage")
    public Storage getStorage() {
        return storage;
    }

    @JsonProperty("storage")
    public void setStorage(Storage storage) {
        this.storage = storage;
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