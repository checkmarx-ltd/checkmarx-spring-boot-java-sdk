
package com.checkmarx.sdk.remotesettings.git;

import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.annotation.Generated;

@Generated("jsonschema2pojo")
public class Gitremotemain {

    private String url;
    private String branch;
    private Boolean useSsh;
    private Link link;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Gitremotemain withUrl(String url) {
        this.url = url;
        return this;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Gitremotemain withBranch(String branch) {
        this.branch = branch;
        return this;
    }

    public Boolean getUseSsh() {
        return useSsh;
    }

    public void setUseSsh(Boolean useSsh) {
        this.useSsh = useSsh;
    }

    public Gitremotemain withUseSsh(Boolean useSsh) {
        this.useSsh = useSsh;
        return this;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Gitremotemain withLink(Link link) {
        this.link = link;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Gitremotemain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
