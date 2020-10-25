package com.checkmarx.sdk.config;

import com.checkmarx.sdk.utils.ScanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = CxGoProperties.CONFIG_PREFIX)
@Validated
public class CxGoProperties extends CxPropertiesBase {

    public static final String CONFIG_PREFIX = "cxgo";
    
    private String postCloneScript;
    private List<String> engineTypes = Arrays.asList(CxProperties.CONFIG_PREFIX.toUpperCase(),
                                                     ScaProperties.CONFIG_PREFIX.toUpperCase());

    public Boolean getEnableOsa() {
        return false;
    }
    
    public List<String> getEngineTypes() {
        return engineTypes;
    }

    public void setEngineTypes(List<String> engineTypes) {
        this.engineTypes = engineTypes;
    }
    
    public String getPostCloneScript() {
        return postCloneScript;
    }

    public void setPostCloneScript(String postCloneScript) {
        this.postCloneScript = postCloneScript;
    }
    
 
}

