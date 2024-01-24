
package com.checkmarx.sdk.remotesettings.svn;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Svnremotemain {

    private Uri uri;
    private List<String> paths;
    private Boolean useSsh;
    private Link link;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Svnremotemain withUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Svnremotemain withPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public Boolean getUseSsh() {
        return useSsh;
    }

    public void setUseSsh(Boolean useSsh) {
        this.useSsh = useSsh;
    }

    public Svnremotemain withUseSsh(Boolean useSsh) {
        this.useSsh = useSsh;
        return this;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Svnremotemain withLink(Link link) {
        this.link = link;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Svnremotemain withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}
