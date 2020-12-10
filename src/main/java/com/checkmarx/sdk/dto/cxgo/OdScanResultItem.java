package com.checkmarx.sdk.dto.cxgo;

import com.fasterxml.jackson.annotation.*;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OdScanResultItem {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("queryId")
    private Integer queryId;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("status")
    private String status;

    @JsonProperty("state")
    private Integer state;

    @JsonProperty("sourceNode")
    private String sourceNode;

    @JsonProperty("sourceFile")
    private String sourceFile;

    @JsonProperty("sinkNode")
    private String sinkNode;

    @JsonProperty("sinkFile")
    private String sinkFile;

    @JsonProperty("assignTo")
    private String assignTo;

    @JsonProperty("note")
    private String note;

    @JsonProperty("similarityId")
    private String similarityId;

    @JsonProperty("hasNotes")
    private boolean hasNotes;

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

    @JsonProperty("queryId")
    public Integer getQueryId() {
        return queryId;
    }

    @JsonProperty("queryId")
    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    @JsonProperty("state")
    public Integer getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(Integer state) {
        this.state = state;
    }

    @JsonProperty("hasNotes")
    public boolean getHasNotes() {
        return hasNotes;
    }

    @JsonProperty("hasNotes")
    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("severity")
    public String getSeverity() {
        return severity;
    }

    @JsonProperty("severity")
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("sourceNode")
    public String getSourceNode() {
        return sourceNode;
    }

    @JsonProperty("sourceNode")
    public void setSourceNode(String sourceNode) {
        this.sourceNode = sourceNode;
    }

    @JsonProperty("sourceFile")
    public String getSourceFile() {
        return sourceFile;
    }

    @JsonProperty("sourceFile")
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    @JsonProperty("sinkNode")
    public String getSinkNode() {
        return sinkNode;
    }

    @JsonProperty("sinkNode")
    public void setSinkNode(String sinkNode) {
        this.sinkNode = sinkNode;
    }

    @JsonProperty("sinkFile")
    public String getSinkFile() {
        return sinkFile;
    }

    @JsonProperty("sinkFile")
    public void setSinkFile(String sinkFile) {
        this.sinkFile = sinkFile;
    }

    @JsonProperty("assignTo")
    public String getAssignTo() {
        return assignTo;
    }

    @JsonProperty("assignTo")
    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    @JsonProperty("note")
    public String getNotes() {
        return note;
    }

    @JsonProperty("note")
    public void setNotes(String note) {
        this.note = note;
    }

    @JsonProperty("similarityId")
    public String getSimilarityId() {
        return similarityId;
    }

    @JsonProperty("similarityId")
    public void setSimilarityId(String similarityId) {
        this.similarityId = similarityId;
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