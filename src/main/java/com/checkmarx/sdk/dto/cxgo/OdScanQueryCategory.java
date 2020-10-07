package com.checkmarx.sdk.dto.cxgo;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "severityCode",
        "severity",
        "title",
        "vulnerabilitiesAmount"
})
public class OdScanQueryCategory {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("severityCode")
    private Integer severityCode;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("title")
    private String title;

    @JsonProperty("vulnerabilitiesAmount")
    private Integer vulnerabilitiesAmount;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("severityCode")
    public Integer getSeverityCode() {
        return severityCode;
    }

    @JsonProperty("severityCode")
    public void setSeverityCode(Integer severityCode) {
        this.severityCode = severityCode;
    }

    @JsonProperty("severity")
    public String getSeverity() {
        return severity;
    }

    @JsonProperty("severity")
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("vulnerabilitiesAmount")
    public Integer getVulnerabilitiesAmount() {
        return vulnerabilitiesAmount;
    }

    @JsonProperty("vulnerabilitiesAmount")
    public void setVulnerabilitiesAmount(Integer vulnerabilitiesAmount) {
        this.vulnerabilitiesAmount = vulnerabilitiesAmount;
    }
}