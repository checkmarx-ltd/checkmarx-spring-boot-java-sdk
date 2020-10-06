package com.checkmarx.sdk.dto.od;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultNode {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("line")
    private Integer line;
    @JsonProperty("column")
    private Integer column;
    @JsonProperty("length")
    private Integer length;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("file_path")
    private String filePath;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("line")
    public Integer getLine() {
        return line;
    }

    @JsonProperty("line")
    public void setLine(Integer line) {
        this.line = line;
    }

    @JsonProperty("column")
    public Integer getColumn() {
        return column;
    }

    @JsonProperty("column")
    public void setColumn(Integer column) {
        this.column = column;
    }

    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    @JsonProperty("length")
    public void setLength(Integer length) {
        this.length = length;
    }

    @JsonProperty("file_name")
    public String getFileName() {
        return fileName;
    }

    @JsonProperty("file_name")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("file_path")
    public String getFilePath() {
        return filePath;
    }

    @JsonProperty("file_path")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
