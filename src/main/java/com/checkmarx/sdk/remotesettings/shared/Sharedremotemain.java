
package com.checkmarx.sdk.remotesettings.shared;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;

@Generated("jsonschema2pojo")
public class Sharedremotemain {

    private List<String> paths;
    private Link link;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Sharedremotemain withPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Sharedremotemain withLink(Link link) {
        this.link = link;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Sharedremotemain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
