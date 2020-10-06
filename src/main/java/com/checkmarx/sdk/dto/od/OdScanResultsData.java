package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OdScanResultsData {
    @JsonProperty("items")
    private List<OdScanResultItem> items;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("pagination")
    private Pagination pagination;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("items")
    public List<OdScanResultItem> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setId(List<OdScanResultItem> items) {
        this.items = items;
    }

    @JsonProperty("totalCount")
    public Integer getTotalCount() {
        return totalCount;
    }

    @JsonProperty("totalCount")
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    @JsonProperty("pagination")
    public Pagination getPagination() {
        return pagination;
    }

    @JsonProperty("pagination")
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
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