package com.checkmarx.sdk.dto.cxgo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data"
})
public class OdScanFileResult {
    @JsonProperty("data")
    private OdScanFile data;

    @JsonProperty("data")
    public OdScanFile getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(OdScanFile data) {
        this.data = data;
    }
}
