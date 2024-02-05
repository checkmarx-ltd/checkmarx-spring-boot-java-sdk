
package com.checkmarx.sdk.remotesettings.svn;

import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.annotation.Generated;

@Generated("jsonschema2pojo")
public class Link {

    private String rel;
    private String uri;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public Link withRel(String rel) {
        this.rel = rel;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Link withUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Link withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
