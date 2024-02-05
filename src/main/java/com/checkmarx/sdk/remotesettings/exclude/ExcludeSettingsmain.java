
package com.checkmarx.sdk.remotesettings.exclude;

import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.annotation.Generated;

@Generated("jsonschema2pojo")
public class ExcludeSettingsmain {

    private Integer projectId;
    private String excludeFoldersPattern;
    private String excludeFilesPattern;
    private Link link;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public ExcludeSettingsmain withProjectId(Integer projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getExcludeFoldersPattern() {
        return excludeFoldersPattern;
    }

    public void setExcludeFoldersPattern(String excludeFoldersPattern) {
        this.excludeFoldersPattern = excludeFoldersPattern;
    }

    public ExcludeSettingsmain withExcludeFoldersPattern(String excludeFoldersPattern) {
        this.excludeFoldersPattern = excludeFoldersPattern;
        return this;
    }

    public String getExcludeFilesPattern() {
        return excludeFilesPattern;
    }

    public void setExcludeFilesPattern(String excludeFilesPattern) {
        this.excludeFilesPattern = excludeFilesPattern;
    }

    public ExcludeSettingsmain withExcludeFilesPattern(String excludeFilesPattern) {
        this.excludeFilesPattern = excludeFilesPattern;
        return this;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public ExcludeSettingsmain withLink(Link link) {
        this.link = link;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public ExcludeSettingsmain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
