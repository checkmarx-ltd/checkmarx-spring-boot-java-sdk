package com.checkmarx.sdk.dto.cxgo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "currentPage",
        "pageSize"
})
public class Pagination {

    @JsonProperty("currentPage")
    private String currentPage;
    @JsonProperty("pageSize")
    private String pageSize;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("currentPage")
    public String getCurrentPage() {
        return currentPage;
    }

    @JsonProperty("currentPage")
    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    @JsonProperty("pageSize")
    public String getPageSize() {
        return pageSize;
    }

    @JsonProperty("pageSize")
    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
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
