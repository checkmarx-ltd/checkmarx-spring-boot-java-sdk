package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SCAScanResult {
    @JsonProperty("scan_id")
    private Integer scanId;
    @JsonProperty("id")
    private String id;
    @JsonProperty("cve_name")
    private String cveName;
    @JsonProperty("severity")
    private Severity severity;
    @JsonProperty("score")
    private Double score;
    @JsonProperty("published_at")
    private String publishedAt;
    @JsonProperty("references")
    private List<String> references;
    @JsonProperty("references_data")
    private Object referencesData;
    @JsonProperty("description")
    private String description;
    @JsonProperty("cvss")
    private Map<String, Object> cvss;
    @JsonProperty("recommendations")
    private String recommendations;
    @JsonProperty("package_id")
    private String packageId;
    @JsonProperty("similarity_id")
    private String similarityId;
    @JsonProperty("fix_resolution_text")
    private String fixResolutionText;
    @JsonProperty("is_ignored")
    private boolean isIgnored;
    @JsonProperty("exploitable_methods")
    private List<String> exploitableMethods;
    @JsonProperty("cwe")
    private String cwe;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("scan_id")
    public Integer getScanId() {
        return scanId;
    }

    @JsonProperty("scan_id")
    public void setScanId(Integer scanId) {
        this.scanId = scanId;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("cve_name")
    public String getCveName() {
        return cveName;
    }

    @JsonProperty("cve_name")
    public void setCveName(String cveName) {
        this.cveName = cveName;
    }

    @JsonProperty("severity")
    public Severity getSeverity() {
        return severity;
    }

    @JsonProperty("severity")
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @JsonProperty("score")
    public Double getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Double score) {
        this.score = score;
    }

    @JsonProperty("published_at")
    public String getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty("published_at")
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @JsonProperty("references")
    public List<String> getReferences() {
        return references;
    }

    @JsonProperty("references")
    public void setReferences(List<String> references) {
        this.references = references;
    }

    @JsonProperty("references_data")
    public Object getReferencesData() {
        return referencesData;
    }

    @JsonProperty("references_data")
    public void setReferencesData(Object referencesData) {
        this.referencesData = referencesData;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("cvss")
    public Map<String, Object> getCvss() {
        return cvss;
    }

    @JsonProperty("cvss")
    public void setCvss(Map<String, Object> cvss) {
        this.cvss = cvss;
    }

    @JsonProperty("recommendations")
    public String getRecommendations() {
        return recommendations;
    }

    @JsonProperty("recommendations")
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    @JsonProperty("package_id")
    public String getPackageId() {
        return packageId;
    }

    @JsonProperty("package_id")
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @JsonProperty("similarity_id")
    public String getSimilarityId() {
        return similarityId;
    }

    @JsonProperty("similarity_id")
    public void setSimilarityId(String similarityId) {
        this.similarityId = similarityId;
    }

    @JsonProperty("fix_resolution_text")
    public String getFixResolutionText() {
        return fixResolutionText;
    }

    @JsonProperty("fix_resolution_text")
    public void setFixResolutionText(String fixResolutionText) {
        this.fixResolutionText = fixResolutionText;
    }

    @JsonProperty("is_ignored")
    public boolean isIgnored() {
        return isIgnored;
    }

    @JsonProperty("is_ignored")
    public void setIgnored(boolean ignored) {
        isIgnored = ignored;
    }

    @JsonProperty("exploitable_methods")
    public List<String> getExploitableMethods() {
        return exploitableMethods;
    }

    @JsonProperty("exploitable_methods")
    public void setExploitableMethods(List<String> exploitableMethods) {
        this.exploitableMethods = exploitableMethods;
    }

    @JsonProperty("cwe")
    public String getCwe() {
        return cwe;
    }

    @JsonProperty("cwe")
    public void setCwe(String cwe) {
        this.cwe = cwe;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public enum Severity {
        HIGH("HIGH"),
        MEDIUM("MEDIUM"),
        LOW("LOW"),
        INFO("INFO"),
        High("High"),
        Medium("Medium"),
        Low("Low"),
        Info("Info");

        private String s;

        private Severity(String s) {
            this.s = s;
        }

        public String getSeverity() {
            return s;
        }

        public void setSeverity(String s) {
            this.s = s;
        }
    }
}
