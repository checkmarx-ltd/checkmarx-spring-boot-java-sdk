package com.checkmarx.sdk.dto.cx;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.Valid;

@JsonPropertyOrder({
        "url",
        "branch",
        "privateKey",
        "useSsh",
        "link"
})
public class CxProjectSource {
    @JsonProperty("url")
    public String url;
    @JsonProperty("branch")
    public String branch;
    @JsonProperty("privateKey")
    public String privateKey;
    @JsonProperty("useSsh")
    public Boolean useSsh;
    @JsonProperty("link")
    @Valid
    public Link link;

    @java.beans.ConstructorProperties({"url", "branch", "privateKey", "useSsh", "link"})
    CxProjectSource(String url, String branch, String privateKey, Boolean useSsh, Link link) {
        this.url = url;
        this.branch = branch;
        this.privateKey = privateKey;
        this.useSsh = useSsh;
        this.link = link;
    }

    public static CxProjectSourceBuilder builder() {
        return new CxProjectSourceBuilder();
    }

    public String getUrl() {
        return this.url;
    }
    
    public String getPrivateKey() {
        return this.privateKey;
    }

    public String getBranch() {
        return this.branch;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Boolean getUseSsh() {
        return this.useSsh;
    }

    public void setUseSsh(Boolean useSsh) {
        this.useSsh = useSsh;
    }

    public Link getLink() {
        return this.link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String toString() {
        return "CxProjectSource(url=" + this.getUrl() + ", branch=" + this.getBranch() + ", privateKey=" + this.getPrivateKey() + ", useSsh=" + this.getUseSsh() + ")";
    }

    public static class CxProjectSourceBuilder {
        private String url;
        private String branch;
        private String privateKey;
        private Boolean useSsh;
        private Link link;

        CxProjectSourceBuilder() {
        }

        public CxProjectSource.CxProjectSourceBuilder url(String url) {
            this.url = url;
            return this;
        }

        public CxProjectSource.CxProjectSourceBuilder branch(String branch) {
            this.branch = branch;
            return this;
        }

        public CxProjectSource.CxProjectSourceBuilder useSsh(Boolean useSsh) {
            this.useSsh = useSsh;
            return this;
        }

        public CxProjectSource.CxProjectSourceBuilder privateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public CxProjectSource build() {
            return new CxProjectSource(url, branch, privateKey, useSsh, link);
        }

        public String toString() {
            return "CxProjectSource.CxProjectSourceBuilder(url=" + this.url + ", branch=" + this.branch + ")";
        }
    }

    public static class Link {

        @JsonProperty("rel")
        public String rel;
        @JsonProperty("uri")
        public String uri;

    }
}