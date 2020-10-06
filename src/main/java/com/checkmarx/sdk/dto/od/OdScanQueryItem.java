package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id"
})
public class OdScanQueryItem {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("language")
    private String language;

    @JsonProperty("severity")
    private List<OdScanQuerySeverity> severity;

    @JsonProperty("categories")
    private List<OdScanQueryCategory> categories;

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

    @JsonProperty("language")
    public String getLanguage() {
        return language;
    }

    @JsonProperty("language")
    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonProperty("severity")
    public List<OdScanQuerySeverity> getSeverity() {
        return severity;
    }

    @JsonProperty("severity")
    public void setSeverity(List<OdScanQuerySeverity> severity) {
        this.severity = severity;
    }

    @JsonProperty("categories")
    public List<OdScanQueryCategory> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<OdScanQueryCategory> categories) {
        this.categories = categories;
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
