package com.checkmarx.sdk.dto;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Osa implements Serializable {

    @JsonProperty("fileExcludes")
    private String fileExcludes;
    @JsonProperty("folderExcludes")
    private String folderExcludes;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -7121161780560758042L;

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
