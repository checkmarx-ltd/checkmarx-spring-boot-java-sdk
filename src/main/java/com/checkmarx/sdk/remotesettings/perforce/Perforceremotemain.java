
package com.checkmarx.sdk.remotesettings.perforce;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Generated;

@Generated("jsonschema2pojo")
public class Perforceremotemain {

    private Uri uri;
    private List<String> paths;
    private String browseMode;
    private Link link;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Perforceremotemain withUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Perforceremotemain withPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public String getBrowseMode() {
        return browseMode;
    }

    public void setBrowseMode(String browseMode) {
        this.browseMode = browseMode;
    }

    public Perforceremotemain withBrowseMode(String browseMode) {
        this.browseMode = browseMode;
        return this;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Perforceremotemain withLink(Link link) {
        this.link = link;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Perforceremotemain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
