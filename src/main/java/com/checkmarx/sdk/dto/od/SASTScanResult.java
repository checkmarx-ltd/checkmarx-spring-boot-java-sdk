package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SASTScanResult {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("type")
    private Type type;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("state")
    private Integer state;
    @JsonProperty("severity")
    private Severity severity;
    @JsonProperty("similarity_id")
    private Integer similarityId;
    @JsonProperty("has_notes")
    private Boolean hasNotes;
    @JsonProperty("source_node")
    private ResultNode sourceNode;
    @JsonProperty("sink_node")
    private ResultNode sinkNode;
    @JsonProperty("assignee")
    private Scanner assignee;
    @JsonProperty("description")
    private String description;
    @JsonProperty("language_name")
    private String languageName;
    @JsonProperty("vulnerability_type")
    private String vulnerabilityType;
    @JsonProperty("cwe")
    private String cwe;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
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

    @JsonProperty("type")
    public Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Type type) {
        this.type = type;
    }

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("state")
    public Integer getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(Integer state) {
        this.state = state;
    }

    @JsonProperty("severity")
    public Severity getSeverity() {
        return severity;
    }

    @JsonProperty("severity")
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    @JsonProperty("similarity_id")
    public Integer getSimilarityId() {
        return similarityId;
    }

    @JsonProperty("similarity_id")
    public void setSimilarityId(Integer similarityId) {
        this.similarityId = similarityId;
    }

    @JsonProperty("has_notes")
    public Boolean getHasNotes() {
        return hasNotes;
    }

    @JsonProperty("has_notes")
    public void setHasNotes(Boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    @JsonProperty("source_node")
    public ResultNode getSourceNode() {
        return sourceNode;
    }

    @JsonProperty("source_node")
    public void setSourceNode(ResultNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    @JsonProperty("sink_node")
    public ResultNode getSinkNode() {
        return sinkNode;
    }

    @JsonProperty("sink_node")
    public void setSinkNode(ResultNode sinkNode) {
        this.sinkNode = sinkNode;
    }

    @JsonProperty("assignee")
    public Scanner getAssignee() {
        return assignee;
    }

    @JsonProperty("assignee")
    public void setAssignee(Scanner assignee) {
        this.assignee = assignee;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("language_name")
    public String getLanguageName() {
        return languageName;
    }

    @JsonProperty("language_name")
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    @JsonProperty("vulnerability_type")
    public String getVulnerabilityType() {
        return vulnerabilityType;
    }

    @JsonProperty("vulnerability_type")
    public void setVulnerabilityType(String vulnerabilityType) {
        this.vulnerabilityType = vulnerabilityType;
    }

    public String getCwe() {
        return cwe;
    }

    public void setCwe(String cwe) {
        this.cwe = cwe;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public enum Type {
        VULNERABILITY("VULNERABILITY");

        private String t;

        private Type(String t) {
            this.t = t;
        }

        public String getType() {
            return t;
        }

        public void setType(String t) {
            this.t = t;
        }
    }

    public enum Status {
        NEW("NEW"),
        RECURRENT("RECURRENT");

        private String s;

        private Status(String s) {
            this.s = s;
        }

        public String getStatus() {
            return s;
        }

        public void setStatus(String s) {
            this.s = s;
        }
    }

    public enum Severity {
        HIGH("HIGH"),
        MEDIUM("MEDIUM"),
        LOW("LOW"),
        INFO("INFO");

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
