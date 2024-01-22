
package com.checkmarx.sdk.remotesettings.svn;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Uri {

    private String absoluteUrl;
    private Integer port;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public String getAbsoluteUrl() {
        return absoluteUrl;
    }

    public void setAbsoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
    }

    public Uri withAbsoluteUrl(String absoluteUrl) {
        this.absoluteUrl = absoluteUrl;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Uri withPort(Integer port) {
        this.port = port;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Uri withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
