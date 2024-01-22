
package com.checkmarx.sdk.remotesettings.custom;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Customremotemain {

    private String path;
    private Integer pullingCommandId;
    private Link link;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Customremotemain withPath(String path) {
        this.path = path;
        return this;
    }

    public Integer getPullingCommandId() {
        return pullingCommandId;
    }

    public void setPullingCommandId(Integer pullingCommandId) {
        this.pullingCommandId = pullingCommandId;
    }

    public Customremotemain withPullingCommandId(Integer pullingCommandId) {
        this.pullingCommandId = pullingCommandId;
        return this;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Customremotemain withLink(Link link) {
        this.link = link;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Customremotemain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
