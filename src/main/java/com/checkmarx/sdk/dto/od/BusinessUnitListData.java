package com.checkmarx.sdk.dto.od;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "items",
        "totalCount",
        "pagination",
        "sorting",
        "filters",
        "smartFilters"
})
public class BusinessUnitListData {

    @JsonProperty("items")
    private List<BusinessUnitListEntry> items = null;
    @JsonProperty("totalCount")
    private Long totalCount;
    @JsonProperty("pagination")
    private Pagination pagination;
    @JsonProperty("sorting")
    private List<Object> sorting = null;
    @JsonProperty("filters")
    private List<Object> filters = null;
    @JsonProperty("smartFilters")
    private List<Object> smartFilters = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("items")
    public List<BusinessUnitListEntry> getItems() {
        return items;
    }

    @JsonProperty("items")
    public void setItems(List<BusinessUnitListEntry> items) {
        this.items = items;
    }

    @JsonProperty("totalCount")
    public Long getTotalCount() {
        return totalCount;
    }

    @JsonProperty("totalCount")
    public void setTotalCount(Long totalCount) {
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

    @JsonProperty("sorting")
    public List<Object> getSorting() {
        return sorting;
    }

    @JsonProperty("sorting")
    public void setSorting(List<Object> sorting) {
        this.sorting = sorting;
    }

    @JsonProperty("filters")
    public List<Object> getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(List<Object> filters) {
        this.filters = filters;
    }

    @JsonProperty("smartFilters")
    public List<Object> getSmartFilters() {
        return smartFilters;
    }

    @JsonProperty("smartFilters")
    public void setSmartFilters(List<Object> smartFilters) {
        this.smartFilters = smartFilters;
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