package com.checkmarx.sdk.dto.sast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Sast implements Serializable {

    @JsonProperty("preset")
    private String preset;
    @JsonProperty("engineConfiguration")
    private String engineConfiguration;
    @JsonProperty("incremental")
    private Boolean incremental;
    @JsonProperty("forceScan")
    private Boolean forceScan;
    @JsonProperty("fileExcludes")
    private String fileExcludes;
    @JsonProperty("folderExcludes")
    private String folderExcludes;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 1949448108099095334L;

    @JsonProperty("preset")
    public String getPreset() {
        return preset;
    }

    @JsonProperty("preset")
    public void setPreset(String preset) {
        this.preset = preset;
    }

    @JsonProperty("engineConfiguration")
    public String getEngineConfiguration() {
        return engineConfiguration;
    }

    @JsonProperty("engineConfiguration")
    public void setEngineConfiguration(String engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
    }

    @JsonProperty("incremental")
    public Boolean getIncremental() {
        return incremental;
    }

    @JsonProperty("incremental")
    public void setIncremental(Boolean incremental) {
        this.incremental = incremental;
    }

    @JsonProperty("forceScan")
    public Boolean getForceScan() {
        return forceScan;
    }

    @JsonProperty("forceScan")
    public void setForceScan(Boolean forceScan) {
        this.forceScan = forceScan;
    }

    @JsonProperty("fileExcludes")
    public String getFileExcludes() {
        return fileExcludes;
    }

    @JsonProperty("fileExcludes")
    public void setFileExcludes(String fileExcludes) {
        this.fileExcludes = fileExcludes;
    }

    @JsonProperty("folderExcludes")
    public String getFolderExcludes() {
        return folderExcludes;
    }

    @JsonProperty("folderExcludes")
    public void setFolderExcludes(String folderExcludes) {
        this.folderExcludes = folderExcludes;
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
