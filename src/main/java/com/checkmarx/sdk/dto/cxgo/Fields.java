package com.checkmarx.sdk.dto.cxgo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Fields {

    @JsonProperty("key")
    private String key;
    @JsonProperty("bucket")
    private String bucket;
    @JsonProperty("X-Amz-Algorithm")
    private String xAmzAlgorithm;
    @JsonProperty("X-Amz-Credential")
    private String xAmzCredential;
    @JsonProperty("X-Amz-Date")
    private String xAmzDate;
    @JsonProperty("X-Amz-Security-Token")
    private String xAmzSecurityToken;
    @JsonProperty("Policy")
    private String policy;
    @JsonProperty("X-Amz-Signature")
    private String xAmzSignature;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("bucket")
    public String getBucket() {
        return bucket;
    }

    @JsonProperty("bucket")
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @JsonProperty("X-Amz-Algorithm")
    public String getXAmzAlgorithm() {
        return xAmzAlgorithm;
    }

    @JsonProperty("X-Amz-Algorithm")
    public void setXAmzAlgorithm(String xAmzAlgorithm) {
        this.xAmzAlgorithm = xAmzAlgorithm;
    }

    @JsonProperty("X-Amz-Credential")
    public String getXAmzCredential() {
        return xAmzCredential;
    }

    @JsonProperty("X-Amz-Credential")
    public void setXAmzCredential(String xAmzCredential) {
        this.xAmzCredential = xAmzCredential;
    }

    @JsonProperty("X-Amz-Date")
    public String getXAmzDate() {
        return xAmzDate;
    }

    @JsonProperty("X-Amz-Date")
    public void setXAmzDate(String xAmzDate) {
        this.xAmzDate = xAmzDate;
    }

    @JsonProperty("X-Amz-Security-Token")
    public String getXAmzSecurityToken() {
        return xAmzSecurityToken;
    }

    @JsonProperty("X-Amz-Security-Token")
    public void setXAmzSecurityToken(String xAmzSecurityToken) {
        this.xAmzSecurityToken = xAmzSecurityToken;
    }

    @JsonProperty("Policy")
    public String getPolicy() {
        return policy;
    }

    @JsonProperty("Policy")
    public void setPolicy(String policy) {
        this.policy = policy;
    }

    @JsonProperty("X-Amz-Signature")
    public String getXAmzSignature() {
        return xAmzSignature;
    }

    @JsonProperty("X-Amz-Signature")
    public void setXAmzSignature(String xAmzSignature) {
        this.xAmzSignature = xAmzSignature;
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